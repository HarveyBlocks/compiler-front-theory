# compiler

## IO

- [x] 错误恢复

## lexical

- [x] REs->REs AST
- [x] REs->NFA
- [x] NFA->DFA
- [x] DFA 最小化
- [x] 状态表持久化
- [x] 状态接受支持优先级
- [x] lexical analyser
- [x] 行号追踪
- [ ] 字符类、预定义字符集
  - REs 上的功能, 比如range `[a-z]`, 或者 \w, \s
  - 没有做, 但是增加很容易
- [x] 错误恢复

## syntax

### Predicate

- [x] 文法对象
- [x] 计算 FIRST 集
- [x] 计算 FOLLOW 集
- [x] 消除左递归（包括间接左递归）
- [x] 提取左公因子
- [x] 构造预测分析表
- [x] 表驱动引擎
- [x] 错误恢复
- [x] 语义动作与 AST 构建

### DeRemer & Pennello LALR 

- [ ] 增广文法
- [x] 项集族
- [x] GOTO
- [x] DR
- [x] READ
- [x] Lookahead
- [x] 分析表
- [ ] 表驱动引擎 


1. 词法分析
2. 语法分析
   1. 自上而下预测
   2. 自下而上: 移进-规约
3. 语义分析
   - 中间代码生成 三地址码
   - 类型推断/检查
   - 常量折叠: 表达式的常量折叠+控制结构的常量折叠
   - 函数: 注册参数列表+声明, 依据控制结构判断return是否正确
   - Struct 结构体 + 数组

优化数组
int[] an = new int[12]; // 长度可以是表达式不一定是常量
结构体
关键字 null, 数组/struct都可以是null
stuct StructObject{
    int value = 0;
    int value2; // 1nt->0 , float->0, boolean->false, 数组->null, struct结构体->null
    StructObject obj; // obj -> null
}
StructObject obj = new();
