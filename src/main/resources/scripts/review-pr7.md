# PR Review Report
**PR**: HarveyBlocks/compiler-front-theory#7  |  Context lines: ±10

## PR Comments

**HarveyBlocks** commented on 2026-05-25T18:06:44Z:

Review完了, 我明天把重要性优先级排一下. 其实写的代码不是很多, 主要是花大把的时间纠结于问题出在哪了

---

## Code Review Comments

### src/main/java/org/harvey/vie/theory/syntax/grammar/produce/DefineSimpleGrammarProduction.java

- **Line**: 1

```java
> 1: package org.harvey.vie.theory.syntax.grammar.produce;
  2: 
  3: import lombok.AllArgsConstructor;
  4: import lombok.EqualsAndHashCode;
  5: import lombok.Getter;
  6: import org.harvey.vie.theory.io.Loaders;
  7: import org.harvey.vie.theory.io.Storages;
  8: import org.harvey.vie.theory.syntax.grammar.symbol.*;
  9: import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;
  10: 
  11: import java.io.IOException;
```

> 不应该改, 因为我们就是要求tags是升序的, 在Factory构造的时候已经决定了, 但是这里却乱搞

---

### src/main/java/org/harvey/vie/theory/semantic/type/TypeBuildCallback.java

- **Line**: 1

```java
> 1: package org.harvey.vie.theory.semantic.type;
  2: 
  3: import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
  4: import org.harvey.vie.theory.exception.CompilerException;
  5: import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
  6: import org.harvey.vie.theory.semantic.array.ArrayCreationDimensions;
  7: import org.harvey.vie.theory.semantic.command.LocationKind;
  8: import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
  9: import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
  10: import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
  11: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
```

> TypeBuildCallback任务太重了, 但是小问题, 现在改, 顶多提取几个static方法出来放到一个类里这种愚蠢的改法

---

### src/main/java/org/harvey/vie/theory/semantic/command/LocationKind.java

- **Line**: 1

```java
> 1: package org.harvey.vie.theory.semantic.command;
  2: 
  3: /**
  4:  * Semantic location kind for command generation.
  5:  *
  6:  * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
  7:  * @version 1.0
  8:  * @date 2026-05-25 17:10
  9:  */
  10: public enum LocationKind {
  11:     ADDRESS,
```

> 不是很懂, 这有什么好区分的? 

---

### src/main/java/org/harvey/vie/theory/semantic/context/ShiftReduceSemanticContext.java

- **Line**: 385

```java
  375:     public void registerStruct(StructRecord record) {
  376:         structContext.register(record);
  377:     }
  378: 
  379:     public void requireDeclaredType(SemanticType type, SourceToken anchor, String message) {
  380:         if (type != null && type.getNamedTypeKey() != null && getStruct(type) == null) {
  381:             addError(anchor.getOffset(), message);
  382:             throw new CompilerException(message);
  383:         }
  384:     }
> 385: 
  386:     public void bindType(ShiftReduceSyntaxTreeNode node, TypeRegister register) {
  387:         typeContext.bind(node, register);
  388:     }
  389: 
  390:     public TypeRegister getType(ShiftReduceSyntaxTreeNode node) {
  391:         return typeContext.get(node);
  392:     }
  393: 
  394:     public boolean hasType(ShiftReduceSyntaxTreeNode node) {
  395:         return typeContext.has(node);
```

> 我怀疑存在大量函数是反复修改函数参数作用域的时候的错误猜测导致的, 没清理干净导致的

- **Line**: 307

```java
  297:             SourceToken nameToken = parameter.getNameToken();
  298:             if (existIdentifier(nameToken)) {
  299:                 addError(nameToken.getOffset(), "duplicate identifier declaration is not allowed.");
  300:                 throw new CompilerException("duplicate identifier declaration is not allowed.");
  301:             }
  302:             registerIdentifier(parameter.getTypeNode(), parameter.getType(), nameToken, true, null);
  303:         }
  304:     }
  305: 
  306:     public void markPendingStructBody() {
> 307:         pendingStructBraceDepth++;
  308:     }
  309: 
  310:     public boolean consumePendingStructBody() {
  311:         if (pendingStructBraceDepth <= 0) {
  312:             return false;
  313:         }
  314:         pendingStructBraceDepth--;
  315:         return true;
  316:     }
  317: 
```

> 我不好说到底是不是简单的实现, 是不是特判, 是不是修复函数错误过程中导致的遗留

---

### src/main/java/org/harvey/vie/theory/semantic/structure/StructFieldStepper.java

- **Line**: 1

```java
> 1: package org.harvey.vie.theory.semantic.structure;
  2: 
  3: import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
  4: import org.harvey.vie.theory.semantic.sequence.SequnceStep;
  5: import org.harvey.vie.theory.semantic.sequence.Stepper;
  6: import org.harvey.vie.theory.semantic.tree.node.HeadNode;
  7: 
  8: /**
  9:  * Iterates list-shaped struct field syntax nodes.
  10:  *
  11:  * @author Temper
```

> 居然学会用工具了, 开智了?

---

### src/main/java/org/harvey/vie/theory/semantic/structure/StructRecord.java

- **Line**: 30

```java
  20:     private final HeadNode declarationNode;
  21: 
  22:     public StructRecord(SourceToken nameToken, List<StructField> fields, HeadNode declarationNode) {
  23:         this.nameToken = nameToken;
  24:         this.nameKey = IdentifierKey.generate(nameToken);
  25:         this.fields = List.copyOf(fields);
  26:         this.declarationNode = declarationNode;
  27:     }
  28: 
  29:     public StructField field(SourceToken token) {
> 30:         for (StructField field : fields) {
  31:             if (field.isNamed(token)) {
  32:                 return field;
  33:             }
  34:         }
  35:         return null;
  36:     }
  37: }
  38: 
```

> 有点简单粗暴, 不过作为初步实现还行吧

---

### src/main/java/org/harvey/vie/theory/semantic/identifier/IdentifierScopeCallback.java

- **Line**: 38

```java
  28: public class IdentifierScopeCallback implements ShiftReduceCallback {
  29:     private final ShiftPredicate scopeIntoPredicate;
  30:     private final ReducePredicate scopeExistPredicate;
  31:     private final ReducePredicate functionBodyBlockPredicate = TagReducePredicateFactory.predicate(
  32:             ProgramSemanticTag.BLOCK,
  33:             ProgramSemanticTag.COMMAND
  34:     );
  35: 
  36:     @Override
  37:     public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
> 38:         if (token.getType() == ProgramTokenType.KEYWORD_STRUCT) {
  39:             context.markPendingStructBody();
  40:         }
  41:         // token
  42:         if (scopeIntoPredicate.test(token)) {
  43:             if (context.consumePendingStructBody()) {
  44:                 ShiftReduceCallback.super.onShift(context, nextStatus, token);
  45:                 return;
  46:             }
  47:             context.scopeIntoBlock();
  48:         }
```

> 哦, 冲突了就在移进的时候进行特判啊, 我还以为是怎么解决的呢, 搞了半天还是特判啊. 通过脆弱的判断是否进入struct上下文, 来进行特判. 额...原来判定作用域的方法其实也是简单实现的, 不是很好, 结果现在被长期使用���, 还建立在原来的不好实现上继续往下做了不少.....

---

### docs/修改报告.md

- **Line**: 511

```markdown
  501: ### 全量验证命令
  502: 
  503: ```powershell
  504: mvn --% -q -Dsyntax.flushTable=true test
  505: ```
  506: 
  507: 本次已经实际执行并通过。
  508: 
  509: ---
  510: 
> 511: ## 这 7 个小时主要花在哪里
  512: 
  513: 如果只看最后落在代码里的 diff，确实不算大。  
  514: 但这段时间实际消耗主要在下面这些工作上：
  515: 
  516: ### 1. 梳理边界，而不是乱改
  517: 
  518: 花时间确认：
  519: 
  520: - 数组类型是否和长度绑定
  521: - 结构体是否按引用对象处理
```

> 居然还有七个小时

---

### src/main/java/org/harvey/vie/theory/semantic/type/TypeRegister.java

- **Line**: 19

```java
  9: /**
  10:  * Semantic type attribute bound to a syntax node.
  11:  * @author Temper
  12:  */
  13: @Getter
  14: @AllArgsConstructor
  15: public class TypeRegister {
  16:     private final SemanticType type;
  17:     private final SemanticType instructionType;
  18:     private final SourceToken anchorToken;
> 19:     private final LocationKind locationKind;
  20: 
  21:     public static TypeRegister simple(SemanticType type, SourceToken anchorToken) {
  22:         return new TypeRegister(type, type, anchorToken, null);
  23:     }
  24: 
  25:     public static TypeRegister typed(SemanticType type, SemanticType instructionType, SourceToken anchorToken) {
  26:         return new TypeRegister(type, instructionType, anchorToken, null);
  27:     }
  28: 
  29:     public static TypeRegister located(
```

> 真的有必要通过LocationKind来判断局部变量表的类型吗? 为什么是LocationKind? 那所有的int32, int64全部都是一样的, 为什么要有LocationKind? 我说addresss和reference容易搞混是在中间代码生成那里, 这里怎么会搞混? 对于引用类型自��就是引用, 局部变量表里存的自然是值啊, 只不过这个值是地址reference罢了. 然后这个address是在作为左值的时候, 需要写局部变量表里的变量值, 因此是address. 为什么要LocationKind, 解决的都不是同一个问题! 

---

### src/main/java/org/harvey/vie/theory/semantic/type/SemanticType.java

- **Line**: 19

```java
  9: import java.util.Objects;
  10: import java.util.StringJoiner;
  11: 
  12: 
  13: /**
  14:  * @author Temper
  15:  */
  16: @Getter
  17: public final class SemanticType {
  18:     public enum Kind {
> 19:         BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, VOID, STRUCT, NULL;
  20:     }
  21: 
  22:     private final Kind kind;
  23:     private final List<Integer> dimensions;
  24:     private final IdentifierKey namedTypeKey;
  25: 
  26:     private SemanticType(Kind kind, List<Integer> dimensions, IdentifierKey namedTypeKey) {
  27:         this.kind = kind;
  28:         this.dimensions = List.copyOf(dimensions);
  29:         this.namedTypeKey = namedTypeKey;
```

> 为何不使用继承, 而是要在原来的代码上反复使用? 要知道int32, float64这种基本数据类型永远都不会用到namedType, 这就说明namedType不应该放到这里来!

- **Line**: 19

```java
  9: import java.util.Objects;
  10: import java.util.StringJoiner;
  11: 
  12: 
  13: /**
  14:  * @author Temper
  15:  */
  16: @Getter
  17: public final class SemanticType {
  18:     public enum Kind {
> 19:         BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, VOID, STRUCT, NULL;
  20:     }
  21: 
  22:     private final Kind kind;
  23:     private final List<Integer> dimensions;
  24:     private final IdentifierKey namedTypeKey;
  25: 
  26:     private SemanticType(Kind kind, List<Integer> dimensions, IdentifierKey namedTypeKey) {
  27:         this.kind = kind;
  28:         this.dimensions = List.copyOf(dimensions);
  29:         this.namedTypeKey = namedTypeKey;
```

> 但是分开来其实有过度设计的嫌疑, 因为只有一个identifierKey有差. 但是考虑到将来的扩展性的话, 那肯定是要分开的. 

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/WhileStatementTranslator.java

- **Line**: 31

```java
  21:  * @version 1.0
  22:  * @date 2026-04-21 00:35
  23:  */
  24: public class WhileStatementTranslator implements CommandTranslator {
  25:     @Override
  26:     public CommandNodeRegister translate(
  27:             ShiftReduceSemanticContext context,
  28:             SimpleGrammarProduction production,
  29:             CommandNodeRegister[] children) {
  30:         if (children.length != 5) {
> 31:             throw new org.harvey.vie.theory.exception.CompilerException(
  32:                     "illegal statement on while statement production."
  33:             );
  34:         }
  35:         // while ( expr ) stmt
  36:         // while 循环语句
  37:         //    L1:
  38:         //    expr.command();
  39:         //    DefaultCommandFactory.ifn_goto(L2);
  40:         //    (matched_stmt|unmatched_stmt).command();
  41:         //    DefaultCommandFactory.goto(L1);
```

> 我都把length判断给优化掉了, 现在检验tag了, 为什么又改回来了????

- **Line**: 31

```java
  21:  * @version 1.0
  22:  * @date 2026-04-21 00:35
  23:  */
  24: public class WhileStatementTranslator implements CommandTranslator {
  25:     @Override
  26:     public CommandNodeRegister translate(
  27:             ShiftReduceSemanticContext context,
  28:             SimpleGrammarProduction production,
  29:             CommandNodeRegister[] children) {
  30:         if (children.length != 5) {
> 31:             throw new org.harvey.vie.theory.exception.CompilerException(
  32:                     "illegal statement on while statement production."
  33:             );
  34:         }
  35:         // while ( expr ) stmt
  36:         // while 循环语句
  37:         //    L1:
  38:         //    expr.command();
  39:         //    DefaultCommandFactory.ifn_goto(L2);
  40:         //    (matched_stmt|unmatched_stmt).command();
  41:         //    DefaultCommandFactory.goto(L1);
```

> 我希望一个理由, 而不是直接改回去, 我要一个坚持使用length判断的理由

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/StatementListTranslator.java

- **Line**: 24

```java
  14:  * @version 1.0
  15:  * @date 2026-04-21 00:22
  16:  */
  17: public class StatementListTranslator implements CommandTranslator {
  18:     @Override
  19:     public CommandNodeRegister translate(
  20:             ShiftReduceSemanticContext context,
  21:             SimpleGrammarProduction production,
  22:             CommandNodeRegister[] children) {
  23:         if (children.length == 0) {
> 24:             return new org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister();
  25:         }
  26:         if (children.length == 1) {
  27:             return children[0];
  28:         }
  29:         CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
  30:         children[0].register(thisBuilder);
  31:         children[1].register(thisBuilder);
  32:         return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
  33:     }
  34: }
```

> 我都把length判断给优化掉了, 现在检验tag了, 为什么又改回来了????

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/PrimaryProduceLeftValueTranslator.java

- **Line**: 34

```java
  24:         CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
  25:         if (constant != null) {
  26:             return constant;
  27:         }
  28:         CommandNodeBuilder builder = new CommandNodeListBuilder();
  29:         children[0].register(builder);
  30:         var register = TypeAttributes.childHasType(context, 0)
  31:                 ? TypeAttributes.child(context, 0)
  32:                 : TypeAttributes.result(context);
  33:         LocationKind locationKind = register.getLocationKind();
> 34:         if (locationKind == LocationKind.REFERENCE) {
  35:             builder.add(new TerminalNode(context.getCommandFactory().stTopRefToVal(register.requireType(
  36:                     "semantic type is required for left value."
  37:             ))));
  38:         } else {
  39:             builder.add(new TerminalNode(context.getCommandFactory().stTopAddrToVal(register.requireType(
  40:                     "semantic type is required for left value."
  41:             ))));
  42:         }
  43:         return new NormalCommandNodeRegister(builder.build(), production, children);
  44:     }
```

> .address专门用在左值的, reference是用在引用对象上的.这两个东西都不是 一个level上的问题, 为什么要放到一起, 作为一个对称的来使用? 作为对称来使用一定错误了, 因为要解决的不是同一个问题. 
> 
> 比如 obj.value = 2. 应该是
> ```text
> st_load_static_int32 2  ;加载常量2栈顶, 由于赋值是右结合的, 因此先加载右值的表达式
> st_load_reference 0     ;加载局部变量表里偏移量为0的引用变量到���顶, 因为obj在局部变量表里的offset是0
> st_load_static_int32 0  ;加载常量0到栈顶, 因为value的offset是0
> st_get_address            ;栈顶的两个元素做一个"取值"的操作, 取出的是一个左值(为赋值做准备), 放入栈顶
> st_assign                     ;栈顶的两个元素做一个赋值
> ```

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/MemberAccessTranslator.java

- **Line**: 45

```java
  35:             );
  36:         }
  37:         StructRecord struct = context.getStruct(baseType);
  38:         if (struct == null) {
  39:             SemanticDiagnostics.reject(
  40:                     context,
  41:                     TypeAttributes.childAnchor(context, 0),
  42:                     "member access requires a declared struct operand."
  43:             );
  44:         }
> 45:         StructField field = struct.field(TypeAttributes.childAnchor(context, 2));
  46:         builder.add(new TerminalNode(context.getCommandFactory().biasFromStTopToRef(field.getType(), field.getOffset())));
  47:         return new NormalCommandNodeRegister(builder.build(), production, children);
  48:     }
  49: }
  50: 
```

> 告警说struct可能为null. 这边建议
> ```java
> if (struct == null) {
>     SemanticDiagnostics.reject(
>             context,
>             TypeAttributes.childAnchor(context, 0),
>              "member access requires a declared struct operand."
>      );
> }
> ```
> ```java
> SemanticDiagnostics.requireNonNull(
>     struct,    
>     context,
>     TypeAttributes.childAnchor(context, 0),
>     "member access requires a declared struct operand."
> );
> ```
> 其他的还可以有requireTrue之类的, 参考Objects

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/IfStatementTranslator.java

- **Line**: 32

```java
  22:  * @version 1.0
  23:  * @date 2026-04-21 00:35
  24:  */
  25: public class IfStatementTranslator implements CommandTranslator {
  26:     @Override
  27:     public CommandNodeRegister translate(
  28:             ShiftReduceSemanticContext context,
  29:             SimpleGrammarProduction production,
  30:             CommandNodeRegister[] children) {
  31:         if (children.length != 5) {
> 32:             throw new org.harvey.vie.theory.exception.CompilerException(
  33:                     "illegal statement on if statement production."
  34:             );
  35:         }
  36:         // if ( expr ) stmt
  37:         // 一般的 if 语句
  38:         //    expr.command();
  39:         //    DefaultCommandFactory.ifn_goto(L1);
  40:         //    stmt.command();
  41:         //    L1:
  42:         Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
```

> 为什么又用length检��了? 有什么必须要用length检查的理由吗? 

- **Line**: 34

```java
  24:  */
  25: public class IfStatementTranslator implements CommandTranslator {
  26:     @Override
  27:     public CommandNodeRegister translate(
  28:             ShiftReduceSemanticContext context,
  29:             SimpleGrammarProduction production,
  30:             CommandNodeRegister[] children) {
  31:         if (children.length != 5) {
  32:             throw new org.harvey.vie.theory.exception.CompilerException(
  33:                     "illegal statement on if statement production."
> 34:             );
  35:         }
  36:         // if ( expr ) stmt
  37:         // 一般的 if 语句
  38:         //    expr.command();
  39:         //    DefaultCommandFactory.ifn_goto(L1);
  40:         //    stmt.command();
  41:         //    L1:
  42:         Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
  43:         if (constantCondition != null) {
  44:             return constantCondition
```

> 倒不如说, 我之前彻底取缔length真的正确吗? 是不是太激进了? 建议给出必须使用length的理由说服我

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/IfElseStatementTranslator.java

- **Line**: 32

```java
  22:  * @date 2026-04-21 00:35
  23:  */
  24: public class IfElseStatementTranslator implements CommandTranslator {
  25:     @Override
  26:     public CommandNodeRegister translate(
  27:             ShiftReduceSemanticContext context,
  28:             SimpleGrammarProduction production,
  29:             CommandNodeRegister[] children) {
  30:         if (children.length != 7) {
  31:             throw new org.harvey.vie.theory.exception.CompilerException(
> 32:                     "illegal statement on if-else statement production."
  33:             );
  34:         }
  35:         // if ( expr ) stmt else stmt
  36:         // if-else 语句
  37:         //    expr.command();
  38:         //    DefaultCommandFactory.ifn_goto(L1);
  39:         //    stmt.command();
  40:         //    DefaultCommandFactory.ifn_goto(L2);
  41:         //    L1:
  42:         //    (unmatched_stmt|matched_stmt).command();
```

> length检查的问题

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/FunctionReturnTranslator.java

- **Line**: 25

```java
  15: /**
  16:  * @author Temper
  17:  */
  18: public class FunctionReturnTranslator implements CommandTranslator {
  19:     @Override
  20:     public CommandNodeRegister translate(
  21:             ShiftReduceSemanticContext context,
  22:             SimpleGrammarProduction production,
  23:             CommandNodeRegister[] children) {
  24:         if (children.length != 2 && children.length != 3) {
> 25:             throw new org.harvey.vie.theory.exception.CompilerException(
  26:                     "illegal statement on return statement production: " + production + ", children=" + children.length
  27:             );
  28:         }
  29:         boolean hasValue = production.containsTag(ProgramSemanticTag.VALUE);
  30:         CommandNodeBuilder builder = new CommandNodeListBuilder();
  31:         if (hasValue && ConstantAttributes.childIsConstant(context, 1)) {
  32:             ConstantValue value = ConstantAttributes.child(context, 1);
  33:             if (value != null) {
  34:                 builder.add(new TerminalNode(context.getCommandFactory().loadConstant(value)));
  35:             }
```

> length检查的问题

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/DoWhileStatementTranslator.java

- **Line**: 31

```java
  21:  * @date 2026-04-21 00:35
  22:  */
  23: public class DoWhileStatementTranslator implements CommandTranslator {
  24:     @Override
  25:     public CommandNodeRegister translate(
  26:             ShiftReduceSemanticContext context,
  27:             SimpleGrammarProduction production,
  28:             CommandNodeRegister[] children) {
  29:         if (children.length != 7) {
  30:             throw new org.harvey.vie.theory.exception.CompilerException(
> 31:                     "illegal statement on do while statement production."
  32:             );
  33:         }
  34:         Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 4);
  35:         if (Boolean.FALSE.equals(constantCondition)) {
  36:             return onFalseConstantCondition(production, children);
  37:         }
  38:         return onNormalCondition(context, production, children);
  39:     }
  40: 
  41:     private static NormalCommandNodeRegister onNormalCondition(
```

> length的必须存在的价值的问题

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/ArrayAtExpressionTranslator.java

- **Line**: 33

```java
  23: public class ArrayAtExpressionTranslator implements CommandTranslator {
  24: 
  25:     @Override
  26:     public CommandNodeRegister translate(
  27:             ShiftReduceSemanticContext context, SimpleGrammarProduction production, CommandNodeRegister[] children) {
  28:         CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
  29:         children[0].register(thisBuilder);
  30:         children[2].register(thisBuilder);
  31:         SemanticType baseType = TypeAttributes.childType(context, 0);
  32:         SemanticType indexType = TypeAttributes.childType(context, 2);
> 33:         if (baseType == null) {
  34:             SemanticDiagnostics.reject(
  35:                     context,
  36:                     TypeAttributes.childAnchor(context, 0),
  37:                     "array access requires a typed left operand."
  38:             );
  39:         }
  40:         if (indexType == null) {
  41:             SemanticDiagnostics.reject(
  42:                     context,
  43:                     TypeAttributes.childAnchor(context, 2),
```

> if() reject 修改成 require的问题, 算是设计的小优化, 优先级不算高

---

### src/main/java/org/harvey/vie/theory/semantic/command/command/factory/TypedCommandFactory.java

- **Line**: 19

```java
  9: import org.harvey.vie.theory.semantic.type.SemanticType;
  10: import org.harvey.vie.theory.semantic.value.ConstantValue;
  11: 
  12: /**
  13:  * TODO
  14:  *
  15:  * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
  16:  * @version 1.0
  17:  * @date 2026-05-24 23:04
  18:  */
> 19: public interface TypedCommandFactory {
  20:     SemanticCommand loadLiteral(SourceToken token);
  21: 
  22:     SemanticCommand loadIdentifierAddress(SourceToken token);
  23: 
  24:     SemanticCommand loadIdentifierAddress(IdentifierRecord record);
  25: 
  26:     SemanticCommand loadConstant(ConstantValue constantValue);
  27: 
  28:     SemanticCommand newStruct(SourceToken token);
  29: 
```

> 任务太重了, 这个类, 这些真的是 "typed command"吗? 都放到这个TypedCommandFactory合适吗? 及时是typed的, 是不是也应该多分一分? 
> 
> 分的时候还是要复合抽象-实现, 而不是直接静态方法

---

### src/main/java/org/harvey/vie/theory/lexical/analysis/token/IdentifierKey.java

- **Line**: 21

```java
  11:  */
  12: public class IdentifierKey {
  13:     private final byte[] lexeme;
  14: 
  15:     private IdentifierKey(byte[] lexeme) {this.lexeme = lexeme;}
  16:     public static IdentifierKey generate(SourceToken token){
  17:         return generate(token.getLexeme());
  18:     }
  19: 
  20:     public static IdentifierKey generate(byte[] lexeme) {
> 21:         return new IdentifierKey(Arrays.copyOf(lexeme, lexeme.length));
  22:     }
  23: 
  24:     @Override
  25:     public boolean equals(Object o) {
  26:         if (this == o) {
  27:             return true;
  28:         }
  29:         if (!(o instanceof IdentifierKey)) {
  30:             return false;
  31:         }
```

> IdentifierKey的值都是不可Getter的呀, 在其内部的实现也不会对lexeme进行写操作, 也没有对外开放写的接口? 为什么要多次一举做这个数组的拷贝? 我怀疑一切这种莫名其妙的多此一举的修改, 都是由于对错误的方向判断失误导致的. 以为这里是导致bug产生的原因, 于是改了, 改了发现bug没有解决, 于是继续思考, 忘记这里的莫名其妙的修改改回来了

---

### src/main/java/org/harvey/vie/theory/demo/program/ProgramSemanticTag.java

- **Line**: 21

```java
  11: /**
  12:  * TODO 一个Production对应0-n个Tag
  13:  *
  14:  * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
  15:  * @version 1.0
  16:  * @date 2026-05-24 16:19
  17:  */
  18: public enum ProgramSemanticTag implements SemanticTag {
  19:     FUNCTION,
  20:     HEAD,
> 21:     STRUCT_DECL,
  22:     STRUCT_FIELD,
  23:     DECLARATION,
  24:     CALL,
  25:     RETURN,
  26:     VALUE,
  27:     PARAMETER,
  28:     ARGUMENT,
  29:     LIST,
  30:     EMPTY,
  31:     ITEM,
```

> 不一定是一个Tag一个产生式的, 一个产生式多个tag, 但是也不能太多, 必须有利于后续的语义分析. 设计合适的tag

---

### src/main/java/org/harvey/vie/theory/semantic/command/translator/command/AssignStatementTranslator.java

- **Line**: 46

```java
  36:                 "assignment requires assignable types."
  37:         );
  38:         CommandNodeBuilder builder = new CommandNodeListBuilder();
  39:         children[0].register(builder);
  40:         children[2].register(builder);
  41:         if (context.requiresImplicitCast(sourceType, targetType)) {
  42:             builder.add(new TerminalNode(context.getCommandFactory().stTopCast(sourceType, targetType)));
  43:         }
  44:         LocationKind locationKind = TypeAttributes.child(context, 0).getLocationKind();
  45:         if (locationKind == LocationKind.REFERENCE) {
> 46:             builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToRef(targetType)));
  47:         } else {
  48:             builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToAddr(targetType)));
  49:         }
  50:         return new NormalCommandNodeRegister(builder.build(), production, children);
  51:     }
  52: }
  53: 
```

> 依旧是对Address和Reference的理解不清晰

---

### src/main/java/org/harvey/vie/theory/syntax/bu/table/ShiftReduceParsingTableFactoryImpl.java

- **Line**: 1

```java
> 1: package org.harvey.vie.theory.syntax.bu.table;
  2: 
  3: import lombok.AllArgsConstructor;
  4: import org.harvey.vie.theory.syntax.bu.item.ItemSet;
  5: import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
  6: import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
  7: import org.harvey.vie.theory.syntax.bu.la.LookaheadMap;
  8: import org.harvey.vie.theory.syntax.bu.table.element.AcceptTableElementImpl;
  9: import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
  10: import org.harvey.vie.theory.syntax.bu.table.element.ReduceTableElementImpl;
  11: import org.harvey.vie.theory.syntax.bu.table.element.ShiftTableElementImpl;
```

> 应当对于syntax阶段的代码的修改尽可能保守

- **Line**: 124

```java
  114:             int col,
  115:             ActiveTableElement element,
  116:             int state,
  117:             TerminalSymbol terminal,
  118:             ParsingContext pc) {
  119:         if (raw[col] == null) {
  120:             raw[col] = element;
  121:             return;
  122:         }
  123:         if (raw[col].conflict(element)) {
> 124:             String existing = pc.describe(raw[col]);
  125:             String incoming = pc.describe(element);
  126:             throw new IllegalStateException("The grammar do not fix in LALR for conflict between " +
  127:                                             existing +
  128:                                             " and " +
  129:                                             incoming +
  130:                                             " at state " +
  131:                                             state +
  132:                                             " on terminal " +
  133:                                             terminal);
  134:         }
```

> 哦哦, 只是对于日志打印更加仔细了一点

- **Line**: 273

```java
  263:                                                 "It is not supported.");
  264:             }
  265: 
  266:             SemanticTag[] tags = Stream.concat(Arrays.stream(head.getTags()), Arrays.stream(body.getTags()))
  267:                     .distinct()
  268:                     .sorted(tagComparator)
  269:                     .toArray(SemanticTag[]::new);
  270:             return productionDict.computeIfAbsent(
  271:                     new DefineSimpleGrammarProduction(head.toDefine(), body, tags),
  272:                     k -> {
> 273:                         int id = productionDict.size();
  274:                         productionList.add(k);
  275:                         return id;
  276:                     }
  277:             );
  278:         }
  279: 
  280:         public SimpleGrammarProduction getProduction(int id) {
  281:             return productionList.get(id);
  282:         }
  283: 
```

> 为什么要这么做? productionDict 不就是 从production到index的映射吗? productionDict 不就可以转换成一个list吗? 为什么要有一个专门的list数组?

- **Line**: 281

```java
  271:                     new DefineSimpleGrammarProduction(head.toDefine(), body, tags),
  272:                     k -> {
  273:                         int id = productionDict.size();
  274:                         productionList.add(k);
  275:                         return id;
  276:                     }
  277:             );
  278:         }
  279: 
  280:         public SimpleGrammarProduction getProduction(int id) {
> 281:             return productionList.get(id);
  282:         }
  283: 
  284:         public String describe(ActiveTableElement element) {
  285:             if (element instanceof ReduceTableElementImpl) {
  286:                 ReduceTableElementImpl reduce = (ReduceTableElementImpl) element;
  287:                 return "reduce " + reduce.getProduction() + " (" + getProduction(reduce.getProduction()) + ")";
  288:             }
  289:             if (element instanceof AcceptTableElementImpl) {
  290:                 AcceptTableElementImpl accept = (AcceptTableElementImpl) element;
  291:                 return "accept " + accept.getProduction() + " (" + getProduction(accept.getProduction()) + ")";
```

> 由于需要describe->需要从id获取production->需要构建一个productionList而不是复用已经有的productionDict...
> 
> 这么大的付出, 居然仅仅是为了打印异常栈? 

- **Line**: 281

```java
  271:                     new DefineSimpleGrammarProduction(head.toDefine(), body, tags),
  272:                     k -> {
  273:                         int id = productionDict.size();
  274:                         productionList.add(k);
  275:                         return id;
  276:                     }
  277:             );
  278:         }
  279: 
  280:         public SimpleGrammarProduction getProduction(int id) {
> 281:             return productionList.get(id);
  282:         }
  283: 
  284:         public String describe(ActiveTableElement element) {
  285:             if (element instanceof ReduceTableElementImpl) {
  286:                 ReduceTableElementImpl reduce = (ReduceTableElementImpl) element;
  287:                 return "reduce " + reduce.getProduction() + " (" + getProduction(reduce.getProduction()) + ")";
  288:             }
  289:             if (element instanceof AcceptTableElementImpl) {
  290:                 AcceptTableElementImpl accept = (AcceptTableElementImpl) element;
  291:                 return "accept " + accept.getProduction() + " (" + getProduction(accept.getProduction()) + ")";
```

> 我们认为出现错误是少见的, 直接遍历原来的productionDict不行吗? 

---

### src/main/java/org/harvey/vie/theory/demo/program/ProgramStructAwareTokenIterator.java

- **Line**: 18

```java
  8: 
  9: import java.util.ArrayList;
  10: import java.util.HashSet;
  11: import java.util.List;
  12: import java.util.Set;
  13: 
  14: /**
  15:  * Reclassifies declared struct names into a dedicated token when they are used
  16:  * in type positions. This keeps the grammar explicit while avoiding the
  17:  * classic IDENTIFIER/type-name ambiguity.
> 18:  */
  19: public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
  20:     private final SourceTokenIterator delegate;
  21:     private final List<Entry> rawEntries = new ArrayList<>();
  22:     private final List<Entry> normalizedEntries = new ArrayList<>();
  23:     private final Set<String> declaredStructs = new HashSet<>();
  24:     private int normalizedCursor;
  25:     private int index;
  26:     private boolean endReached;
  27: 
  28:     public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
```

> 当发现这个位置是变量声明, 也就是说是类型使用的位置的时候, 把这个identifier的token的类型改成type的token类型, 避免语法分析阶段的冲突

- **Line**: 20

```java
  10: import java.util.HashSet;
  11: import java.util.List;
  12: import java.util.Set;
  13: 
  14: /**
  15:  * Reclassifies declared struct names into a dedicated token when they are used
  16:  * in type positions. This keeps the grammar explicit while avoiding the
  17:  * classic IDENTIFIER/type-name ambiguity.
  18:  */
  19: public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
> 20:     private final SourceTokenIterator delegate;
  21:     private final List<Entry> rawEntries = new ArrayList<>();
  22:     private final List<Entry> normalizedEntries = new ArrayList<>();
  23:     private final Set<String> declaredStructs = new HashSet<>();
  24:     private int normalizedCursor;
  25:     private int index;
  26:     private boolean endReached;
  27: 
  28:     public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
  29:         this.delegate = delegate;
  30:     }
```

> 可以, 但是任务太重了, 一个类的任务太重了, 容易出问题, 特别是这种偏底层的, 更要注重可维护性.

- **Line**: 19

```java
  9: import java.util.ArrayList;
  10: import java.util.HashSet;
  11: import java.util.List;
  12: import java.util.Set;
  13: 
  14: /**
  15:  * Reclassifies declared struct names into a dedicated token when they are used
  16:  * in type positions. This keeps the grammar explicit while avoiding the
  17:  * classic IDENTIFIER/type-name ambiguity.
  18:  */
> 19: public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
  20:     private final SourceTokenIterator delegate;
  21:     private final List<Entry> rawEntries = new ArrayList<>();
  22:     private final List<Entry> normalizedEntries = new ArrayList<>();
  23:     private final Set<String> declaredStructs = new HashSet<>();
  24:     private int normalizedCursor;
  25:     private int index;
  26:     private boolean endReached;
  27: 
  28:     public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
  29:         this.delegate = delegate;
```

> 现在这个类只能做到对于type的identifier和变量的identifier之间的冲突的解决, 应该抽取一层合适的抽象

- **Line**: 19

```java
  9: import java.util.ArrayList;
  10: import java.util.HashSet;
  11: import java.util.List;
  12: import java.util.Set;
  13: 
  14: /**
  15:  * Reclassifies declared struct names into a dedicated token when they are used
  16:  * in type positions. This keeps the grammar explicit while avoiding the
  17:  * classic IDENTIFIER/type-name ambiguity.
  18:  */
> 19: public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
  20:     private final SourceTokenIterator delegate;
  21:     private final List<Entry> rawEntries = new ArrayList<>();
  22:     private final List<Entry> normalizedEntries = new ArrayList<>();
  23:     private final Set<String> declaredStructs = new HashSet<>();
  24:     private int normalizedCursor;
  25:     private int index;
  26:     private boolean endReached;
  27: 
  28:     public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
  29:         this.delegate = delegate;
```

> 抽取抽象的难度很高, 对于这个类的一切优化之后再说, 低优先级

- **Line**: 19

```java
  9: import java.util.ArrayList;
  10: import java.util.HashSet;
  11: import java.util.List;
  12: import java.util.Set;
  13: 
  14: /**
  15:  * Reclassifies declared struct names into a dedicated token when they are used
  16:  * in type positions. This keeps the grammar explicit while avoiding the
  17:  * classic IDENTIFIER/type-name ambiguity.
  18:  */
> 19: public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
  20:     private final SourceTokenIterator delegate;
  21:     private final List<Entry> rawEntries = new ArrayList<>();
  22:     private final List<Entry> normalizedEntries = new ArrayList<>();
  23:     private final Set<String> declaredStructs = new HashSet<>();
  24:     private int normalizedCursor;
  25:     private int index;
  26:     private boolean endReached;
  27: 
  28:     public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
  29:         this.delegate = delegate;
```

> 目前阶段先跑起来

---

### src/main/java/org/harvey/vie/theory/semantic/array/ArrayCreationDimensions.java

- **Line**: 81

```java
  71:             SemanticDiagnostics.reject(context, token, "array creation requires at least one specified length.");
  72:         }
  73:         return summary;
  74:     }
  75: 
  76:     private static void visitDimensions(ShiftReduceSyntaxTreeNode node, java.util.function.Consumer<HeadNode> consumer) {
  77:         if (node == null || !node.isHead()) {
  78:             return;
  79:         }
  80:         ArrayDeque<HeadNode> stack = new ArrayDeque<>();
> 81:         HeadNode cursor = node.toHead();
  82:         while (true) {
  83:             if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.SEQUENCE)) {
  84:                 stack.push(cursor.get(1).toHead());
  85:                 cursor = cursor.get(0).toHead();
  86:                 continue;
  87:             }
  88:             if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.FORWARD)) {
  89:                 stack.push(cursor.get(0).toHead());
  90:                 break;
  91:             }
```

> 又来了, 遍历节点

- **Line**: 81

```java
  71:             SemanticDiagnostics.reject(context, token, "array creation requires at least one specified length.");
  72:         }
  73:         return summary;
  74:     }
  75: 
  76:     private static void visitDimensions(ShiftReduceSyntaxTreeNode node, java.util.function.Consumer<HeadNode> consumer) {
  77:         if (node == null || !node.isHead()) {
  78:             return;
  79:         }
  80:         ArrayDeque<HeadNode> stack = new ArrayDeque<>();
> 81:         HeadNode cursor = node.toHead();
  82:         while (true) {
  83:             if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.SEQUENCE)) {
  84:                 stack.push(cursor.get(1).toHead());
  85:                 cursor = cursor.get(0).toHead();
  86:                 continue;
  87:             }
  88:             if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.FORWARD)) {
  89:                 stack.push(cursor.get(0).toHead());
  90:                 break;
  91:             }
```

> 这种利用了文法的递归的特点��, 从 params -> params, param 到 arguments->arguments, argument , 都可以套用同一个模板. 现在你是 array_inits -> array_inits  '[' expr ']' 也是一个道理, 只要对原来的实现稍作修改, 大不了对原来的实现进行模仿, 在sequence包下再写一个呢? 都不至于直接去遍历树!

- **Line**: 39

```java
  29:             } else {
  30:                 summary.trailingOmittedDimensions++;
  31:             }
  32:         });
  33:         if (summary.specifiedDimensions <= 0) {
  34:             throw new CompilerException("array creation requires at least one specified length.");
  35:         }
  36:         return summary;
  37:     }
  38: 
> 39:     public static Summary summarizeAndValidate(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
  40:         Summary summary = new Summary();
  41:         visitDimensions(node, dimension -> {
  42:             summary.totalDimensions++;
  43:             if (dimension.containsTag(ProgramSemanticTag.VALUE)) {
  44:                 if (summary.trailingOmittedDimensions > 0) {
  45:                     SourceToken token = ShiftReduceSyntaxTreeNode.anchor(dimension);
  46:                     SemanticDiagnostics.reject(
  47:                             context,
  48:                             token,
  49:                             "array creation dimensions with length must precede omitted dimensions."
```

> 这个方法太复杂了, 任务太重了, 而且钻石

*Report generated by gh-review2md*
