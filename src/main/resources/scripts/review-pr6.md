# PR Review Report
**PR**: HarveyBlocks/compiler-front-theory#6  |  Context lines: ±10

## Code Review Comments

### 全链路说明.md


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

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/WhileStatementTranslator.java

- **Line**: 44

```java
  34:         //    L1:
  35:         //    expr.command();
  36:         //    DefaultCommandFactory.ifn_goto(L2);
  37:         //    (matched_stmt|unmatched_stmt).command();
  38:         //    DefaultCommandFactory.goto(L1);
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

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/DoWhileStatementTranslator.java

- **Line**: 43

```java
  33:         //    L1:
  34:         //    stmt.command();
  35:         //    L2:
  36:         //    expr.command();
  37:         //    DefaultCommandFactory.if_goto(L1);
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

*Report generated by gh-review2md*