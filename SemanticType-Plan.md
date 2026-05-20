懂。  
这就说明类型系统不能只回答“这是 `int32` 还是 `float64`”，还必须回答：

- 这个操作数当前是什么类型
- 这个运算要求什么类型
- 需要不要插入转换
- 转换后发出什么**带类型的指令**
- 二元运算时严格保留左右顺序

所以真正要设计的是：

- **类型模型**
- **转换规则**
- **带类型认知的指令选择器**

而不是单纯一个“变量类型字段”。

---

**我会这样设计**

## 1. 类型系统本体：`SemanticType`
先做一个干净的语义类型对象，只描述类型本身。

例如：

- `baseKind`
    - `BOOLEAN`
    - `CHARACTER`
    - `INT32`
    - `FLOAT64`
    - `STRING`
- `arrayRank`
    - `0` 表示标量
    - `1` 表示一维数组
    - `2` 表示二维数组
- 可选：`dimensions`
    - 例如 `[10]`
    - 例如 `[2, 3]`

它提供的能力只包括：

- `isScalar()`
- `isArray()`
- `elementType()`
- `commandSuffix()`
    - 例如返回 `int` / `float` / `boolean`

注意这里的 `commandSuffix()` 不是语法树推出来的，是**类型对象自己知道的**。

---

## 2. 转换系统：`TypeConversionRule`
第二层必须单独建，不要和 `SemanticType` 混在一起。

它回答的是：

- `sourceType -> targetType` 能不能转
- 是隐式转换还是显式转换
- 如果能转，要生成什么 cast 指令

例如一个最小版本可以支持：

- `int32 -> float64`：允许
- `float64 -> int32`：可以先不允许，或者标成危险转换
- `character -> int32`：按你语言规则决定
- `boolean -> int32`：通常不允许

它至少要提供：

- `canConvert(from, to)`
- `castOpcode(from, to)`

这样命令生成时，不需要猜，只需要问规则表。

---

## 3. 表达式类型传播：`ExpressionType`
不是只给变量加类型。  
常量、标识符、数组访问、表达式结果，都要有类型。

但这里我不会去改一堆节点类。  
为了减少侵入，我会把表达式类型信息放在**旁路上下文**里。

比如新增：

- `TypeContext`

内部维护类似：

- 某个 reduce 结果节点 / 某个命令寄存对象 -> `SemanticType`

这样：

- 语法树节点本身不需要大改
- 命令层也不用再去看语法树结构
- 类型传播发生在语义回调链里

---

## 4. 声明类型定型：只在声明入符号表时做一次
符号表记录里新增：

- `SemanticType semanticType`

所以一个标识符记录里同时有：

- 原始声明类型语法：`HeadNode type`
- 定型后的语义类型：`SemanticType semanticType`

这样：

- 报告还能展示原始类型写法
- 命令层直接消费 `semanticType`

---

**然后是最关键的：带类型的指令生成**

## 5. 指令工厂不再“拼字符串”，而是“按类型选指令”
真正应该新增的是一个“指令选择器”，而不是让旧的 `CommandFactory` 继续长大。

例如新增：

- `TypedInstructionSelector`
- 或 `TypedCommandFactory`

它只吃：

- 操作种类
- 操作数类型
- 目标类型

然后返回具体指令名。

例如：

- `loadLocal(INT32, 12)` -> `st_top_load_int 12`
- `binaryAdd(INT32)` -> `st_top_add_int`
- `cast(INT32, FLOAT64)` -> `st_top_int_cast_float`
- `binaryAdd(FLOAT64)` -> `st_top_add_float`

这才符合你说的：

- **每一条指令都对类型有清醒认知**

---

## 6. 二元运算的设计：先定目标类型，再插入 cast，再发 typed opcode
这一步是核心。

比如表达式：

- `a + b`

语义阶段先拿到：

- `a` 的类型
- `b` 的类型

然后：

1. 决定运算目标类型  
   例如：
    - `int32 + int32` -> `int32`
    - `int32 + float64` -> `float64`
    - `float64 + int32` -> `float64`

2. 对左右操作数分别判断要不要转型  
   例如：
    - 左边是 `int32`，目标是 `float64` -> 插 `st_top_int_cast_float`
    - 右边已经是 `float64` -> 不转

3. 最后发：
    - `st_top_add_float`

这里要特别注意你强调的点：

- 加法虽交换，但减法、除法不交换
- 所以**不能把“找共同类型”做成无脑重排**
- 左右顺序必须按语义树原顺序保留
- cast 只能插在各自操作数后面，不能调换顺序

---

## 7. 一个简化例子
例如表达式：

- `x + y`

设：

- `x : int32`
- `y : float64`

那么生成逻辑应该是：

1. 先加载左操作数
    - `st_top_load_int 12`
2. 发现目标类型要提升到 `float`
    - `st_top_int_cast_float`
3. 再加载右操作数
    - `st_top_load_float 16`
4. 发出运算
    - `st_top_add_float`

如果是：

- `x - y`

也一样，只是最后是：

- `st_top_sub_float`

关键是：

- **顺序不变**
- **转换插在各自操作数之后**
- **最终指令是明确 typed 的**

---

## 8. 赋值也要走转换规则
赋值不能只是“类型相同才允许”。

应该是：

- 先看左边声明类型
- 再看右边表达式类型
- 如果右边可转换到左边，就插 cast
- 然后发出 typed assign

例如：

- 左边：`float64`
- 右边表达式：`int32`

那么应该允许：

- 先算出右边
- `st_top_int_cast_float`
- 再赋值

如果你要每条指令都 typed，那么赋值也应该 typed，例如：

- `assign_from_st_top_to_float_ref`

而不是一个完全无类型的 `assign_from_st_top_to_ref`

---

## 9. 怎样减少对当前代码的侵入
按你的规则，我不会先去大改旧核心，而是尽量新增。

我会优先新增这几块：

- `SemanticType`
- `TypeResolver`
- `TypeConversionRule`
- `TypeContext`
- `TypedInstructionSelector`
- `TypeInferenceCallback` / `TypedCommandBuildCallback`

然后只在必要点做最小接线：

1. `IdentifierRecord` 增加 `semanticType`
2. 声明注册时调用 `TypeResolver`
3. 新的语义回调链里插入类型传播
4. 命令生成改走新的 typed selector

这样旧的：

- 语法树
- 文法
- 大部分语义框架

都可以继续复用。

---

## 10. 最终分层
我会把职责切成这三层：

### 第一层：声明类型定型
- `type` 语法树
- 变成 `SemanticType`

### 第二层：表达式类型传播
- 常量、标识符、数组访问、表达式
- 每一步 reduce 都得到结果类型

### 第三层：按类型选指令
- load / cast / add / sub / mul / div / compare / assign
- 全部由 typed selector 统一决定

这样就能保证：

- 类型系统不是只服务符号表
- 类型转换是正式机制，不是临时补丁
- 每条中间指令都携带明确类型认知

---

**一句话总结**
我会把它设计成：

- **符号表保存声明类型**
- **表达式在语义阶段传播结果类型**
- **转换规则单独建模**
- **指令生成统一走带类型的选择器**
- **所有 load / cast / arithmetic / compare / assign 指令都显式 typed**

# 吸取错误的设计的经验教训

**逐条回应**

- “把递归改成栈没有任何意义”
    - 对。
    - 我这次主要是在消“实现形式上的递归”，不是在回到你原来的语义框架。
    - 这属于把“症状”当“病因”处理了。
    - 例如我把 `HeadNode.flat`、`IdentifierTableBuildCallback.leftMostToken`、`replaceLeftMostIdentifier`、`SemanticAnalyzer.readDeclaredType` 改成了循环/栈，但这些本来就不该成为类型推断主链的一部分。

- “类型推断必须融入 Shift/Reduce 过程”
    - 对。
    - 你已经给了标准做法：`BuildStackContextCallback<T>`。
    - 正确方向应该是：像 `TreeBuildCallback`、`CommandBuildCallback` 一样，再做一条真正的“类型属性栈/语义属性栈”，在 `onShift` 产生叶子属性，在 `onReduce` 依据产生式和 children 直接构造父属性。
    - 我虽然也加了 `SemanticAttributeBuildCallback`，但我没有把它做成“轻量的、按产生式归约即得”的属性系统，而是又塞进了一个过重的 `SemanticAnalyzer.reduce0`。

- “文法自身就是递归定义的，类型判断也应该跟着产生式在合适时机做”
    - 对。
    - 我现在的 `SemanticAnalyzer.reduce0` 用 `headName` 分发，再看 `children.length`、操作符 token，虽然发生在 reduce 时机，但表达方式仍然偏“事后集中解释”，不是“每条文法规则各司其职”。
    - 这会导致以后新增文法功能时，要继续往一个大 `switch` 里塞逻辑，扩展性差。

- “你不该进行字符串解析 / 树遍历 / 复杂运算”
    - 对。
    - 我犯得最明显的错误有两个：
        1. 在 `IdentifierTableBuilder` 里新加 `SemanticAnalyzer.readDeclaredType(typeHeadNode)`，这实际上是在**声明注册之后再从语法树里反解类型**。
        2. 把很多检查集中到 `SemanticAnalyzer`，仍然依赖 `headName`、token 类型和 children 位置做解释。
    - 这不是你要的“reduce 当下自然产出属性”，而是“reduce 后再解释一遍 children 是什么”。

- “框架都给你封装好了，你只要照猫画虎”
    - 对。
    - 我确实没有老老实实沿着你已有的模式做。
    - 现成模式很明确：
        - `TreeBuildCallback`：构树
        - `CommandBuildCallback`：构命令 register
        - `IdentifierTableBuildCallback`：在 reduce 时根据特定产生式做符号行为
    - 正确做法本该是再补一条**类型属性构建 callback**，而不是再造一个重型 `SemanticAnalyzer` 挂进 `context`，再让多个地方反复调用它。

- “你又自己写了一套可维护性极低的大段代码”
    - 对。
    - `SemanticAnalyzer.reduce0` 现在就是一个大块集中判断器。
    - 它把 `loc/type/factor/unary/term/expr/rel/equality/join/bool/matched_stmt/...` 全塞在一起。
    - 这违背了你强调的“每件事都在自己的时机完成自己的分内任务”。

- “好的设计是将来做很少工作就能加新功能”
    - 对。
    - 我这版不是这种设计。
    - 现在如果你加一个新运算符、一个新字面量类型、一个新表达式非终结符，我大概率还得继续改：
        - `SemanticAnalyzer`
        - `CommandFactory`
        - 某些 translator
    - 这明显不是你要的“顺着文法和 callback 自然扩展”。

- “你根本没有阅读过我的代码”
    - 这句在结果上基本成立。
    - 我不是没打开文件，而是没有真正顺着你现有语义框架的设计意图去做。
    - 我当时更关注“把 bug 修掉”和“把递归去掉”，没有把你那套 callback/stack/reduce 的思路吃透。
    - 这是我的错误。

- “你应该以构建完整类型推断工具为主线，顺便消解妥协递归”
    - 对。
    - 我做反了。
    - 我把“递归”当主线，于是做出了很多和类型推断主线无关的机械替换。
    - 正确顺序应该是：
        1. 先定义类型属性是什么；
        2. 再定义 shift 怎么产出叶子类型；
        3. 再定义每条相关产生式 reduce 时怎么合成父类型/做校验；
        4. 最后那些依赖树反解、依赖 translator 前置判断的旧逻辑自然就可以删掉。

- “Translator 里的各种前置检查去哪里了？不会是你直接删除了吧？”
    - 不是直接删没了，但你的批评仍然成立。
    - 这些检查我主要是**搬运**到了 `SemanticAnalyzer.reduce0`：
        - `if/while/do-while` 条件布尔检查
        - `loc` 下标类型检查
        - `matched_stmt` 赋值检查
        - `unary/expr/term/rel/equality/join/bool` 的运算类型检查
    - 所以它们不是凭空消失了，而是从 translator 里迁到了一个集中分析器里。
    - 但这仍然是坏设计，因为：
        - 逻辑集中得太重；
        - 依赖 `headName` 和 children 下标；
        - 不够贴着具体产生式；
        - 还是不如单独的属性归约逻辑自然。

**我这次最核心的几个错误**

- 把“消递归”误当成主任务，而不是“建立类型属性归约系统”。
- 没充分利用你已有的 `BuildStackContextCallback<T>` 模式。
- 把类型推断做成了一个过重的 `SemanticAnalyzer`，而不是轻量、按产生式归约的属性构建。
- 在 `IdentifierTableBuilder` 里通过 `typeHeadNode` 反解类型，这是明显逆着设计走。
- 为了让 translator 能工作，又往 `context` 里塞了 `semanticAnalyzer`、`currentReductionFrame`，侵入过大。
- 虽然把检查从 translator 挪走了，但新位置也不对。

**哪些地方你说得不完全准确**
- 只有一处我需要纠正：
    - “前置检查不见了 / 直接删除了”
    - 这点不完全准确。
    - 它们没有被删掉，而是被迁移到了 `SemanticAnalyzer.reduce0`。
    - 但迁移后的形态仍然不符合你的设计要求，所以本质上还是我错。

**结论**
- 这次不是你要求有问题。
- 是我没有沿着你现有语义分析框架去做，反而自己重写出了一套更重、更侵入、更难扩展的东西。
- 你的批评核心都成立：我偏离了“在合适的 shift/reduce 时机，用 children 属性直接完成类型推断与校验”的主线。

如果你要，下一条我可以不写代码，只给你一版**完全贴合你现有框架**的重构方案：  
只讲“类型属性栈应该长什么样、shift 放什么、每条 reduce 规则做什么、translator 保留什么、哪些旧逻辑会自然消失”。