# PR6 Review 整理与后续计划

本文基于 [src/main/resources/scripts/review-pr6.md](/D:/IT_study/source/temp/compiler-front-theory/src/main/resources/scripts/review-pr6.md) 重写整理。

这次整理遵守四个原则：

1. `review-pr6.md` 里的代码上下文一条不丢，直接搬进本文。
2. 每条 comment 同时保留“原文”和“清洗后表述”。
3. 文件链接只给文件，不给行号，避免代码变动后误导读者。
4. `Review 快照行` 只是当时评审记录里的历史位置，不代表当前源码行号。


## 1. 分类总结

### 1.1 文法识别与语义分发方式不稳

- 多处通过 `production.toString()`、`head.getSymbol().toString()`、字符串替换等方式驱动语义逻辑。
- 这类做法不是单点问题，而是“语义动作如何识别”的入口没有稳定下来。
- `FunctionSemanticCallback` 和 `ConstantValueBuildCallback` 是这类问题最集中的位置。


### 1.2 递归遍历与框架误用

- 现有框架已经提供了 shift/reduce 驱动、类型寄存、常量寄存、命令寄存。
- 但在 function 参数收集、实参收集、return flow 判断、常量条件支持等地方，又额外手写了树递归。
- 评审意见的核心不是“递归语法不好看”，而是“绕开框架重新做了一套局部分析”。


### 1.3 Function 语义设计与职责边界

- `FunctionSemanticCallback` 的职责过重，而且挂载位置本身就被质疑。
- `FunctionContext`、`FunctionBodyState`、`ShiftReduceSemanticContext` 上与 function 相关的边界还不清楚。
- `FunctionReturnFlowAnalyzer` 也暴露出“功能目的有了，但设计没有收束”的问题。


### 1.4 名字表示、`SourceToken` 与文本解码

- 语义层明明已经拿到了 `SourceToken`，但多处又把它退化成 Java `String`。
- 这里面混在一起的其实是三件事：内部键、相等性比较、展示文本。
- 评审意见认为这些职责不该混在一起，更不该直接 `new String(..., UTF_8)`。


### 1.5 常量推导与常量条件支持

- 方向本身是认可的：在语义阶段判断常量条件，有助于命令生成。
- 但实现形式偏临时：静态工具类、局部判断、与类型推导关系不清。
- 评审意见要求把它上升为正式能力，而不是继续堆工具函数。


### 1.6 命令生成与字符串命令体系

- 当前 `StringCommand` 的责任边界不清，命令字符串的拼接散落在多个层面。
- `SemanticType.mnemonic()` 也被质疑承担了不该承担的字符串职责。
- 评审意见倾向于把字符串命令生成能力收束到专门工厂层。


### 1.7 文档、测试与工程整理

- 启动参数已经进入实现，但文档没有同步说明用法。
- 测试资产可读性也不足，样例没有写清“测试目的”。
- markdown 文档的位置管理也需要统一。


### 1.8 过渡性产生式合法性判断

- `children.length` 这种判断目前被允许暂存。
- 但评审意见已经明确：这只是过渡方案，下阶段应优先替换。


### 1.9 其余局部意见

- 有些 comment 不是否定，而是要求补说明、补抽象、或者确认当前没有问题。
- 这类意见也完整保留，避免只记录“负面项”。


## 2. 逐条整理

### 2.1 文档、测试与工程整理

#### 条目 1

文件： [docs/全链路说明.md](/D:/IT_study/source/temp/compiler-front-theory/docs/全链路说明.md)  
Review 快照中的文件名： `全链路说明.md`  
Review 快照行： `1`

代码上下文：

```markdown
> 1: # 全链路说明
  2:
  3: 这份文档的目标不是罗列源码路径，而是让读者在**不先通读全部源码**的前提下，也能理解这个项目如何工作、如何验证、以及如何扩展。
  4:
  5: 因此全文尽量按下面的顺序组织：
  6:
  7: 1. 先说明这一层要解决什么问题
  8: 2. 再说明当前实现用了什么设计技巧
  9: 3. 再给出关键代码片段
  10: 4. 最后用一个小例子说明它在全链路里如何生效
  11:
```

Comment 原文：

> 现在有代码如        private static final boolean FLUSH_TABLE = Boolean.getBoolean("lexical.flushTable"); 其是从启动参数里取值作为配置的. 那么就有必要在这个文件种说明所有被应用的启动项配置以及相关用法

清洗后表述：

> 既然实现里已经通过启动参数控制行为，那么全链路说明文档就应该明确列出这些启动项、用途和使用方式，不能只讲流程，不讲实际运行配置。


#### 条目 2

文件： [src/main/resources/program-tests/text24-if-true-inline.txt](/D:/IT_study/source/temp/compiler-front-theory/src/main/resources/program-tests/text24-if-true-inline.txt)  
Review 快照行： `1`

代码上下文：

```text
> 1: {
  2: int32 a;
  3: a = 1;
  4: if (true) a = 2;
  5: a = 3;
  6: }
  7:
```

Comment 原文：

> 这些测试用例里都补一下注释, 说明一下都是测试哪些功能的.

清洗后表述：

> 测试样例需要补充注释，明确每个输入文件到底在验证什么语义或命令生成行为，否则测试资产可读性太差。


#### 条目 3

文件： [docs/grammar0_全链路说明.md](/D:/IT_study/source/temp/compiler-front-theory/docs/grammar0_全链路说明.md)  
Review 快照中的文件名： `grammar0_全链路说明.md`  
Review 快照行： `1`

代码上下文：

```markdown
> 1: # grammar0 编译前端全链路说明文档
  2:
  3: 本文说明 `ProgramSyntaxDemo` 所串联起来的一整条编译前端链路。文档面向不直接阅读源码的人，因此会尽量用语言解释每个阶段“做什么、为什么这样做、数据如何流动、最终产生什么结果”。文中只描述当前项目中已经实现或已经接入的能力，不额外假设不存在的功能。
  4:
  5: 本文涉及的主线是：
  6:
  7: ```text
  8: 测试程序文本
  9:   -> 词法分析，得到 token 流
  10:   -> 语法分析，依据 grammar0 构造并使用移进归约表
  11:   -> 语义分析，随移进/归约事件构建语法树、符号表和语义命令
```

Comment 原文：

> 除了Readme以外的markdown文件, 都放到docs目录下统一管理, 以后文件说不定会变多

清洗后表述：

> 除 `README` 外，其余项目文档应统一收拢到 `docs` 目录，避免后续文档增多后散落在仓库根目录。


#### 条目 36

文件： [src/main/java/org/harvey/vie/theory/demo/program/ProgramLexicalDemo.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/demo/program/ProgramLexicalDemo.java)  
Review 快照行： `59`

代码上下文：

```java
  49:             "=", "{", "}", "[", "]", "\\|", "\\\\", ":", ";",
  50:             "\"", "'", "<", ">", ",", ".", "?", "/"
  51:     );
  52:     private static final RegexCharSet OTHER = RegexCharSet.of("@", "#", "$", "_");
  53:     private static final RegexCharSet ANY_WITHOUT_NEWLINE = RegexCharSet.unionAll(
  54:             DIGIT, WHITESPACE_WITHOUT_NEWLINE, LOWER_LETTER, UPPER_LETTER, OPERATOR, OTHER
  55:     );
  56:     private static final RegexCharSet ANY = RegexCharSet.unionAll(
  57:             DIGIT, WHITESPACE, LOWER_LETTER, UPPER_LETTER, OPERATOR, OTHER
  58:     );
> 59:     private static final boolean FLUSH_TABLE = Boolean.getBoolean("lexical.flushTable");
  60:     private static volatile LexicalAnalyzer cachedAnalyzer;
  61:
  62:     private static final List<LexicalPattern> REGEX_PATTERNS = List.of(
  63:             new LexicalPattern(WHITESPACE + "" + WHITESPACE + "*", ProgramTokenType.SPACE),
  64:             new LexicalPattern(
  65:                     "/\\*(" + ANY.exclude("\\*") + "|\\*" + ANY.exclude("/") + ")*\\**\\*/",
  66:                     ProgramTokenType.COMMENT_BLOCK
  67:             ),
  68:             new LexicalPattern("//" + ANY_WITHOUT_NEWLINE + "*\n", ProgramTokenType.COMMENT_LINE),
  69:             new LexicalPattern("\"(" +
```

Comment 原文：

> 这里采用了命令行参数, 那么命令行如何使用也应该再文档中补充

清洗后表述：

> 既然这里已经引入了启动参数，就需要在文档中补上命令行用法，说明参数名、效果和典型调用方式。


#### 条目 37

文件： [src/main/java/org/harvey/vie/theory/demo/SyntaxDemo.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/demo/SyntaxDemo.java)  
Review 快照行： `143`

代码上下文：

```java
  133:         return null;
  134:     }
  135:
  136:     private static LexicalAnalyzer lexicalAnalyzer() {
  137:         AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
  138:         RegexDfaStatusTable table = LexicalDemo.buildTable(alphabetCharacterFactory);
  139:         SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
  140:         return new DefaultLexicalAnalyzer(table, saca);
  141:     }
  142:
> 143:     private static final boolean FLUSH_TABLE = Boolean.getBoolean("syntax.flushTable");
  144:     private static volatile ShiftReduceParsingTable cachedShiftReduceParsingTable;
  145:
  146:     public static ShiftReduceParsingTable buildShiftReduceParsingTable(
  147:             String startHead,
  148:             ProductionSetContext context,
  149:             String filename) {
  150:         if (cachedShiftReduceParsingTable != null) {
  151:             return cachedShiftReduceParsingTable;
  152:         }
  153:         synchronized (SyntaxDemo.class) {
```

Comment 原文：

> 命令行参数, 同上.

清洗后表述：

> 和词法 demo 一样，这里的启动参数也应该在文档中统一说明，避免使用者只能去读源码猜参数。


### 2.2 `TypeBuildCallback` 与 `ConstantValueBuildCallback` 的关系

#### 条目 6

文件： [src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java)  
Review 快照行： `1`

代码上下文：

```java
> 1: package org.harvey.vie.theory.semantic.type;
  2:
  3: import org.harvey.vie.theory.exception.CompilerException;
  4: import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
  5: import org.harvey.vie.theory.semantic.analysis.SemanticType;
  6: import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
  7: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  8: import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
  9: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  10: import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
  11: import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
```

Comment 原文：

> 和theory/semantic/value/ConstantValueBuildCallback.java什么关系? 为啥有两个?

清洗后表述：

> 需要把 `TypeBuildCallback` 和 `ConstantValueBuildCallback` 的职责关系讲清楚，否则看起来像是两套相似实现被平行堆起来了。


#### 条目 7

文件： [src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java)  
Review 快照行： `1`

代码上下文：

```java
> 1: package org.harvey.vie.theory.semantic.type;
  2:
  3: import org.harvey.vie.theory.exception.CompilerException;
  4: import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
  5: import org.harvey.vie.theory.semantic.analysis.SemanticType;
  6: import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
  7: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  8: import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
  9: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  10: import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
  11: import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
```

Comment 原文：

> 哦哦, 一个是Type, 一个是Constant, 懂了. 两个逻辑挺类似的

清洗后表述：

> 两者的职责可以区分开，但实现结构明显相似，后续应考虑把共性模式整理出来，而不是只靠“名字不同”维持理解。


### 2.3 文法识别与语义分发方式不稳

#### 条目 4

文件： [src/main/java/org/harvey/vie/theory/semantic/value/ConstantValueBuildCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/value/ConstantValueBuildCallback.java)  
Review 快照行： `120`

代码上下文：

```java
  110:             case "rel->expr OPERATOR_GREATER expr":
  111:                 return relation(context, head, "greater");
  112:             case "rel->expr OPERATOR_GREATER_EQUAL expr":
  113:                 return relation(context, head, "greater_equal");
  114:             default:
  115:                 return null;
  116:         }
  117:     }
  118:
  119:     private String normalizeKey(String key) {
> 120:         return key.replace("return_type", "type");
  121:     }
  122:
  123:     private boolean isEpsilonOf(String key, String... heads) {
  124:         int index = key.indexOf("->");
  125:         if (index < 0) {
  126:             return false;
  127:         }
  128:         String head = key.substring(0, index);
  129:         boolean matchesHead = false;
  130:         for (String candidate : heads) {
```

Comment 原文：

> 这是干什么? 什么目的?

清洗后表述：

> 这里的 `replace("return_type", "type")` 缺少设计说明，看起来像是在修补产生式字符串不一致，而不是在建立稳定的语义识别机制。


#### 条目 5

文件： [src/main/java/org/harvey/vie/theory/semantic/value/ConstantValueBuildCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/value/ConstantValueBuildCallback.java)  
Review 快照行： `140`

代码上下文：

```java
  130:         for (String candidate : heads) {
  131:             if (candidate.equals(head)) {
  132:                 matchesHead = true;
  133:                 break;
  134:             }
  135:         }
  136:         if (!matchesHead) {
  137:             return false;
  138:         }
  139:         String body = key.substring(index + 2).trim();
> 140:         return body.isEmpty() || "蔚".equals(body) || "ε".equals(body);
  141:     }
  142:
  143:     private ConstantValue literal(ShiftReduceSemanticContext context, SourceToken token) {
  144:         SemanticType type = context.literalType(token);
  145:         String lexeme = new String(token.getLexeme(), StandardCharsets.UTF_8);
  146:         switch (type.getKind()) {
  147:             case BOOLEAN:
  148:                 return new ConstantValue(type, Boolean.parseBoolean(lexeme));
  149:             case INT32:
  150:                 return new ConstantValue(type, Integer.parseInt(lexeme));
```

Comment 原文：

> 气笑了

清洗后表述：

> 这里把空体、`蔚`、`ε` 混在一起做字符串判定，说明 epsilon 处理仍停留在脆弱的文本层，缺少稳定的结构化表示。


#### 条目 8

文件： [src/main/java/org/harvey/vie/theory/semantic/identifier/IdentifierScopeCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/identifier/IdentifierScopeCallback.java)  
Review 快照行： `60`

代码上下文：

```java
  50:             throws CompileException {
  51:         // 是需要从符号表中查询
  52:         if (!scopeExistPredicate.test(production)) {
  53:             return;
  54:         }
  55:         TreeContext treeContext = context.getTreeContext();
  56:         if (treeContext.isEmpty() || !treeContext.peek().isHead()) {
  57:             return;
  58:         }
  59:         IdentifierRecord[] scope = context.scopeExistBlock();
> 60:         if (production.toString().trim().equals("block->OPERATOR_BRACE_OPEN block_items OPERATOR_BRACE_CLOSE")
  61:                 && context.isCurrentBlockFunctionBody()) {
  62:             FunctionBodyState bodyState = context.currentFunctionBodyState();
  63:             if (bodyState != null
  64:                     && !FunctionReturnFlowAnalyzer.guaranteesReturn(context, treeContext.peek())
  65:                     && !bodyState.getFunction().getSignature().getReturnType().isVoidScalar()) {
  66:                 throw new CompileException("non-void function must return a value.");
  67:             }
  68:         }
  69:         context.finishBlockScope();
  70:         treeContext.resetTop(top -> {
```

Comment 原文：

> 字符串判断

清洗后表述：

> 这里直接依赖 `production.toString().trim().equals(...)` 做语义分支，是典型的文本驱动语义，耦合方式不稳。


#### 条目 10

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `182`

代码上下文：

```java
  172:     }
  173:
  174:     private void collectArgumentTypes0(
  175:             ShiftReduceSemanticContext context,
  176:             ShiftReduceSyntaxTreeNode node,
  177:             List<TypeRegister> result) {
  178:         if (!node.isHead()) {
  179:             return;
  180:         }
  181:         HeadNode head = node.toHead();
> 182:         if ("bool".equals(head.getSymbol().toString())) {
  183:             TypeRegister register = context.getType(head);
  184:             if (register != null) {
  185:                 result.add(register);
  186:             }
  187:             return;
  188:         }
  189:         for (ShiftReduceSyntaxTreeNode child : head) {
  190:             collectArgumentTypes0(context, child, result);
  191:         }
  192:     }
```

Comment 原文：

> 解析字符串了

清洗后表述：

> 这里通过 `head.getSymbol().toString()` 判断是否为 `bool`，说明语义层依旧在解析文本符号，而不是依赖稳定的产生式或节点结构。


#### 条目 14

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `148`

代码上下文：

```java
  138:
  139:     private void collectParameters0(
  140:             ShiftReduceSemanticContext context,
  141:             ShiftReduceSyntaxTreeNode node,
  142:             List<FunctionParameter> result) {
  143:         if (!node.isHead()) {
  144:             return;
  145:         }
  146:         HeadNode head = node.toHead();
  147:         if ("param".equals(head.getSymbol().toString())) {
> 148:             TypeRegister register = context.getType(head.get(0));
  149:             if (register == null) {
  150:                 throw new CompilerException("parameter type is missing.");
  151:             }
  152:             SemanticType type = register.requireType("parameter type is required.");
  153:             SourceToken nameToken = head.get(1).toToken().getSource();
  154:             SemanticTypeDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
  155:             for (FunctionParameter parameter : result) {
  156:                 if (parameter.isNamed(nameToken)) {
  157:                     SemanticTypeDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
  158:                 }
```

Comment 原文：

> 解析字符串的问题

清洗后表述：

> 这里的问题不在 `context.getType(head.get(0))` 本身，而在它前面通过 `head.getSymbol().toString()` 判定 `param`，依旧是字符串驱动的分支逻辑。


#### 条目 15

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `148`

代码上下文：

```java
  138:
  139:     private void collectParameters0(
  140:             ShiftReduceSemanticContext context,
  141:             ShiftReduceSyntaxTreeNode node,
  142:             List<FunctionParameter> result) {
  143:         if (!node.isHead()) {
  144:             return;
  145:         }
  146:         HeadNode head = node.toHead();
  147:         if ("param".equals(head.getSymbol().toString())) {
> 148:             TypeRegister register = context.getType(head.get(0));
  149:             if (register == null) {
  150:                 throw new CompilerException("parameter type is missing.");
  151:             }
  152:             SemanticType type = register.requireType("parameter type is required.");
  153:             SourceToken nameToken = head.get(1).toToken().getSource();
  154:             SemanticTypeDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
  155:             for (FunctionParameter parameter : result) {
  156:                 if (parameter.isNamed(nameToken)) {
  157:                     SemanticTypeDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
  158:                 }
```

Comment 原文：

> 实际上, 除了toString本身可以调用下一层模块的toString, 代码的其他任何地方都不应该出现对toString的调用

清洗后表述：

> 除调试或展示外，业务逻辑不应依赖 `toString()` 结果；一旦把 `toString()` 当成结构化判断依据，设计就会变得脆弱且难维护。


#### 条目 16

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `31`

代码上下文：

```java
  21:         onReduce0(context, normalizeKey(production.toString().trim()));
  22:         ShiftReduceCallback.super.onReduce(context, production);
  23:     }
  24:
  25:     private void onReduce0(ShiftReduceSemanticContext context, String key) {
  26:         if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
  27:             return;
  28:         }
  29:         HeadNode head = context.getTreeContext().peek().toHead();
  30:         switch (key) {
> 31:             case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  32:             case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  33:                 prepareFunction(context, head);
  34:                 return;
  35:             case "function_decl->function_head block":
  36:             case "top_item->function_decl":
  37:             case "arg_list->蔚":
  38:             case "arg_list->ε":
  39:                 return;
  40:             case "return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON":
  41:                 validateReturnValue(context, head, true);
```

Comment 原文：

> ShiftReduceCallback  不是你这么用的

清洗后表述：

> `ShiftReduceCallback` 这里被当成“拿到产生式字符串后自己分发所有 function 语义”的挂点来用，评审意见认为这已经偏离了它更合适的使用层级。


#### 条目 17

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `31`

代码上下文：

```java
  21:         onReduce0(context, normalizeKey(production.toString().trim()));
  22:         ShiftReduceCallback.super.onReduce(context, production);
  23:     }
  24:
  25:     private void onReduce0(ShiftReduceSemanticContext context, String key) {
  26:         if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
  27:             return;
  28:         }
  29:         HeadNode head = context.getTreeContext().peek().toHead();
  30:         switch (key) {
> 31:             case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  32:             case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  33:                 prepareFunction(context, head);
  34:                 return;
  35:             case "function_decl->function_head block":
  36:             case "top_item->function_decl":
  37:             case "arg_list->蔚":
  38:             case "arg_list->ε":
  39:                 return;
  40:             case "return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON":
  41:                 validateReturnValue(context, head, true);
```

Comment 原文：

> 充满了对框架的误解

清洗后表述：

> 这段实现被认为没有顺着现有框架的职责边界来做，而是把 callback 当成了一个总入口，再在内部手写一层小框架。


#### 条目 18

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `54`

代码上下文：

```java
  44:                 validateReturnValue(context, head, false);
  45:                 return;
  46:             case "call_expr->IDENTIFIER OPERATOR_PARENTHESIS_OPEN arg_list OPERATOR_PARENTHESIS_CLOSE":
  47:                 validateCall(context, head);
  48:                 return;
  49:             default:
  50:         }
  51:     }
  52:
  53:     private String normalizeKey(String key) {
> 54:         return key.replace("return_type", "type");
  55:     }
  56:
  57:     private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
  58:         SourceToken nameToken = head.get(1).toToken().getSource();
  59:         String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
  60:         if (context.existFunction(name)) {
  61:             SemanticTypeDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
  62:         }
  63:         TypeRegister returnTypeRegister = context.getType(head.get(0));
  64:         if (returnTypeRegister == null) {
```

Comment 原文：

> 何意味, 如果不replace会怎么样的? 这种改发难道是为了解决问题吗? 难道不是为了糊弄测试吗?

清洗后表述：

> 这里的 `replace` 像是为了抹平产生式命名差异而加的补丁。评审意见质疑它没有真正解决设计问题，只是在让当前 case 勉强跑通。


#### 条目 21

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `31`

代码上下文：

```java
  21:         onReduce0(context, normalizeKey(production.toString().trim()));
  22:         ShiftReduceCallback.super.onReduce(context, production);
  23:     }
  24:
  25:     private void onReduce0(ShiftReduceSemanticContext context, String key) {
  26:         if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
  27:             return;
  28:         }
  29:         HeadNode head = context.getTreeContext().peek().toHead();
  30:         switch (key) {
> 31:             case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  32:             case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
  33:                 prepareFunction(context, head);
  34:                 return;
  35:             case "function_decl->function_head block":
  36:             case "top_item->function_decl":
  37:             case "arg_list->蔚":
  38:             case "arg_list->ε":
  39:                 return;
  40:             case "return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON":
  41:                 validateReturnValue(context, head, true);
```

Comment 原文：

> 这种针对于产生式的, 都已经不是和ShiftReduceCallback同一个层级的了, 还要强行套用ShiftReduce就是大错特错. 就是除了ShiftReduceCallback不知道其他更合适的框架提供的接口导致的

清洗后表述：

> 评审意见认为：这种“针对具体产生式做语义调度”的逻辑，已经超出了 `ShiftReduceCallback` 本该直接承担的层级，继续强行挂在这里会把设计做歪。


#### 条目 23

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `18`

代码上下文：

```java
  8: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  9: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  10: import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
  11: import org.harvey.vie.theory.semantic.type.TypeRegister;
  12: import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
  13:
  14: import java.nio.charset.StandardCharsets;
  15: import java.util.ArrayList;
  16: import java.util.List;
  17:
> 18: public class FunctionSemanticCallback implements ShiftReduceCallback {
  19:     @Override
  20:     public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
  21:         onReduce0(context, normalizeKey(production.toString().trim()));
  22:         ShiftReduceCallback.super.onReduce(context, production);
  23:     }
  24:
  25:     private void onReduce0(ShiftReduceSemanticContext context, String key) {
  26:         if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
  27:             return;
  28:         }
```

Comment 原文：

> 这种针对于产生式的, 都已经不是和ShiftReduceCallback同一个层级的了, 还要强行套用ShiftReduce就是大错特错. 就是除了ShiftReduceCallback不知道其他更���适的框架提供的接口导致的

清洗后表述：

> 这条意见和前一条一致，只是指向更外层：`FunctionSemanticCallback` 整体挂成一个 `ShiftReduceCallback`，本身就被怀疑挂错了层。


#### 条目 25

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java)  
Review 快照行： `19`

代码上下文：

```java
  9:     private FunctionReturnFlowAnalyzer() {
  10:     }
  11:
  12:     public static boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
  13:         if (node == null || !node.isHead()) {
  14:             return false;
  15:         }
  16:         HeadNode head = node.toHead();
  17:         String symbol = head.getSymbol().toString();
  18:         switch (symbol) {
> 19:             case "block":
  20:                 return blockGuaranteesReturn(context, head);
  21:             case "block_items":
  22:                 return blockItemsGuaranteeReturn(context, head);
  23:             case "block_item":
  24:                 return head.size() > 0 && guaranteesReturn(context, head.get(0));
  25:             case "stmt":
  26:             case "matched_stmt":
  27:             case "unmatched_stmt":
  28:                 return statementGuaranteesReturn(context, head);
  29:             case "return_stmt":
```

Comment 原文：

> 依旧解析字符串

清洗后表述：

> `FunctionReturnFlowAnalyzer` 仍然通过 `head.getSymbol().toString()` 做分支，这和前面的字符串分发问题属于同一类耦合。


### 2.4 递归遍历与框架误用

#### 条目 11

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `190`

代码上下文：

```java
  180:         }
  181:         HeadNode head = node.toHead();
  182:         if ("bool".equals(head.getSymbol().toString())) {
  183:             TypeRegister register = context.getType(head);
  184:             if (register != null) {
  185:                 result.add(register);
  186:             }
  187:             return;
  188:         }
  189:         for (ShiftReduceSyntaxTreeNode child : head) {
> 190:             collectArgumentTypes0(context, child, result);
  191:         }
  192:     }
  193: }
  194:
```

Comment 原文：

> 怎么还有递归?????

清洗后表述：

> 这里又回到了手写树递归，而不是直接利用已有语义框架提供的归约结果和寄存信息。


#### 条目 12

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `164`

代码上下文：

```java
  154:             SemanticTypeDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
  155:             for (FunctionParameter parameter : result) {
  156:                 if (parameter.isNamed(nameToken)) {
  157:                     SemanticTypeDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
  158:                 }
  159:             }
  160:             result.add(new FunctionParameter(nameToken, type, head.get(0).toHead()));
  161:             return;
  162:         }
  163:         for (ShiftReduceSyntaxTreeNode child : head) {
> 164:             collectParameters0(context, child, result);
  165:         }
  166:     }
  167:
  168:     private List<TypeRegister> collectArgumentTypes(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
  169:         List<TypeRegister> result = new ArrayList<>();
  170:         collectArgumentTypes0(context, node, result);
  171:         return result;
  172:     }
  173:
  174:     private void collectArgumentTypes0(
```

Comment 原文：

> 递归的问题

清洗后表述：

> 参数收集这里的问题不是局部写法，而是整体思路仍然在做树扫描。


#### 条目 13

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `164`

代码上下文：

```java
  154:             SemanticTypeDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
  155:             for (FunctionParameter parameter : result) {
  156:                 if (parameter.isNamed(nameToken)) {
  157:                     SemanticTypeDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
  158:                 }
  159:             }
  160:             result.add(new FunctionParameter(nameToken, type, head.get(0).toHead()));
  161:             return;
  162:         }
  163:         for (ShiftReduceSyntaxTreeNode child : head) {
> 164:             collectParameters0(context, child, result);
  165:         }
  166:     }
  167:
  168:     private List<TypeRegister> collectArgumentTypes(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
  169:         List<TypeRegister> result = new ArrayList<>();
  170:         collectArgumentTypes0(context, node, result);
  171:         return result;
  172:     }
  173:
  174:     private void collectArgumentTypes0(
```

Comment 原文：

> 递归不要用栈+循环消解, 这个框架本身就能消解掉递归的, 本身就不应该使用递归, 使用了递归就说明彻底没有理解框架的作用, 相当于自己又写了一套效率低, 没法复用, 扩展性差的代码

清洗后表述：

> 评审意见的重点很明确：这里不该讨论“递归要不要改成显式栈”，而是根本不该重新写一套树遍历逻辑，因为框架本来就承担了这类分解工作。


#### 条目 27

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java)  
Review 快照行： `50`

代码上下文：

```java
  40:             return false;
  41:         }
  42:         return guaranteesReturn(context, head.get(1));
  43:     }
  44:
  45:     private static boolean blockItemsGuaranteeReturn(ShiftReduceSemanticContext context, HeadNode head) {
  46:         if (head.size() == 0) {
  47:             return false;
  48:         }
  49:         if (head.size() == 1) {
> 50:             return guaranteesReturn(context, head.get(0));
  51:         }
  52:         return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
  53:     }
  54:
  55:     private static boolean statementGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
  56:         String symbol = head.getSymbol().toString();
  57:         if ("matched_stmt".equals(symbol) && head.size() == 1 && head.get(0).isHead()) {
  58:             HeadNode child = head.get(0).toHead();
  59:             if ("return_stmt".equals(child.getSymbol().toString())) {
  60:                 return true;
```

Comment 原文：

> 递归吗? 判断类型是否正确不要用递归啊, 完全不需要使用递归解析树啊, 明显就没有利用好类型判断的那个框架, 明显又是重新写了一遍架构

清洗后表述：

> `FunctionReturnFlowAnalyzer` 在这里也是同样的问题：它没有优先利用已有分析结果，而是重新依赖语法树递归做控制流判断。


#### 条目 28

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java)  
Review 快照行： `50`

代码上下文：

```java
  40:             return false;
  41:         }
  42:         return guaranteesReturn(context, head.get(1));
  43:     }
  44:
  45:     private static boolean blockItemsGuaranteeReturn(ShiftReduceSemanticContext context, HeadNode head) {
  46:         if (head.size() == 0) {
  47:             return false;
  48:         }
  49:         if (head.size() == 1) {
> 50:             return guaranteesReturn(context, head.get(0));
  51:         }
  52:         return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
  53:     }
  54:
  55:     private static boolean statementGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
  56:         String symbol = head.getSymbol().toString();
  57:         if ("matched_stmt".equals(symbol) && head.size() == 1 && head.get(0).isHead()) {
  58:             HeadNode child = head.get(0).toHead();
  59:             if ("return_stmt".equals(child.getSymbol().toString())) {
  60:                 return true;
```

Comment 原文：

> 判断类型是否匹配用递归, 即没有必要, 又造成效率的浪费, 还导致代码的可维护性为0

清洗后表述：

> 这类递归既没有必要，又会让性能、复用性和可维护性一起变差。


#### 条目 44

文件： [src/main/java/org/harvey/vie/theory/semantic/command/register/MergedCommandNodeRegister.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/register/MergedCommandNodeRegister.java)  
Review 快照行： `37`

代码上下文：

```java
  27:     }
  28:
  29:     @Override
  30:     public List<UncertainLabelGotoCommand> getUncertainContinues() {
  31:         return merge(primary.getUncertainContinues(), false);
  32:     }
  33:
  34:     private List<UncertainLabelGotoCommand> merge(List<UncertainLabelGotoCommand> seed, boolean breaks) {
  35:         List<UncertainLabelGotoCommand> result = new ArrayList<>(seed);
  36:         for (CommandNodeRegister extra : extras) {
> 37:             result.addAll(breaks ? extra.getUncertainBreaks() : extra.getUncertainContinues());
  38:         }
  39:         return result;
  40:     }
  41: }
  42:
```

Comment 原文：

> 递归? 合适吗? 没有更好的方法了吗?

清洗后表述：

> 这条 comment 的关注点是：合并 `CommandNodeRegister` 的方式是否仍然隐含递归展开，是否可以改成更直接、更清晰的结构化合并。


#### 条目 45

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ConstantConditionSupport.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ConstantConditionSupport.java)  
Review 快照行： `8`

代码上下文：

```java
  1: package org.harvey.vie.theory.semantic.command.translator.command;
  2:
  3: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  4: import org.harvey.vie.theory.semantic.type.TypeAttributes;
  5: import org.harvey.vie.theory.semantic.value.ConstantAttributes;
  6: import org.harvey.vie.theory.semantic.value.ConstantValue;
  7:
> 8: final class ConstantConditionSupport {
  9:     private ConstantConditionSupport() {
  10:     }
  11:
  12:     static Boolean booleanValue(ShiftReduceSemanticContext context, int childIndex) {
  13:         if (!ConstantAttributes.childIsConstant(context, childIndex)) {
  14:             return null;
  15:         }
  16:         ConstantValue value = ConstantAttributes.child(context, childIndex);
  17:         if (value == null || !value.getType().isBooleanScalar()) {
  18:             return null;
```

Comment 原文：

> 哦不是看源码判断常量值, 而是判断是不是可以推导出常量啊(我后面的Review的Comment有误了). 常量的推导也不应该使用递归. 类型推断也不应该使用递归, 两者应该是类似的处理逻辑.

清洗后表述：

> 这里确认了一个前提：判断的不是“源码里是不是字面量”，而是“能否在语义阶段推导成常量”。但常量推导和类型推导都不应重新走树递归，应有一致的处理框架。


### 2.5 Function 语义设计与职责边界

#### 条目 9

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSignature.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSignature.java)  
Review 快照行： `8`

代码上下文：

```java
  1: package org.harvey.vie.theory.semantic.function;
  2:
  3: import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
  4: import org.harvey.vie.theory.semantic.analysis.SemanticType;
  5: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  6:
  7: public class FunctionSignature {
> 8:     private final String name;
  9:     private final SourceToken nameToken;
  10:     private final SemanticType returnType;
  11:     private final HeadNode declarationNode;
  12:
  13:     public FunctionSignature(String name, SourceToken nameToken, SemanticType returnType, HeadNode declarationNode) {
  14:         this.name = name;
  15:         this.nameToken = nameToken;
  16:         this.returnType = returnType;
  17:         this.declarationNode = declarationNode;
  18:     }
```

Comment 原文：

> 有了nameToken为什么需要name? 这个nameToken是通过读取源码文件来的, 源码文件的编码方式不好说, 怎么能直接解析成Java的String的name呢?

清洗后表述：

> `FunctionSignature` 同时保存 `name` 和 `nameToken`，需要明确二者职责。如果内部键和值比较都依赖 `nameToken`，那么额外保存一个直接解码出来的 `String` 就很可疑。


#### 条目 20

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `61`

代码上下文：

```java
  51:     }
  52:
  53:     private String normalizeKey(String key) {
  54:         return key.replace("return_type", "type");
  55:     }
  56:
  57:     private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
  58:         SourceToken nameToken = head.get(1).toToken().getSource();
  59:         String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
  60:         if (context.existFunction(name)) {
> 61:             SemanticTypeDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
  62:         }
  63:         TypeRegister returnTypeRegister = context.getType(head.get(0));
  64:         if (returnTypeRegister == null) {
  65:             throw new CompilerException("function return type is missing.");
  66:         }
  67:         SemanticType returnType = returnTypeRegister.requireType("function return type is required.");
  68:         List<FunctionParameter> parameters = collectParameters(context, head.get(3));
  69:         FunctionRecord record = new FunctionRecord(
  70:                 new FunctionSignature(name, nameToken, returnType, head),
  71:                 parameters,
```

Comment 原文：

> 直接用SourceToken去比较啊, 比如创建SourceToken的Comparator

清洗后表述：

> 这里的重名判断更适合建立在 `SourceToken` 的稳定比较规则上，而不是先退化成 `String` 再查表。


#### 条目 22

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `112`

代码上下文：

```java
  102:                 valueType.requireType("return value type is required."),
  103:                 returnType,
  104:                 returnToken,
  105:                 "return value type does not match function return type."
  106:         );
  107:     }
  108:
  109:     private void validateCall(ShiftReduceSemanticContext context, HeadNode head) {
  110:         SourceToken nameToken = head.get(0).toToken().getSource();
  111:         String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
> 112:         FunctionRecord record = context.getFunction(name);
  113:         if (record == null) {
  114:             SemanticTypeDiagnostics.reject(context, nameToken, "function must be defined before it is called.");
  115:         }
  116:         List<TypeRegister> args = collectArgumentTypes(context, head.get(2));
  117:         if (args.size() != record.getParameters().size()) {
  118:             SemanticTypeDiagnostics.reject(context, nameToken, "function argument count does not match.");
  119:         }
  120:         for (int i = 0; i < args.size(); i++) {
  121:             SemanticType sourceType = args.get(i).requireType("argument type is required.");
  122:             SemanticType targetType = record.getParameters().get(i).getType();
```

Comment 原文：

> 经典UTF-8, 错误!

清洗后表述：

> 这里依赖 `UTF_8` 解码后再做函数查找，评审意见明确认为这种处理不可靠，也不应该成为函数名查找的基础。


#### 条目 24

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionContext.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionContext.java)  
Review 快照行： `12`

代码上下文：

```java
  2:
  3: import org.harvey.vie.theory.semantic.analysis.SemanticType;
  4:
  5: import java.util.ArrayDeque;
  6: import java.util.Collection;
  7: import java.util.LinkedHashMap;
  8: import java.util.Map;
  9: import java.util.Optional;
  10:
  11: public class FunctionContext {
> 12:     private final Map<String, FunctionRecord> functions = new LinkedHashMap<>();
  13:     private final ArrayDeque<FunctionRecord> functionStack = new ArrayDeque<>();
  14:     private final ArrayDeque<FunctionBodyState> bodyStateStack = new ArrayDeque<>();
  15:
  16:     public boolean exists(String name) {
  17:         return functions.containsKey(name);
  18:     }
  19:
  20:     public FunctionRecord get(String name) {
  21:         return functions.get(name);
  22:     }
```

Comment 原文：

> 直接String不太好. 是不是因为我在Identifier的符号表里也用String了? 其实不好, 应该用SourceToken作为Key的, 在SourceToken上OverrideHashcode和equals方法是比较好的做法, 而不是String

清洗后表述：

> `FunctionContext` 直接以 `String` 为 key，会把编码、比较规则和名字表示问题全部压扁。评审意见建议优先基于 `SourceToken` 建立稳定的相等性语义。


#### 条目 26

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java)  
Review 快照行： `8`

代码上下文：

```java
  1: package org.harvey.vie.theory.semantic.function;
  2:
  3: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  4: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  5: import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
  6: import org.harvey.vie.theory.semantic.value.ConstantValue;
  7:
> 8: public final class FunctionReturnFlowAnalyzer {
  9:     private FunctionReturnFlowAnalyzer() {
  10:     }
  11:
  12:     public static boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
  13:         if (node == null || !node.isHead()) {
  14:             return false;
  15:         }
  16:         HeadNode head = node.toHead();
  17:         String symbol = head.getSymbol().toString();
  18:         switch (symbol) {
```

Comment 原文：

> 判断Return的形式是否正确的分析器类是吗? 缺少注释补一补

清洗后表述：

> 这个类的职责需要写清楚。它到底是在做“return 语句合法性检查”、还是“函数体控制流是否保证返回”的分析，应该在注释里明确下来。


#### 条目 29

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionBodyState.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionBodyState.java)  
Review 快照行： `3`

代码上下文：

```java
  1: package org.harvey.vie.theory.semantic.function;
  2:
> 3: public class FunctionBodyState {
  4:     private final FunctionRecord function;
  5:
  6:     public FunctionBodyState(FunctionRecord function) {
  7:         this.function = function;
  8:     }
  9:
  10:     public FunctionRecord getFunction() {
  11:         return function;
  12:     }
  13: }
```

Comment 原文：

> 何意味?

清洗后表述：

> 这个状态对象目前过薄，存在理由不自明。需要说明它为什么不是直接用 `FunctionRecord`，或者为什么未来会承载额外函数体状态。


#### 条目 30

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionBodyState.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionBodyState.java)  
Review 快照行： `1`

代码上下文：

```java
> 1: package org.harvey.vie.theory.semantic.function;
  2:
  3: public class FunctionBodyState {
  4:     private final FunctionRecord function;
  5:
  6:     public FunctionBodyState(FunctionRecord function) {
  7:         this.function = function;
  8:     }
  9:
  10:     public FunctionRecord getFunction() {
  11:         return function;
```

Comment 原文：

> 何意味?

清洗后表述：

> 从类级别看，这个类也没有把自身必要性讲清楚：如果只是包一层 `FunctionRecord`，那它更像未完成的中间形态，而不是稳定设计。


#### 条目 31

文件： [src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java)  
Review 快照行： `208`

代码上下文：

```java
  198:     public IdentifierRecord[] scopeExist() {
  199:         return identifierTableBuilder.scopeExist();
  200:     }
  201:
  202:     public IdentifierRecord[] identifierRecords() {
  203:         return identifierRecords.toArray(IdentifierRecord[]::new);
  204:     }
  205:     // endregion
  206:
  207:     // region functions
> 208:     public boolean existFunction(String name) {
  209:         return functionContext.exists(name);
  210:     }
  211:
  212:     public FunctionRecord getFunction(String name) {
  213:         return functionContext.get(name);
  214:     }
  215:
  216:     public void registerFunction(FunctionRecord record) {
  217:         functionContext.register(record);
  218:     }
```

Comment 原文：

> 小问题: 就是可以再创建一个类专门管理Function的有关参数和方法, 这些函数全部写道Function的这个类里. 而不是家中ShiftReduceSemanticContext的压力. 不过小问题, 优先级不高

清洗后表述：

> 这部分 function 相关接口可以进一步从 `ShiftReduceSemanticContext` 中下沉出去，避免上下文对象继续膨胀。不过这是结构优化项，优先级低于主设计问题。


#### 条目 32

文件： [src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java)  
Review 快照行： `208`

代码上下文：

```java
  198:     public IdentifierRecord[] scopeExist() {
  199:         return identifierTableBuilder.scopeExist();
  200:     }
  201:
  202:     public IdentifierRecord[] identifierRecords() {
  203:         return identifierRecords.toArray(IdentifierRecord[]::new);
  204:     }
  205:     // endregion
  206:
  207:     // region functions
> 208:     public boolean existFunction(String name) {
  209:         return functionContext.exists(name);
  210:     }
  211:
  212:     public FunctionRecord getFunction(String name) {
  213:         return functionContext.get(name);
  214:     }
  215:
  216:     public void registerFunction(FunctionRecord record) {
  217:         functionContext.register(record);
  218:     }
```

Comment 原文：

> 小问题: 就是可以再创建一个类专门管理Function的有关参数和方法, 这些函数全部写道Function的这个类里. 而不是加重ShiftReduceSemanticContext的压力. 不过小问题, 优先级不高

清洗后表述：

> 同上。这条意见重复强调了 function 能力不应继续堆到 `ShiftReduceSemanticContext` 上。


### 2.6 名字表示、`SourceToken` 与文本解码

#### 条目 19

文件： [src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java)  
Review 快照行： `59`

代码上下文：

```java
  49:             default:
  50:         }
  51:     }
  52:
  53:     private String normalizeKey(String key) {
  54:         return key.replace("return_type", "type");
  55:     }
  56:
  57:     private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
  58:         SourceToken nameToken = head.get(1).toToken().getSource();
> 59:         String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
  60:         if (context.existFunction(name)) {
  61:             SemanticTypeDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
  62:         }
  63:         TypeRegister returnTypeRegister = context.getType(head.get(0));
  64:         if (returnTypeRegister == null) {
  65:             throw new CompilerException("function return type is missing.");
  66:         }
  67:         SemanticType returnType = returnTypeRegister.requireType("function return type is required.");
  68:         List<FunctionParameter> parameters = collectParameters(context, head.get(3));
  69:         FunctionRecord record = new FunctionRecord(
```

Comment 原文：

> 你怎么知道是UTF_8呢? 靠做梦吗?

清洗后表述：

> 这里直接假定源码 lexeme 应按 `UTF_8` 解码，缺少来源和保证；如果编码约束不在更前面被明确保证，这个假设就不成立。


补充说明：

- 条目 22 同时也属于“名字表示与解码链路”问题，因为函数查找这一段同样建立在 `UTF_8` 文本化之后。


#### 条目 46

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionCallTranslator.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionCallTranslator.java)  
Review 快照行： `42`

代码上下文：

```java
  32:
  33:     private String functionName(ShiftReduceSemanticContext context) {
  34:         if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
  35:             throw new CompilerException("current reduced head is absent for function call.");
  36:         }
  37:         HeadNode head = context.getTreeContext().peek().toHead();
  38:         ShiftReduceSyntaxTreeNode token = head.get(0);
  39:         if (!token.isToken()) {
  40:             throw new CompilerException("function call name is absent.");
  41:         }
> 42:         return new String(token.toToken().getSource().getLexeme(), StandardCharsets.UTF_8);
  43:     }
  44: }
  45:
```

Comment 原文：

> new String就是不对的

清洗后表述：

> `FunctionCallTranslator` 这里同样把 token 直接解码成 `String`。评审意见认为这类做法本身就不该继续出现。


### 2.7 常量推导与常量条件支持

#### 条目 34

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/WhileStatementTranslator.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/WhileStatementTranslator.java)  
Review 快照行： `44`

代码上下文：

```java
  34:         //    L1:
  35:         //    expr.command();
  36:         //    CommandFactory.ifn_goto(L2);
  37:         //    (matched_stmt|unmatched_stmt).command();
  38:         //    CommandFactory.goto(L1);
  39:         //    L2:
  40:
  41:         if (children.length != 5) {
  42:             throw new CompilerException("illegal statement on while statement production.");
  43:         }
> 44:         Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
  45:         if (Boolean.FALSE.equals(constantCondition)) {
  46:             return new PlaceholderNodeRegister();
  47:         }
  48:         SemanticTypeDiagnostics.requireBoolean(
  49:                 context,
  50:                 TypeAttributes.childType(context, 2),
  51:                 TypeAttributes.childAnchor(context, 0),
  52:                 "condition must be boolean."
  53:         );
  54:         CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
```

Comment 原文：

> 哦, 通过ConstantCondition来判断源码里是不是bool值啊, 可以. 但是这种写成一个工具类+静态方法不合适. 写成接口+实现类, 然后由于之前我对于源码中字面量的解析的实现是简单实现的, 也没有太注重实际. 既然现在语义分析阶段做大了, 就应该充分考虑设计了. 就应该做成接口+实现类, 目前的实现姑且称作Simple吧

清洗后表述：

> 常量条件支持这个方向本身是认可的，但现在的落地方式太临时。随着语义分析能力扩大，应该把它提升成正式接口和实现，而不是继续用静态工具类扩张。


#### 条目 43

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/DoWhileStatementTranslator.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/DoWhileStatementTranslator.java)  
Review 快照行： `43`

代码上下文：

```java
  33:         //    L1:
  34:         //    stmt.command();
  35:         //    L2:
  36:         //    expr.command();
  37:         //    CommandFactory.if_goto(L1);
  38:         //    L3:
  39:         if (children.length != 7) {
  40:             throw new CompilerException("illegal statement on do while statement production.");
  41:         }
  42:         Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 4);
> 43:         if (Boolean.FALSE.equals(constantCondition)) {
  44:             SemanticLabel whileStartLabel = new DefaultSemanticLabel();
  45:             SemanticLabel whileEndLabel = new DefaultSemanticLabel();
  46:             CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
  47:             thisBuilder.add(new LabelNode(whileStartLabel));
  48:             children[1].register(thisBuilder);
  49:             thisBuilder.add(new LabelNode(whileEndLabel));
  50:             WhileStatementTranslator.bindLoopLabels(children[1], whileEndLabel, whileEndLabel);
  51:             return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
  52:         }
  53:         SemanticTypeDiagnostics.requireBoolean(
```

Comment 原文：

> 可以, 抽取一个方法出来呢? 更清晰

清洗后表述：

> 这里的常量条件分支可以进一步提炼成单独方法，使 while / do-while 在常量条件处理上更统一、更清晰。


### 2.8 过渡性产生式合法性判断

#### 条目 35

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ProgramCommandTranslator.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ProgramCommandTranslator.java)  
Review 快照行： `24`

代码上下文：

```java
  14:  * @date 2026-04-21 15:29
  15:  */
  16: public class ProgramCommandTranslator implements CommandTranslator {
  17:     private final CommandTranslator delegate = new SimpleShrinkTranslator();
  18:
  19:     @Override
  20:     public CommandNodeRegister translate(
  21:             ShiftReduceSemanticContext context,
  22:             SimpleGrammarProduction production,
  23:             CommandNodeRegister[] children) {
> 24:         if (children.length != 1 && children.length != 2) {
  25:             throw new CompilerException("illegal statement on program production.");
  26:         }
  27:         CommandNodeRegister result = delegate.translate(context, production, children);
  28:         rejectUnresolved(context, result);
  29:         return result;
  30:     }
  31:
  32:     private void rejectUnresolved(ShiftReduceSemanticContext context, CommandNodeRegister result) {
  33:         boolean failed = false;
  34:         for (UncertainLabelGotoCommand gotoCommand : result.getUncertainBreaks()) {
```

Comment 原文：

> 对于children长度的判断不同... 额将来会使用新的方案来判断产生式是否正确, 因此现在姑且保留这种用法, 下阶段应当优先对这种判断方法进行改进

清洗后表述：

> 这里按 `children.length` 判断合法性的方式可以暂时保留，但它只是过渡设计，下个阶段应优先替换为更稳定的产生式识别机制。


### 2.9 命令生成与字符串命令体系

#### 条目 38

文件： [src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java)  
Review 快照行： `88`

代码上下文：

```java
  78:     }
  79:
  80:     public boolean canImplicitlyCastTo(SemanticType target) {
  81:         return equals(target) || isNumericScalar() && target.isNumericScalar();
  82:
  83:     }
  84:
  85:     public String mnemonic() {
  86:         // TODO 不合适的设计, 本质是解析字符串,
  87:         //  通过JDK的字符串在类之间互相传递是没有丝毫可维护性的做法
> 88:         switch (kind) {
  89:             case BOOLEAN:
  90:                 return "boolean";
  91:             case CHARACTER:
  92:                 return "character";
  93:             case INT32:
  94:                 return "int32";
  95:             case FLOAT64:
  96:                 return "float64";
  97:             case STRING:
  98:                 return "string";
```

Comment 原文：

> 这个是不合适的解析字符串的设计了, 将来也应该被淘汰掉.

清洗后表述：

> `SemanticType.mnemonic()` 把类型能力导出成字符串，评审意见认为这是不合适的过渡设计，长期看应被淘汰。


#### 条目 39

文件： [src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java)  
Review 快照行： `88`

代码上下文：

```java
  78:     }
  79:
  80:     public boolean canImplicitlyCastTo(SemanticType target) {
  81:         return equals(target) || isNumericScalar() && target.isNumericScalar();
  82:
  83:     }
  84:
  85:     public String mnemonic() {
  86:         // TODO 不合适的设计, 本质是解析字符串,
  87:         //  通过JDK的字符串在类之间互相传递是没有丝毫可维护性的做法
> 88:         switch (kind) {
  89:             case BOOLEAN:
  90:                 return "boolean";
  91:             case CHARACTER:
  92:                 return "character";
  93:             case INT32:
  94:                 return "int32";
  95:             case FLOAT64:
  96:                 return "float64";
  97:             case STRING:
  98:                 return "string";
```

Comment 原文：

> 为什么会有字符串的必要呢? 我不理解.

清洗后表述：

> 需要先说明“为什么语义类型需要转成字符串”，否则这一步就显得是无来源的职责扩张。


#### 条目 40

文件： [src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java)  
Review 快照行： `88`

代码上下文：

```java
  78:     }
  79:
  80:     public boolean canImplicitlyCastTo(SemanticType target) {
  81:         return equals(target) || isNumericScalar() && target.isNumericScalar();
  82:
  83:     }
  84:
  85:     public String mnemonic() {
  86:         // TODO 不合适的设计, 本质是解析字符串,
  87:         //  通过JDK的字符串在类之间互相传递是没有丝毫可维护性的做法
> 88:         switch (kind) {
  89:             case BOOLEAN:
  90:                 return "boolean";
  91:             case CHARACTER:
  92:                 return "character";
  93:             case INT32:
  94:                 return "int32";
  95:             case FLOAT64:
  96:                 return "float64";
  97:             case STRING:
  98:                 return "string";
```

Comment 原文：

> 哦哦, 为了构建StringCommand. 那构建StringCommand的这一整套都应该放到StringCommandFactory里去, 而不是放在SemanticType!

清洗后表述：

> 如果字符串化是为了构建 `StringCommand`，那这套责任应收拢到命令工厂层，而不应反向塞进 `SemanticType`。


#### 条目 41

文件： [src/main/java/org/harvey/vie/theory/semantic/command/command/CommandFactory.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/command/CommandFactory.java)  
Review 快照行： `72`

代码上下文：

```java
  62:
  63:     public static SemanticCommand ifnGoto(SemanticLabel label) {
  64:         return TYPED.ifnGoto(label);
  65:     }
  66:
  67:     public static SemanticCommand gotoCommand(SemanticLabel label) {
  68:         return TYPED.gotoCommand(label);
  69:     }
  70:
  71:     public static SemanticCommand callFunction(String name) {
> 72:         return new StringCommand("call " + name);
  73:     }
  74:
  75:     public static SemanticCommand returnCommand() {
  76:         return new StringCommand("return");
  77:     }
  78:
  79:
  80:     public static UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
  81:         return TYPED.gotoCommandUncertainLabel(token);
  82:     }
```

Comment 原文：

> 所有的CommandFactory, 包括TypedCommandFactory, 都是直接一个类的, 没有实现类和接口, 都是不好的, 需要改成抽象+实现. 然后目前的StringCommand, 就应该配套StringFactory

清洗后表述：

> `CommandFactory` / `TypedCommandFactory` / `StringCommand` 这套关系现在过于扁平，评审意见希望把它整理成更清楚的抽象与实现分层，并为字符串命令提供专门工厂。


#### 条目 42

文件： [src/main/java/org/harvey/vie/theory/semantic/command/command/CommandFactory.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/command/CommandFactory.java)  
Review 快照行： `72`

代码上下文：

```java
  62:
  63:     public static SemanticCommand ifnGoto(SemanticLabel label) {
  64:         return TYPED.ifnGoto(label);
  65:     }
  66:
  67:     public static SemanticCommand gotoCommand(SemanticLabel label) {
  68:         return TYPED.gotoCommand(label);
  69:     }
  70:
  71:     public static SemanticCommand callFunction(String name) {
> 72:         return new StringCommand("call " + name);
  73:     }
  74:
  75:     public static SemanticCommand returnCommand() {
  76:         return new StringCommand("return");
  77:     }
  78:
  79:
  80:     public static UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
  81:         return TYPED.gotoCommandUncertainLabel(token);
  82:     }
```

Comment 原文：

> 目前的StringCommand, 就应该配套StringCommandFactory

清洗后表述：

> 至少在当前阶段，`StringCommand` 应配套一个明确的 `StringCommandFactory`，避免字符串命令拼接散落在工厂外部。


### 2.10 其余局部意见

#### 条目 33

文件： [src/main/java/org/harvey/vie/theory/semantic/command/SemanticResultCallback.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/SemanticResultCallback.java)  
Review 快照行： `1`

代码上下文：

```java
> 1: package org.harvey.vie.theory.semantic.command;
  2:
  3: import org.harvey.vie.theory.exception.CompilerException;
  4: import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
  5: import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
  6: import org.harvey.vie.theory.semantic.command.node.CommandContext;
  7: import org.harvey.vie.theory.semantic.command.node.CommandNode;
  8: import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
  9: import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
  10: import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
  11: import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
```

Comment 原文：

> 测试过了应该就没问题吧?

清洗后表述：

> 这条 comment 不是指出明确问题，而是在表示：如果相关测试已经覆盖并通过，这块暂时可以视为稳定。


#### 条目 47

文件： [src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionReturnTranslator.java](/D:/IT_study/source/temp/compiler-front-theory/src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionReturnTranslator.java)  
Review 快照行： `1`

代码上下文：

```java
> 1: package org.harvey.vie.theory.semantic.command.translator.command;
  2:
  3: import org.harvey.vie.theory.exception.CompilerException;
  4: import org.harvey.vie.theory.semantic.command.command.CommandFactory;
  5: import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
  6: import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
  7: import org.harvey.vie.theory.semantic.command.node.TerminalNode;
  8: import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
  9: import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
  10: import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
  11: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
```

Comment 原文：

> 应该没有问题

清洗后表述：

> 当前 review 没有在 `FunctionReturnTranslator` 上提出具体问题，可暂视为通过项。


## 3. 接下来要做什么

### P0. 先收束 function 语义的整体设计

- 明确 function declaration、function body、return 校验、call 校验分别属于哪一层。
- 决定 `FunctionSemanticCallback` 是否还应继续承担现在这一整套职责。
- 明确 `FunctionBodyState` 是否保留；若保留，要说明它未来真正承载的状态。


### P1. 把“语义动作识别”从字符串耦合里拆出来

- 优先处理 `FunctionSemanticCallback`、`FunctionReturnFlowAnalyzer`、`IdentifierScopeCallback`、`ConstantValueBuildCallback`。
- 停止继续依赖 `production.toString()`、`head.getSymbol().toString()`、`replace("return_type", "type")` 这类补丁式识别。
- 建立稳定的中间识别层，允许多个候选体共用同类语义动作，也允许必要时单独区分。


### P2. 清掉手写树递归，回到框架主链

- 参数收集、实参收集、return flow、常量条件判断都重新审视输入来源。
- 能直接从已有寄存结果读取的，就不要再扫语法树。
- 如果必须做结构收集，就做成框架内的正式能力，而不是 callback 私货。


### P3. 统一名字表示与比较规则

- 明确 `SourceToken`、内部 key、展示字符串三者边界。
- 停止新增 `new String(token.getLexeme(), StandardCharsets.UTF_8)`。
- 决定函数名、标识符名、查表 key 是否建立在 `SourceToken` 上，或者建立在更前面已稳定规范化过的名字对象上。


### P4. 把常量推导提升成正式子系统

- 让常量推导和类型推导的接口风格一致。
- while / do-while / if 对常量条件的利用，统一走正式入口。
- 处理好“可推导为常量”和“源码字面量”之间的区别。


### P5. 收束字符串命令的责任边界

- `StringCommand`、`StringCommandFactory`、`CommandFactory`、`TypedCommandFactory` 的边界要明确。
- 不再让 `SemanticType` 承担命令字符串拼接所需的职责。
- 字符串命令生成统一回收到工厂层。


### P6. 补齐文档和测试资产

- 在全链路文档里补全已支持的启动参数及其用法。
- 给测试样例补上“测试目标”注释。
- 继续保持 markdown 文档集中在 `docs` 目录。


### P7. 替换 `children.length` 的过渡方案

- 这件事可以排在主设计问题之后。
- 但既然已经确认只是过渡做法，就不要继续扩散到更多 translator。


## 4. 建议执行顺序

1. 先收束 function 语义设计与 `FunctionSemanticCallback` 的挂载方式。
2. 同步处理字符串驱动分发和手写树递归这两个主问题。
3. 再统一名字表示与查找比较规则。
4. 之后把常量推导和字符串命令体系分别收束成正式子系统。
5. 最后集中补文档、测试说明和过渡性判断替换。


## 5. 一句话结论

这次 review 不是在指出一堆互不相关的小毛病，而是在反复指向同一个核心问题：

- 这批 function / constant / command 相关实现，已经超出了“靠局部补丁继续堆”的阶段。
- 接下来如果还继续依赖字符串判断、手写递归和临时工具类，后面只会越来越乱。
- 正确方向是先把识别入口、职责边界、名字表示和正式子系统收束好，再继续扩功能。
