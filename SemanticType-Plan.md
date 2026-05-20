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