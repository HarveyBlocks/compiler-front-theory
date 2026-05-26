#  Review

按照优先级排列

简单且紧急的任务

- [x] 将日志有关文件写入gitignore, 然后不要把日志上传到github  (简单)
- [x] Boolean.getBoolean
  - 应当被统一管理, 而不是分散在各个地方
  - DEBUG_FUNCTION 是在排查Function的错误的时候使用的, 相关打印和配置都应该删除
  - 剩下的配置, 如果有价值, 就应该写入文档, 说明清楚用法
- [x] 多余的日志打印 (简单)
  - 函数出什么问题不是查清楚了吗? 那就把代码整理一下, 没必要的东西清理干净
- [x] Identifier#generate 的对数组的拷贝的问题
  - IdentifierKey的值都是不可Getter的, 在其内部的实现也不会对lexeme进行写操作, 也没有对外开放写的接口
  - 为什么要多次一举做这个数组的拷贝? 
  - 我怀疑一切这种莫名其妙的多此一举的修改, 都是由于对错误的方向判断失误导致的. 以为这里是导致bug产生的原因, 于是改了, 改了发现bug没有解决, 于是继续思考, 忘记这里的莫名其妙的修改改回来了
- [ ] 对于没有标注@author的类, 加上@author


复杂且紧急的任务

- [x] ProgramSemanticTag  设计不够优秀
  - 不一定是一个Tag一个产生式的, 而是一个产生式0-n个tag
  - 但是也不能太多, 必须有利于后续的语义分析. 设计合适的tag
- [x] 还在解析产生式字符串, 而不是使用Tag (任务重, 容易出错)
  - TypeBuildCallback: 8
  - `FunctionSemanticCallback#onReduce0`
  - 我猜测是因为函数模块出现问题了, 死活改不对, 然后猜测是Tag机制出现问题了, 于是改回了返璞归真的字符串解析. 但是后面明明错误不是这个, 但是没改回来, 还是字符串
  - `TagStrategyCompose` 里全是字符串解析, 死性不改, 使用查看commit历史, 查看`TagStrategyCompose` 原来是什么样子的, 修改回原来的样子

- [ ] LocationKind 是否有存在的必要 (比较紧急)
  - 因为 Address 和 Reference 是完全不同 Level 的问题, 因此放到一起, 好像是对称的一样, 那就是完全错误的
  - Address 是指在局部变量表中, 是否需要被赋值, 如果需要被赋值, 那么放入栈顶的应该是变量在局部变量表中的偏移地址
  - Reference 表示数值应当被解释称引用对象
  - 引用对象在局部变量表中, 也可以被赋值
  - 当然, `obj.value = 2` 中, obj 应该是右值, 而不是左值, `value` 才是左值
- [ ] 对于 100 维的数组的初始化, 中间代码是如何设计的?
  - 对于`int32[][][] a = new int32[11][12][13]`
  - 对于`int32[][][] a = new int32[11][12][]`
  - 对于`int32[][][] a = new int32[11][][]`
  - 都各自是如何处理的? 
- [ ] `ArrayCreationDimensions`的错误遍历`visitDimensions`
  - 从 params -> params, param 到 arguments->arguments, argument , 都可以套用同一个模板
  - 现在是 array_inits -> array_inits '[' expr ']' 也是一个道理
  - 只要对原来的实现稍作修改, 大不了对原来的实现进行模仿, 在sequence包下再写一个呢? 都不至于直接去遍历树!
  - 其实最好的做法是, 让sequence下面的实现不要涉及产生式, 不要被产生式的具体结构影响, 由外界来和产生式耦合
  - 建议创建一个新的类, 完成一套新的, 和产生式无关的实现, 然后跑通array部分的测试之后, 再进行后续的替换
  - 而且由于错误封装(遍历树), 方法变得十分臃肿, 丑陋和钻石





复杂不紧急

- [ ] `ShiftReduceSemanticContext#pendingStructBraceDepth` 通过depth判断深度, 似乎是一个比较简单的做法, 我不知道是否合理, 是不是由于大量函数是反复修改函数参数作用域的时候的错误猜测导致的, 没清理干净导致的历史遗留问题

- [ ] 任务太重的类拆分一下, 拆分不是提取几个static方法
  - `TypeBuildCallback`
  - `TypedCommandFactory`

- [ ] `ProgramStructAwareTokenIterator` 的任务太重
  - 当发现这个位置是变量声明, 也就是说是类型使用的位置的时候, 把这个identifier的token的类型改成type的token类型, 避免语法分析阶段的冲突 
  - 任务太重了, 一个类的任务太重了, 容易出问题, 特别是这种偏底层的(词法分析), 更要注重可维护性.
  - 现在这个类只能做到对于type的identifier和变量的identifier之间的冲突的解决, 应该抽取一层合适的抽象
  - 抽取抽象的难度很高, 对于这个类的一切优化之后再说, 低优先级 

- [ ] `SemanticType` 的字段
  - `this.namedTypeKey = namedTypeKey;` 字段, 因为只有结构体需要, 真的有必要专门加一个字段在SemanticType里吗 ?  难道不是应该把基础数据类型和做一个区分的吗?
  - `private final List<Integer> dimensions;`
    何意味? 还用`List<Integer>`存储维度? 为什么?
  - 但是分开来会不会造成过度设计呢? 但是考虑到将来的扩展性的话, 那肯定是要分开的.

- [ ] 为什么还在用 length 来判断是否符合目标产生式?
  - 希望一个理由, 而不是直接改回去, 我要一个坚持使用length判断的理由
  - 倒不如说, 我之前彻底取缔length真的正确吗? 是不是太激进了? 建议给出必须使用length的理由说服我
  - `WhileStatementTranslator`
  - `StatementListTranslator`
  - `IfStatementTranslator`
  - `IfStatementTranslator`
  - `IfElseStatementTranslator`
  - `FunctionReturnTranslator`
  - `DoWhileStatementTranslator`
  - 看来Translator这里需要优化, 不应该是length来判断,但是后面从children里获取index的成员, 也确实不是好的设计
  - **因此好的设计应该是, 定义一个接口, 外界提供这个接口的实现, 内部调用接口的方法.**


简单不紧急

- [ ] `StructRecord` 是通过遍历fields 来找到字段的, 有点暴力了, 但是不急着改
- [ ] reject 改成require

  这边建议

  ```java
  if (struct == null) {
      SemanticDiagnostics.reject(
              context,
              TypeAttributes.childAnchor(context, 0),
               "member access requires a declared struct operand."
       );
  }
  SemanticDiagnostics.requireNonNull(
      struct,    
      context,
      TypeAttributes.childAnchor(context, 0),
      "member access requires a declared struct operand."
  );
  
  ```

  其他的还可以有requireTrue之类的, 参考Objects

- [ ] `ShiftReduceParsingTableFactoryImpl#productDict` 到describe

  - 由于需要describe->需要从id获取production->需要构建一个productionList而不是复用已经有的productionDict...
  - 是否是没有必要
  - 我们认为出现错误是少见的, 直接遍历原来的productionDict不行吗?



手动改一改

- [x] DefineSimpleGrammarProduction中对tags的错误理解
  - 其内部的tags应该是升序的, 因此可以Arrays.binarySeach, 而不是遍历
  - 但是我担心其对这部分的修改另有意图, 我不放心, 建议还是问一问



