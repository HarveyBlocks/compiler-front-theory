# PR Review Report
**PR**: HarveyBlocks/compiler-front-theory#6  |  Context lines: ±10

## Code Review Comments

### 全链路说明.md

- **Line**: 1

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

> 现在有代码如        private static final boolean FLUSH_TABLE = Boolean.getBoolean("lexical.flushTable"); 其是从启动参数里取值作为配置的. 那么就有必要在这个文件种说明所有被应用的启动项配置以及相关用法

---

### src/main/resources/program-tests/text24-if-true-inline.txt

- **Line**: 1

```
> 1: {
  2: int32 a;
  3: a = 1;
  4: if (true) a = 2;
  5: a = 3;
  6: }
  7: 
```

> 这些测试用例里都补一下注释, 说明一下都是测试哪些功能的.

---

### grammar0_全链路说明.md

- **Line**: 1

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

> 除了Readme以外的markdown文件, 都放到docs目录下统一管理, 以后文件说不定会变多

---

### src/main/java/org/harvey/vie/theory/semantic/value/ConstantValueBuildCallback.java

- **Line**: 120

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

> 这是干什么? 什么目的?

- **Line**: 140

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

> 气笑了

---

### src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java

- **Line**: 1

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

> 和theory/semantic/value/ConstantValueBuildCallback.java什么关系? 为啥有两个?

- **Line**: 1

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

> 哦哦, 一个是Type, 一个是Constant, 懂了. 两个逻辑挺类似的

---

### src/main/java/org/harvey/vie/theory/semantic/identifier/IdentifierScopeCallback.java

- **Line**: 60

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

> 字符串判断

---

### src/main/java/org/harvey/vie/theory/semantic/function/FunctionSignature.java

- **Line**: 8

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

> 有了nameToken为什么需要name? 这个nameToken是通过读取源码文件来的, 源码文件的编码方式不好说, 怎么能直接解析成Java的String的name呢?

---

### src/main/java/org/harvey/vie/theory/semantic/function/FunctionSemanticCallback.java

- **Line**: 182

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

> 解析字符串了

- **Line**: 190

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

> 怎么还有递归?????

- **Line**: 164

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

> 递归的问题

- **Line**: 164

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

> 递归不要用栈+循环消解, 这个框架本身就能消解掉递归的, 本身就不应该使用递归, 使用了递归就说明彻底没有理解框架的作用, 相当于自己又写了一套效率低, 没法复用, 扩展性差的代码

- **Line**: 148

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

> 解析字符串的问题

- **Line**: 148

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

> 实际上, 除了toString本身可以调用下一层模块的toString, 代码的其他任何地方都不应该出现对toString的调用

- **Line**: 31

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

> ShiftReduceCallback  不是你这么用的

- **Line**: 31

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

> 充满了对框架的误解

- **Line**: 54

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

> 何意味, 如果不replace会怎么样的? 这种改发难道是为了解决问题吗? 难道不是为了糊弄测试吗?

- **Line**: 59

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

> 你怎么知道是UTF_8呢? 靠做梦吗?

- **Line**: 61

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

> 直接用SourceToken去比较啊, 比如创建SourceToken的Comparator

- **Line**: 31

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

> 这种针对于产生式的, 都已经不是和ShiftReduceCallback同一个层级的了, 还要强行套用ShiftReduce就是大错特错. 就是除了ShiftReduceCallback不知道其他更合适的框架提供的接口导致的

- **Line**: 112

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

> 经典UTF-8, 错误!

- **Line**: 18

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

> 这种针对于产生式的, 都已经不是和ShiftReduceCallback同一个层级的了, 还要强行套用ShiftReduce就是大错特错. 就是除了ShiftReduceCallback不知道其他更���适的框架提供的接口导致的

---

### src/main/java/org/harvey/vie/theory/semantic/function/FunctionContext.java

- **Line**: 12

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

> 直接String不太好. 是不是因为我在Identifier的符号表里也用String了? 其实不好, 应该用SourceToken作为Key的, 在SourceToken上OverrideHashcode和equals方法是比较好的做法, 而不是String

---

### src/main/java/org/harvey/vie/theory/semantic/function/FunctionReturnFlowAnalyzer.java

- **Line**: 19

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

> 依旧解析字符串

- **Line**: 8

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

> 判断Return的形式是否正确的分析器类是吗? 缺少注释补一补

- **Line**: 50

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

> 递归吗? 判断类型是否正确不要用递归啊, 完全不需要使用递归解析树啊, 明显就��没有利用好类型判断的那个框架, 明显又是重新写了一遍架构

- **Line**: 50

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

> 判断类型是否匹配用递归, 即没有必要, 又造成效率的浪费, 还导致代码的可维护性为0

---

### src/main/java/org/harvey/vie/theory/semantic/function/FunctionBodyState.java

- **Line**: 3

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

> 何意味?

- **Line**: 1

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

> 何意味?

---

### src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java

- **Line**: 208

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

> 小问题: 就是可以再创建一个类专门管理Function的有关参数和方法, 这些函数全部写道Function的这个类里. 而不是家中ShiftReduceSemanticContext的压力. 不过小问题, 优先级不高

- **Line**: 208

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

> 小问题: 就是可以再创建一个类专门管理Function的有关参数和方法, 这些函数全部写道Function的这个类里. 而不是加重ShiftReduceSemanticContext的压力. 不过小问题, 优先级不高

---

### src/main/java/org/harvey/vie/theory/semantic/command/SemanticResultCallback.java

- **Line**: 1

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

> 测试过了应该就没问题吧?

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/WhileStatementTranslator.java

- **Line**: 44

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

> 哦, 通过ConstantCondition来判断源码里是不是bool值啊, 可以. 但是这种写成一个工具类+静态方法不合适. 写成接口+实现类, 然后由于之前我对于源码中字面量的解析的实现是简单实现的, 也没有太注重实际. 既然现在语义分析阶段做大了, 就应该充分考虑设计了. 就应该做成接口+实现类, 目前的实现姑且称作Simple吧

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ProgramCommandTranslator.java

- **Line**: 24

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

> 对于children长度的判断不同... 额将来会使用新的方案来判断产生式是否正确, 因此现在姑且保留这种用法, 下阶段应当优先对这种判断方法进行改进

---

### src/main/java/org/harvey/vie/theory/demo/program/ProgramLexicalDemo.java

- **Line**: 59

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

> 这里采用了命令行参数, 那么命令行如何使用也应该再文档中补充

---

### src/main/java/org/harvey/vie/theory/demo/SyntaxDemo.java

- **Line**: 143

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

> 命令行参数, 同上.

---

### src/main/java/org/harvey/vie/theory/semantic/analysis/SemanticType.java

- **Line**: 88

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

> 这个是不合适的解析字符串的设计了, 将来也应该被淘汰掉.

- **Line**: 88

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

> 为什么会有字符串的必要呢? 我不理解.

- **Line**: 88

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

> 哦哦, 为了构建StringCommand. 那构建StringCommand的这一整套都应该放到StringCommandFactory里去, 而不是放在SemanticType!

---

### src/main/java/org/harvey/vie/theory/semantic/command/command/CommandFactory.java

- **Line**: 72

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

> 所有的CommandFactory, 包括TypedCommandFactory, 都是直接一个类的, 没有实现类和接口, 都是不好的, 需要改成抽象+实现. 然后目前的StringCommand, 就应该配套StringFactory

- **Line**: 72

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

> 目前的StringCommand, 就应该配套StringCommandFactory

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/DoWhileStatementTranslator.java

- **Line**: 43

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

> 可以, 抽取一个方法出来呢? 更清晰

---

### src/main/java/org/harvey/vie/theory/semantic/command/register/MergedCommandNodeRegister.java

- **Line**: 37

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

> 递归? 合适吗? 没有更好的方法了吗?

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ConstantConditionSupport.java

- **Line**: 8

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

> 哦不是看源码判断常量值, 而是判断是不是可以推导出常量啊(我后面的Review的Comment有误了). 常量的推导也不应该使用递归. 类型推断也不应该使用递归, 两者应该是类似的处理逻辑.

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionCallTranslator.java

- **Line**: 42

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

> new String就是不对的

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionReturnTranslator.java

- **Line**: 1

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

> 应该没有问题

---

*Report generated by gh-review2md*