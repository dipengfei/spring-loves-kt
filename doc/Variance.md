# 型变(Variance)

本文将结合示例代码简要介绍范型(Generics)及其可变性(Variance)的背景、定义，以及在Java与Kotlin中的不同表现，进而探讨类型擦除(Type Erasure)与Spring对范型依赖的处理(Spring
Generics Dependency)等高级主题。

## 关键字(Key Words)

本文将涉及以下关键字：

- 面向对象编程的三大原则([OOP 3 Principles](https://www.d.umn.edu/~gshute/softeng/presentations/oo-principles.xhtml))
    - 封装性(Encapsulation)
    - 继承性(Inheritance)
    - 多态性(Polymorphism)
- 软件系统设计的三大原则(Software Design 3 Principles)
    - 开-闭原则([OCP](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle))
    - 里氏代换原则([LSP](https://en.wikipedia.org/wiki/Liskov_substitution_principle))
    - 依赖倒转原则([DIP](https://en.wikipedia.org/wiki/Dependency_inversion_principle))
- 时机(Timing)
    - 编译时(compile-time)
    - 运行时(runtime)
- 型变(Variance)
    - 协变(Covariance)
    - 逆变(Contravariance)
    - 不变(invariance)
- 型变位置(Variance Site)
    - 声明处(Declaration-site variance)
    - 使用处(Use-site variance variance)
- 投影(Projections)
    - 类型投影(Type Projections)
    - 星投影(Star Projections)
- 类型擦(Type Erasure)

## 型变定义

## Java中的型变

## Kotlin中的型变

## 类型擦除(Type Erasure)

## Spring对范型依赖的处理(Spring Generics Dependency)
