# grammar0 编译前端全链路说明文档

本文说明 `ProgramSyntaxDemo` 所串联起来的一整条编译前端链路。文档面向不直接阅读源码的人，因此会尽量用语言解释每个阶段“做什么、为什么这样做、数据如何流动、最终产生什么结果”。文中只描述当前项目中已经实现或已经接入的能力，不额外假设不存在的功能。

本文涉及的主线是：

```text
测试程序文本
  -> 词法分析，得到 token 流
  -> 语法分析，依据 grammar0 构造并使用移进归约表
  -> 语义分析，随移进/归约事件构建语法树、符号表和语义命令
  -> 输出语义命令序列
```

## 1. 总入口：ProgramSyntaxDemo

`ProgramSyntaxDemo` 是这条链路的总入口。它不是只测试某一个局部模块，而是把词法分析、语法分析和语义分析串在一起。

它主要做四件事：

1. 准备测试输入文本。当前测试用例放在 `src/main/resources/program-tests` 目录下，默认入口会批量读取其中的 `text1.txt`、`text2.txt`、`text3.txt` 并逐个运行。
2. 调用程序语言专用的词法分析器，把字符串包装成字符资源，再变成 token 迭代器。
3. 构造 `grammar0` 文法，并生成或加载移进归约分析表。
4. 创建移进归约分析器，并把语义回调注册进去，让语法分析过程中同步触发语义处理。

从职责上看，`ProgramSyntaxDemo` 是一个“集成测试式”的 demo：它本身不承担复杂算法，而是把各个阶段装配起来。

当前主流程可以用下面的概要表示：

```text
读取 program-tests 目录下的测试用例
创建词法分析器
把当前用例文本包装成 Resource
从 Resource 创建 SourceTokenIterator
构造 grammar0 的 ProductionSetContext
构造或读取 ShiftReduceParsingTable
创建 ShiftReducePhaserImpl
注册语义 callback
执行 phase
生成控制台摘要和 Markdown 报告
```

这里最关键的设计点是：语法分析不是单独运行的。移进和归约发生时，会不断通知语义层，所以语义分析和语法分析是事件驱动式地交织在一起完成的。

## 2. 测试输入 text1、text2、text3

当前 demo 中准备了三个测试用例。

`text1` 是综合覆盖型输入。它包含：

- 基本变量声明，例如 `int32 i;`
- 一维数组声明，例如 `int32[10] arr;`
- 布尔变量声明，例如 `boolean flag;`
- 算术表达式，例如 `3 + 4 * 6`
- 数组访问和数组赋值，例如 `arr[1] = i - 2;`
- `if ... else ...`
- `while`
- `do ... while`
- `break`
- 逻辑表达式，例如 `!(i >= 10) && true`

`text2` 主要用于测试悬挂 `if`。它包含嵌套的：

```text
if (a < b)
    if (b < 10)
        a = a + b;
    else
        b = b - a;
```

这个例子用于验证 `else` 是否绑定到最近的未匹配 `if`。在 `grammar0` 中，这个问题不是靠语义阶段修补，而是靠文法本身通过 `matched_stmt` 和 `unmatched_stmt` 拆分解决。

`text3` 是更复杂的综合测试。它覆盖：

- 多维数组声明，例如 `int32[2][3] matrix;`
- 多维数组访问，例如 `matrix[row][col]`
- `float64` 声明和浮点常量
- `while` 中的复合布尔条件
- 匹配完整的 `if ... else`
- `do { ... } while (...)` 形式

这三个测试并不是随意拼接的字符串，而是有意识覆盖了 `grammar0` 中的重要结构。

### 2.1 批量测试与报告输出

当前默认运行方式不是手动切换某一个字符串，而是由 `ProgramSyntaxTestRunner` 批量读取 `src/main/resources/program-tests` 目录下的 `.txt` 文件。每个测试用例都会独立走完词法分析、语法分析和语义分析全链路。

运行后会产生两类输出：

- 控制台只打印简洁结果，例如每个用例的 `PASS` 或 `FAIL`，便于人直接确认整体状态。
- `run-reports/program-syntax` 目录下会生成 Markdown 报告。每个用例有一份详细报告，包含源程序、完整运行输出和失败信息；同一次运行还会生成一份 `summary` 报告，集中列出本轮所有用例的结果和对应报告路径。

这种组织方式把“适合快速阅读的摘要”和“适合排查问题的完整日志”分开了。平时先看控制台或 summary，出现问题时再进入单个报告查看完整链路输出。

## 3. 词法分析阶段

词法分析阶段的目标是把源程序字符串切分成一个个 token。比如：

```text
int32 i;
```

会被识别成类似下面的 token 序列：

```text
TYPE_INT32
IDENTIFIER
OPERATOR_SEMICOLON
```

词法阶段不理解语句是否合法，也不关心 `if` 是否匹配 `else`。它只关心“字符序列应该被识别成什么 token”。

### 3.1 Token 类型设计

项目中用 `ProgramTokenType` 枚举描述程序语言支持的 token 类型。它包含几类：

第一类是辅助 token：

- `IDENTIFIER`：标识符
- `SPACE`：空白字符
- `COMMENT_BLOCK`：块注释
- `COMMENT_LINE`：行注释

第二类是常量：

- `CONSTANT_STRING`
- `CONSTANT_CHARACTER`
- `CONSTANT_INTEGER`
- `CONSTANT_FLOAT`
- `CONSTANT_BOOLEAN_TRUE`
- `CONSTANT_BOOLEAN_FALSE`

第三类是类型关键字：

- `TYPE_BOOLEAN`
- `TYPE_CHARACTER`
- `TYPE_INT32`
- `TYPE_FLOAT64`
- `TYPE_STRING`

第四类是运算符和界符：

- 算术运算符：`+`、`-`、`*`、`/`
- 逻辑运算符：`!`、`&&`、`||`
- 比较运算符：`==`、`!=`、`<`、`<=`、`>`、`>=`
- 赋值和分隔符：`=`、`;`
- 括号：`(`、`)`、`[`、`]`、`{`、`}`

第五类是控制结构关键字：

- `if`
- `else`
- `while`
- `do`
- `break`
- `continue`

每个 token 类型都带有一个优先级。这个优先级用于处理词法匹配中的冲突。例如，`if` 从字符组成上也符合标识符规则，但它应该被识别为关键字，而不是普通变量名。

### 3.2 词法规则如何描述

词法规则在 `ProgramLexicalDemo` 中用一组正则模式描述。每条规则都对应一个 token 类型。例如：

- 字母或下划线开头，后面接字母、数字或下划线，是标识符。
- 一个或多个空白字符，是空白 token。
- `/* ... */` 是块注释。
- `// ...` 到换行，是行注释。
- 双引号包围的是字符串常量。
- 单引号包围的是字符常量。
- 数字组成的是整数常量或浮点常量。
- `true` 和 `false` 是布尔常量。
- `int32`、`float64` 等是类型关键字。
- `if`、`else`、`while` 等是控制关键字。

这些规则不是在切词时逐条手写判断，而是先统一转换成 DFA 状态表，然后由状态表驱动扫描。

### 3.3 正则到 DFA 的构建链路

词法表构造由 `DefaultLexicalDirector` 负责。它把“正则规则列表”转换成一个可执行的 DFA 状态表。

完整过程是：

```text
LexicalPattern 列表
  -> 正则表达式语法树
  -> NFA
  -> DFA
  -> 最小化 DFA
  -> RegexDfaStatusTable
```

每一步的含义如下：

1. 正则表达式语法树：把字符串形式的正则规则解析成内部树结构。
2. NFA：把正则树转换成非确定有限自动机。
3. DFA：把 NFA 转换成确定有限自动机，这样扫描字符时每一步只有唯一目标状态。
4. 最小化 DFA：合并等价状态，减少表大小。
5. 状态表：最终运行时只需要查表推进，不需要重新理解正则。

这个设计的亮点是：词法规则和运行时扫描逻辑分离。规则复杂度被集中在表构造阶段，运行时只做稳定的状态跳转。

### 3.4 运行时如何切 token

运行时的 token 迭代器是 `StatusTableTokenIterator`。

它内部维护：

- 当前 DFA 状态
- 当前正在收集的词素，也就是 token 的原始字符
- 当前读取位置 offset
- 错误上下文

扫描过程可以理解为：

```text
从起始状态开始
读取一个字符
根据字符和当前状态查 DFA 表
如果可以转移，就追加字符并进入下一个状态
如果不能转移，就尝试把当前词素切成一个 token
切分成功后，状态回到起始状态
继续扫描后续字符
```

这里采用的是最长匹配策略。也就是说，只要还能继续走 DFA，就不会急着切 token。直到下一步无法继续转移时，才把目前已经读到的最长合法前缀作为 token。

如果遇到不能识别的字符，会向错误上下文记录词法错误。如果当前词素无法构成合法 token，会记录“未完成 token”一类错误，并中断当前编译流程。

### 3.5 空白和注释的处理

空白和注释在词法阶段会被识别成 token，但语法阶段不会使用它们。

`ProgramSyntaxDemo` 中定义了一个过滤集合：

```text
SPACE
COMMENT_LINE
COMMENT_BLOCK
```

移进归约分析器拿到 token 迭代器时，会通过过滤条件跳过这些 token。这样做的好处是词法阶段仍然保留完整识别能力，而语法阶段只面对真正参与文法推导的 token。

## 4. grammar0 文法设计

`grammar0` 是这条链路中语法分析使用的文法。它描述的是一个小型类 C 语言片段。

它支持：

- 由大括号包裹的程序块
- 声明语句
- 数组类型
- 赋值语句
- `if`
- `if ... else`
- `while`
- `do ... while`
- `break`
- 数组访问
- 算术表达式
- 比较表达式
- 逻辑表达式
- 布尔常量、整数常量、浮点常量

### 4.1 程序和块

文法入口是：

```text
program ::= block
```

也就是说，完整程序必须是一个代码块。

代码块结构是：

```text
block ::= { decls stmts }
```

一个块由三部分组成：

1. 左大括号 `{`
2. 声明列表 `decls`
3. 语句列表 `stmts`
4. 右大括号 `}`

这个设计决定了：声明必须出现在语句之前。比如：

```text
{
    int32 a;
    a = 1;
}
```

是符合文法的，而声明夹在语句之后是否可行，要看文法是否允许。当前 `grammar0` 的结构要求先声明后语句。

### 4.2 声明列表和语句列表

声明列表：

```text
decls ::= decls decl
        | ε
```

语句列表：

```text
stmts ::= stmts stmt
        | ε
```

这两个列表都是左递归结构，并且都允许为空。

这说明：

- 一个块中可以没有声明。
- 一个块中可以没有语句。
- 一个块中可以有多个声明。
- 一个块中可以有多个语句。

使用左递归也说明这套文法不是面向 LL 预测分析器设计的，而是面向移进归约分析器设计的。

### 4.3 类型和数组类型

声明语句是：

```text
decl ::= type id ;
```

类型是：

```text
type ::= type [ num ]
       | basic
```

在代码构造中，`basic` 被展开成具体 token，例如：

```text
boolean
char
int32
float64
string
```

数组类型通过递归构造。例如：

```text
int32[10]
int32[2][3]
```

都可以由 `type ::= type [ num ]` 推导出来。

### 4.4 语句拆分：matched_stmt 与 unmatched_stmt

这是 `grammar0` 中最重要的设计之一。

普通写法中，`if` 语句容易出现悬挂 `else` 问题。例如：

```text
if (a)
    if (b)
        x = 1;
    else
        x = 2;
```

问题是：`else` 应该属于外层 `if`，还是内层 `if`？

在多数语言中，`else` 绑定最近的尚未匹配的 `if`。`grammar0` 通过把语句拆成两类来表达这个规则：

```text
stmt ::= matched_stmt
       | unmatched_stmt
```

`matched_stmt` 表示已经完整匹配的语句。比如：

- 普通赋值语句
- `while` 后面接完整匹配语句
- `do ... while`
- `break`
- 块语句
- 完整的 `if (...) matched_stmt else matched_stmt`

`unmatched_stmt` 表示还可能继续接收 `else` 的语句。比如：

- `if (...) stmt`
- `if (...) matched_stmt else unmatched_stmt`
- `while (...) unmatched_stmt`

通过这个拆分，文法本身就消除了悬挂 `if` 的歧义。语义阶段不需要猜测 `else` 归属，语法树结构已经确定。

### 4.5 赋值和左值

赋值语句在 `grammar0` 中是：

```text
matched_stmt ::= loc = bool ;
```

这里右侧不是单纯的算术表达式，而是 `bool`。这意味着赋值右侧可以是：

- 算术表达式
- 比较表达式
- 逻辑表达式
- 布尔常量
- 数组访问
- 带括号的表达式

左值 `loc` 是：

```text
loc ::= loc [ bool ]
      | id
```

这使得数组访问可以递归出现。例如：

```text
a
arr[1]
matrix[row][col]
```

注意，下标表达式使用的是 `bool`，所以当前文法允许更宽泛的表达式作为数组下标。语义阶段当前主要生成命令，并没有在这里强制做类型检查。

### 4.6 表达式优先级

表达式分层如下：

```text
bool     ::= bool || join | join
join     ::= join && equality | equality
equality ::= equality == rel | equality != rel | rel
rel      ::= expr < expr | expr <= expr | expr >= expr | expr > expr | expr
expr     ::= expr + term | expr - term | term
term     ::= term * unary | term / unary | unary
unary    ::= ! unary | - unary | factor
factor   ::= ( bool ) | loc | num | real | true | false
```

这套层次表达了常见优先级：

1. 括号、变量、常量优先级最高。
2. 一元运算 `!` 和负号高于乘除。
3. 乘除高于加减。
4. 加减高于关系比较。
5. 关系比较高于相等/不等。
6. `&&` 高于 `||`。

由于许多层使用左递归，所以像：

```text
a - b - c
```

会自然按左结合处理。

## 5. 语法表构造阶段

`ProgramSyntaxDemo` 调用 `SyntaxDemo.buildShiftReduceParsingTable("program", context, "syntax_table.data")` 构造或加载语法分析表。

这里的 `context` 是 `buildGrammar0()` 生成的文法上下文，包含所有非终结符、终结符和产生式。

### 5.1 ProductionSetContext

`ProductionSetContext` 可以理解为文法的内部表示。

它保存：

- 每个非终结符的定义
- 每个非终结符有哪些候选产生式
- 终结符和 token 类型之间的映射
- 根据非终结符名称查找定义的能力

`buildGrammar0()` 并不是解析 `grammar0.txt` 文件来建文法，而是用 builder API 手动搭建出等价结构。

这种方式的特点是：

- 文法结构直接由 Java 代码构造。
- 终结符直接绑定到 `ProgramTokenType`。
- 后续语法表构造可以直接使用对象结构，不需要再解析文本文件。

### 5.2 FIRST 集计算

语法表构造第一步是计算 FIRST 集。

FIRST 集表示：某个符号或符号串开头可能出现哪些终结符。

例如，如果有：

```text
stmt ::= matched_stmt | unmatched_stmt
```

那么 `stmt` 的 FIRST 集要综合 `matched_stmt` 和 `unmatched_stmt` 的 FIRST 集。

当前项目使用迭代不动点算法计算 FIRST 集。

它的大致思路是：

```text
先创建所有非终结符的 FIRST 集
把所有终结符作为可加入元素
反复扫描所有产生式
根据产生式右部更新左部 FIRST 集
如果某轮扫描没有任何变化，计算结束
```

这种写法便于反复收敛，对当前这种包含左递归和空产生式的文法比较友好。

### 5.3 项目集族构造

语法分析使用的是移进归约方法，因此需要构造 LR 项目集族。

一个项目可以理解为“产生式右侧某个位置有一个点”。例如：

```text
expr -> expr · + term
```

表示现在已经识别出了一个 `expr`，接下来期待看到 `+ term`。

项目集族构造包含两个核心操作：

1. closure：如果点后面是非终结符，就把这个非终结符的候选产生式也加入当前项目集。
2. goto：如果点后面是某个符号，就沿着这个符号把点向后移动，形成新的项目集。

构造流程是：

```text
从开始产生式建立初始项目集
对初始项目集做 closure
把项目集放入队列
不断取出项目集
对其中每种可前进符号执行 goto
对 goto 结果做 closure
如果得到新项目集，就加入项目集族和队列
直到没有新项目集
```

这个过程最终得到语法分析器的“状态集合”。后续移进归约时，状态栈里保存的就是这些项目集状态编号。

### 5.4 Lookahead 传播

如果只用 LR(0) 项目，很多文法会出现归约冲突。当前项目进一步计算 lookahead 集来参与归约判断。

lookahead 集表示：某个归约项目在哪些终结符向前看符号下可以归约。

简单说，它回答的问题是：

```text
当我已经识别出 A -> α 时，看到当前输入 token 是 t，我能不能把 α 归约成 A？
```

当前实现会：

- 初始化每个项目的 lookahead 集。
- 根据项目之间的关系建立传播边。
- 在传播图上不断传递终结符集合。
- 最后只保留已经到达产生式末尾的项目的 lookahead 集。

这一步对处理复杂文法很重要。它让归约动作不是“看到产生式结束就随便归约”，而是必须结合当前输入符号判断是否应该归约。

### 5.5 ACTION 表和 GOTO 表

有了项目集族和 lookahead 后，就可以构造移进归约表。

表分为两部分：

```text
ACTION[state, terminal]
GOTO[state, non_terminal]
```

ACTION 表负责终结符：

- 如果项目点后面是终结符，并且 goto 到另一个状态，则填入 shift。
- 如果项目已经到末尾，并且当前输入符号在 lookahead 集中，则填入 reduce。
- 如果是开始产生式识别完成，并且看到输入结束符，则填入 accept。

GOTO 表负责非终结符：

- 如果当前状态在某个非终结符上有 goto 目标，则填入目标状态。

如果 ACTION 表某个格子已经有动作，再填入另一个冲突动作，构造器会报错。这说明当前文法不适合当前这套表构造方式，或者需要调整文法。

### 5.6 语法表序列化

语法表可以写入 `syntax_table.data`，也可以从这个文件加载。

这带来两个效果：

1. 构造表的过程可以持久化，避免每次都从头计算。
2. 表中仍然保存产生式池，语法分析阶段可以通过表里的产生式编号执行 reduce 动作。

需要注意的是，产生式池编号不应被当成长期稳定的语义绑定依据。当前语义分析已经改为根据完整产生式文本选择 translator，而不是依赖 `grammar0_pool.txt` 中的数字编号。这样即使重新生成语法表导致产生式编号变化，只要产生式本身没有变，对应语义动作仍然可以匹配。

## 6. 移进归约运行阶段

语法表准备好之后，真正执行分析的是 `ShiftReducePhaserImpl`。

它维护一个状态栈，并不断读取当前 token。

运行逻辑如下：

```text
状态栈初始化为起始状态
查看当前 token
取状态栈栈顶状态
查询 ACTION[栈顶状态, 当前 token]

如果是 shift：
    把新状态压栈
    消耗当前 token

如果是 reduce：
    根据产生式右部长度弹出若干状态
    查看弹栈后的栈顶状态
    查询 GOTO[栈顶状态, 产生式左部]
    把 GOTO 状态压栈

如果是 accept：
    检查输入是否结束
    检查栈状态是否合法
    成功结束

如果没有动作：
    触发语法错误
```

### 6.1 token 过滤

语法分析器使用的 token 迭代器外面包了一层过滤逻辑。空白和注释不会进入语法表匹配。

这样可以让词法阶段保留完整 token 信息，同时语法阶段保持文法简洁。

### 6.2 错误处理

如果 ACTION 表查不到动作，会触发语法错误。

项目中注册了 `PassiveErrorCallback`，它的策略是：出现语法错误后抛出异常，中断分析。

这说明当前 demo 的错误恢复不是重点，它更偏向于“发现错误并停止”，适合教学和验证。

## 7. 语义分析阶段总体设计

语义分析不是在语法分析结束后一次性遍历树完成的，而是在移进归约过程中同步发生。

这套设计基于 callback：

```text
onStart
onShift
onReduce
beforeAccept
onAccept
onError
```

每当语法分析器 shift 或 reduce，就会通知所有注册的语义 callback。不同 callback 负责不同事情。

当前注册的 callback 包括：

1. 构建语法树。
2. 打印语法树日志。
3. 维护作用域。
4. 构建符号表。
5. 被动错误处理。
6. 打印语义命令。
7. 执行语法制导翻译，生成语义命令节点。

这个设计的最大特点是解耦。语法分析器只负责 shift/reduce/accept，不直接知道“如何建符号表”或“如何生成命令”。这些工作都由 callback 插入。

## 8. 语法树构建

语法树构建由 `TreeBuildCallback` 完成。

它维护一棵树对应的栈。

在 shift 时：

```text
把当前 token 包装成 TokenNode
压入语法树栈
```

在 reduce 时：

```text
根据产生式右部长度弹出若干节点
用产生式左部创建 HeadNode
把弹出的节点作为 HeadNode 的子节点
再把 HeadNode 压回语法树栈
```

例如，看到：

```text
loc -> IDENTIFIER
```

归约时会把 `IDENTIFIER` 对应的 token 节点弹出，然后构造一个 `loc` 头节点。

由于语法树构建跟 shift/reduce 同步，所以最终 accept 时，语法树栈顶部就是完整程序的树结构。

## 9. 作用域和符号表

### 9.1 作用域进入和退出

当前语言中，大括号块会创建作用域。

作用域 callback 的规则是：

- 当移进 `{` 时，进入一个新作用域。
- 当 `block` 完成归约时，退出当前作用域。

退出作用域时，会把当前作用域中注册过的标识符记录挂到对应的 block 节点上。

这种方式把作用域和语法结构对齐：每个 `{ ... }` block 都自然对应一个符号表区域。

### 9.2 标识符声明

声明语句形如：

```text
type id ;
```

当声明语句完成归约后，符号表 callback 会：

1. 找到声明中的标识符 token。
2. 检查当前作用域是否已经有同名标识符。
3. 如果没有，就创建一条符号记录。
4. 记录内容包括编号、类型节点、词素和是否初始化。

当前 `grammar0` 的声明语句没有初始化形式，所以对应 translator 使用的是“无初始化声明”处理。

### 9.3 标识符使用

当 `lvalue` 归约时，符号表 callback 会把它看作一次标识符使用。

它会从当前作用域向外层作用域查找：

```text
当前作用域
  -> 外层作用域
  -> 更外层作用域
```

如果找不到，就记录语义错误。

如果找到了，会把该标识符对应的编号写回语法树中的 token 节点。这样后续语义处理可以知道这个变量对应的是符号表中的哪一条记录。

## 10. 语义命令生成

语义命令生成由 `CommandBuildCallback` 完成。

它也维护一套栈，但栈里不是语法树节点，而是命令节点注册器。

### 10.1 shift 时的 token 翻译

当 token 被 shift 时，会根据 token 类型生成基础命令。

当前策略是：

- 标识符生成“加载标识符引用”的命令。
- 整数、浮点、字符、字符串、布尔常量生成“加载静态值”的命令。
- `break` 生成一个目标标签暂未确定的跳转命令。
- `continue` 在语义框架里也有对应的未定标签跳转处理入口，但 `grammar0` 当前没有 `continue` 语句产生式。
- 其他 token 默认不生成命令。

需要说明的是，`continue` 在词法层和语义辅助层已经有对应处理入口，但当前 `grammar0` 文法里没有定义 `continue` 语句产生式，所以它更像是框架预留能力，而不是 grammar0 已经可直接接受的语法。

这里的“加载引用”和“加载值”区别很重要：

- 左值需要引用，因为赋值要写回变量位置。
- 常量表达式需要值，因为它参与运算。

### 10.2 reduce 时的产生式翻译

当发生 reduce 时，语义层会拿到本次归约使用的完整产生式，然后根据产生式文本找到对应的 translator。

例如：

- 赋值产生式使用赋值 translator。
- 数组访问产生式使用数组访问 translator。
- 算术、比较、逻辑二元表达式使用中缀表达式 translator。
- `if` 使用 if translator。
- `if ... else` 使用 if-else translator。
- `while` 使用 while translator。
- `do ... while` 使用 do-while translator。

这就是 `reduceStrategies0()` 的作用：它把 grammar0 中需要特殊语义动作的产生式文本和具体语义处理器一一绑定。

如果一个产生式不需要特殊语义动作，就使用简单收缩 translator 或空操作 translator。

## 11. 主要语义 translator 的行为

### 11.1 赋值语句

赋值语句形如：

```text
loc = bool ;
```

语义命令生成顺序是：

```text
生成 loc 的命令
生成 bool 的命令
生成 assign_from_st_top_to_ref
```

含义是：

1. 先把左值引用加载到栈上。
2. 再计算右侧表达式，把值加载到栈上。
3. 最后把栈顶值赋给栈中的引用。

### 11.2 数组访问

数组访问形如：

```text
loc [ bool ]
```

语义命令生成顺序是：

```text
生成 loc 的命令
生成下标表达式 bool 的命令
生成 bias_from_st_top_to_ref
```

含义是：

1. 先得到数组或数组元素的引用。
2. 再计算下标表达式。
3. 用下标对引用做偏移，得到新的引用。

多维数组访问会通过递归多次应用这个规则。

### 11.3 二元表达式

对于形如：

```text
expr + term
term * unary
equality == rel
bool || join
```

这类产生式使用统一的中缀表达式 translator。

命令生成顺序是：

```text
生成左操作数命令
生成右操作数命令
生成对应运算命令
```

例如 `a + b` 的含义是：

1. 加载 `a`
2. 加载 `b`
3. 执行 `st_plus`

当前已经映射的运算包括：

- `logical_or`
- `logical_and`
- `equal`
- `not_equal`
- `less`
- `less_equal`
- `greater`
- `greater_equal`
- `plus`
- `minus`
- `multiply`
- `divide`

### 11.4 if 语句

不带 else 的 if 结构是：

```text
if (bool) stmt
```

生成命令的思路是：

```text
计算条件 bool
如果条件为假，跳转到 if 结束标签
生成 stmt 命令
放置 if 结束标签
```

也就是：

```text
bool.command
ifn_goto L_end
stmt.command
L_end:
```

这里的 `ifn_goto` 可以理解为“如果条件不成立就跳转”。

### 11.5 if-else 语句

完整 if-else 结构是：

```text
if (bool) matched_stmt else matched_stmt
```

或 unmatched 版本中的对应形式。

语义上应该表达：

```text
如果条件为假，跳到 else 分支
否则执行 then 分支
then 分支执行完后跳到整个 if-else 结束
else 分支执行
结束标签
```

当前 translator 代码里创建了 else 起始标签和 else 结束标签，并把 then 分支、else 分支按顺序组织进命令节点。

需要注意：当前代码中可以看到 `elseEndLabel` 被创建并放置为标签，但显式跳过 else 分支的 `goto elseEndLabel` 命令没有出现在当前 translator 的实际命令构造中。文档这里只描述已经实现的结构，不把未出现的跳转命令当成已实现能力。

### 11.6 while 语句

while 结构是：

```text
while (bool) stmt
```

生成命令的思路是：

```text
L_start:
计算条件 bool
如果条件为假，跳到 L_end
执行循环体 stmt
跳回 L_start
L_end:
```

同时，while translator 会把：

- `break` 的目标回填为 `L_end`
- `continue` 的目标回填为 `L_start`

这就是循环中 `break` 和 `continue` 正常工作的基础。

### 11.7 do-while 语句

do-while 结构是：

```text
do stmt while (bool);
```

生成命令的思路是：

```text
L_start:
执行 stmt
L_before_test:
计算条件 bool
如果条件为真，跳回 L_start
L_end:
```

同时，do-while translator 会把：

- `break` 的目标回填为 `L_end`
- `continue` 的目标回填为 `L_before_test`

这和 while 不同，因为 do-while 的 `continue` 应该跳到条件测试处，而不是直接跳到循环体开头。

### 11.8 break 和 continue 的延迟回填

`break` 和 `continue` 在 shift 时并不知道自己属于哪个循环，也不知道目标标签是什么。

因此当前设计是：

1. shift 到 `break` 或 `continue` 时，创建一个“未定标签跳转命令”。
2. 把这个未定命令登记到语义上下文。
3. 当外层循环语句完成归约时，由 while 或 do-while translator 设置真实标签。

这个设计适合嵌套结构，因为跳转目标要由最近的循环结构决定。

## 12. 语义命令输出

`SemanticCommandPrintCallback` 在接受完成前检查命令栈顶部的程序节点，并把命令节点展开成线性命令序列打印。

当前输出分为三类。

第一类是 `command result`。它按生成顺序给每条语义命令编号，便于观察语义翻译最终得到的线性命令序列。

第二类是 `semantic command view`。它仍然展示同一批 demo 语义命令，只是把命令按行整理成更容易阅读的视图。这里需要特别说明：它不是严格意义上的完整三地址码或四元式生成结果，也不是机器码。

第三类是 `symbol table result`。它把语义阶段登记过的标识符记录打印出来，包括编号、类型、标识符名称以及是否已经初始化。

打印出来的命令用于展示语义翻译结果，例如：

```text
load_st_identifier_reference i
load_st_static 3
st_plus
assign_from_st_top_to_ref
ifn_goto L
goto L
```

这些命令表达的是抽象语义动作：

- 加载变量引用
- 加载常量
- 执行栈顶运算
- 赋值
- 条件跳转
- 无条件跳转
- 标签位置

因此，本文把这部分称为“语义命令输出”或“语义命令视图”，而不是完整的三地址码生成器。这样描述更符合当前代码的实际能力。

## 13. grammar0_pool 与 reduceStrategies0 的关系

`grammar0_pool.txt` 是语法分析阶段的产生式池。每一行都有一个编号，例如：

```text
36 : matched_stmt -> loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON
37 : loc -> loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE
38 : bool -> bool OPERATOR_LOGICAL_OR join
...
```

这个文件适合用来观察语法分析表里“编号和产生式”的对应关系，也方便排查某次 reduce 动作到底归约了哪一条产生式。但是在当前实现中，语义阶段不再把这些编号作为长期绑定依据。

当前 `reduceStrategies0()` 做的事情是按产生式文本匹配 translator，例如：

```text
matched_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON
  -> AssignStatementTranslator

loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE
  -> ArrayAtExpressionTranslator

bool->bool OPERATOR_LOGICAL_OR join
  -> InSuffixExpressionTranslator(logical_or)
...
```

这样设计的原因是：产生式池编号会受到语法表生成顺序影响，不适合作为语义动作的稳定键。改成按产生式文本匹配后，如果重新生成 `syntax_table.data` 只改变编号、不改变产生式内容，语义处理器仍然可以正确对应。

维护者仍然需要注意：如果 grammar0 的产生式内容本身发生变化，例如非终结符名称、右部符号顺序或 token 名称发生变化，`reduceStrategies0()` 中对应的文本键也需要同步调整。

## 14. 全链路运行示例说明

以一个简化输入为例：

```text
{
    int32 i;
    i = 3 + 4;
}
```

完整链路如下。

第一步，词法分析：

```text
{
int32
i
;
i
=
3
+
4
;
}
```

会被识别成：

```text
OPERATOR_BRACE_OPEN
TYPE_INT32
IDENTIFIER
OPERATOR_SEMICOLON
IDENTIFIER
OPERATOR_ASSIGN
CONSTANT_INTEGER
OPERATOR_PLUS
CONSTANT_INTEGER
OPERATOR_SEMICOLON
OPERATOR_BRACE_CLOSE
```

第二步，语法分析：

```text
TYPE_INT32 IDENTIFIER ;
```

会归约为声明：

```text
decl -> type IDENTIFIER ;
decls -> decls decl
```

然后：

```text
i = 3 + 4 ;
```

会归约为：

```text
loc -> IDENTIFIER
factor -> CONSTANT_INTEGER
term -> unary
expr -> expr + term
...
matched_stmt -> loc = bool ;
stmts -> stmts stmt
```

最后整个大括号内容归约成：

```text
block -> { decls stmts }
program -> block
```

第三步，语义分析：

- 声明 `i` 时，符号表注册 `i`。
- 使用 `i` 时，符号表检查 `i` 是否存在。
- 表达式 `3 + 4` 生成加载常量和加法命令。
- 赋值语句生成左值引用、右值计算和赋值命令。
- block 结束时，作用域退出。
- accept 前，命令序列被展开并打印。

## 15. 设计亮点

### 15.1 词法规则和运行时扫描分离

词法规则用正则描述，但运行时不逐条匹配正则，而是使用 DFA 状态表。这样运行时扫描和规则定义分离，是常见的词法实现方式。

### 15.2 语法使用带 lookahead 的表驱动分析

语法分析不是手写递归下降，而是通过 FIRST 集、项目集族、lookahead 和 ACTION/GOTO 表完成。对当前这份包含左递归表达式的文法，表驱动方式可以工作。

### 15.3 悬挂 if 在文法层解决

`matched_stmt` 和 `unmatched_stmt` 的拆分是非常重要的设计。它把 `else` 归属问题前移到语法层，不需要语义阶段事后补救。

### 15.4 语义分析采用 callback 机制

语义阶段没有写成一个庞大的分析器，而是拆成多个 callback：

- 建树 callback
- 日志 callback
- 作用域 callback
- 符号表 callback
- 命令生成 callback
- 错误处理 callback

这种结构让每个功能相对独立，也便于以后扩展。

### 15.5 语义命令生成采用语法制导翻译

命令生成不是随便遍历字符串，而是绑定在产生式归约上。每个重要产生式都有对应 translator，这符合语法制导翻译的思想。

### 15.6 break 和 continue 采用延迟标签回填

`break` 和 `continue` 的目标标签由循环结构决定。当前实现里，循环 translator 负责把未定跳转回填成真实标签；不过在 `grammar0` 中，`continue` 语句本身没有产生式，因此这里主要能在框架层看到它的处理入口。

## 16. 当前实现边界

本文也需要明确当前代码没有实现或没有完整体现的部分。

第一，当前语义命令主要是 demo 级中间命令，不是最终机器码，也不是完整三地址码生成器。

第二，符号表已经处理声明和使用检查，但错误信息中仍有一些 `TODO` 文本，说明诊断信息还可以继续完善。

第三，当前文档描述的类型能力主要来自文法和 token。代码中没有看到完整的类型检查流程，例如数组下标是否必须为整数、赋值左右类型是否兼容等。

第四，`if-else` translator 当前创建了 else 相关标签，但没有看到显式生成“then 分支结束后跳过 else 分支”的命令。因此不能把它描述成已经完整实现了所有控制流细节。

第五，语义动作当前依赖产生式文本进行匹配，不再依赖 `grammar0_pool.txt` 的产生式 id。这样可以避免语法表重建后 id 漂移造成语义处理器错配；但如果文法产生式的文本结构本身改变，`reduceStrategies0()` 仍然需要同步维护。

第六，项目代码大量使用 Lombok 生成构造器、getter 和日志字段。当前 `pom.xml` 已经显式配置 Maven 编译插件和 Lombok annotation processor，因此通过 Maven 编译时可以正确触发注解处理。

## 17. 总结

这条链路展示了一个小型编译前端的核心结构：

```text
字符输入
  -> DFA 词法分析
  -> token 流
  -> grammar0 文法
  -> FIRST / 项目集族 / lookahead
  -> ACTION/GOTO 表
  -> 移进归约分析
  -> callback 驱动语义处理
  -> 语法树、符号表、语义命令
```

它的特色不在于语言功能特别多，而在于结构完整：词法、语法、语义三个阶段都被拆成了可以说明、可以替换、可以扩展的模块。

从教学和实验角度看，这套代码的价值在于：它把编译原理里的抽象概念落实到了可运行结构中，包括正则到 DFA、表驱动移进归约、语法制导翻译、作用域和符号表维护。
