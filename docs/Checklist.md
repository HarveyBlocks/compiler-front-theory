- [ ] SourceToken 的 identifier 如何识别 ?
    - FunctionSemanticCallback.java (line 52)
    - FunctionSemanticCallback.java (line 102)
    - TypeBuildCallback.java (line 142)
    - ConstantValueBuildCallback.java (line 77)
    - TypeResolver.java (line 50)
    - TypedCommandFactory.java (line 14)
    - TypedCommandFactory.java (line 18)
    - FunctionCallTranslator.java (line 42)
- [ ] 常量值计算 ? 对于常量值计算的有关的几个处理类, 全部都写成抽象-实现的形式
    - 抽象类, 表示对外的接口
    - 实现类, 目前的实现, 简单实现, 就是使用JDK的已经有的工具进行字面量到常量的转换
    - 也就是说, 中间引入一个抽象类/接口, 对外开放, 现在的实现, 统统放到实现类里, 叫做SimpleXXXXXX,
- [ ] reject 不要使用reject, 使用require, requireNonNull之类的, requireTrue, 然后在Request里面进行异常抛出, 而不是
  ```java
  if(xxx){
    XXX.reject("...");
  }
  XXX.requireTrue(xxx, "...";)
  ```
  改了

