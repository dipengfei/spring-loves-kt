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
- PECS
    - Producer -> Extends
    - Consumer -> Super
- 型变(Variance)
    - 协变(Covariance)
    - 逆变(Contravariance)
    - 不型变(invariance)
- 型变位置(Variance Site)
    - 声明处型变(Declaration-site variance)
        - Consumer -> in,
        - Producer -> out
    - 使用处型变(Use-site variance)
        - 投影(Projections)
            - 类型投影(Type Projections)
            - 星投影(Star Projections)

- 类型擦除(Type Erasure)

## 型变定义

这里我们给出一个基于LSP的型变定义。
> 给定基类*Base*，记作*B*，*B*的派生类*Derived*，记作*D*，二者满足LSP，记作*B > D*。给定映射关系*R*，分别作用于*B*、*D*，得到*R(B)*、*R(D)*，
> - 如果得到R(B) > R(D)，那么关系R为协变(Covariant)的;
> - 如果得到R(B) < R(D)，那么关系R为逆变(Contravariant)的;
> - 如果得到R(B) <> R(D)，那么关系R为不型变(Invariant)的。

在本文中讨论的范围内，关系R主要表现为

- 数组(Array)
- 范型(Generics)

## Java中的型变

在Java中，数组(Array)被设计成协变(Covariance)的，而范型(Generics)被设计成不变(Invariant)的，下面我们会举例说明这样设计的优缺点。
首先看数组(Array)，

```
// it can be compiled, as Array in Java is covariant, upper cast is ok
Object[] objs = new Integer[10];

// ArrayStoreException occurrs!
objs[0] = "abc";
```

从上面的代码可以看出，数组(Array)被设计成协变(Covariant)的，有以下优点(Pros)：

- 是符合直觉的(intuitive)。
- 变量、参数可以灵活(flexibility)地接收字类型实体。

但也存在缺点(Cons)：

- 在某些操作下，类型安全(type safe)会被破坏。

接下来再看范型(Generics)，

```
public class PoorBox<T> {}

// can't compile, as Generics is invariant.
PoorBox<Number> numberPoorBox = new PoorBox<Integer>();
```

从上面的代码可以看出，范型(Generics)被设计成不型变(Invariant)的，与数组(Array)相比，优缺点刚好相反，缺点(Cons)：

- 反直觉(counterintuitive)。
- 变量、参数丧失灵活性。

优点(Pros)么：

- 类型安全(type safe)得以保证。

综合数组(Array)与范型(Generics)，类型被设计为协变(Covariant)的,可以保证足够的足够的灵活性(flexibility)，但需要在类型安全(type safe)做出额外考量。而类型安全(type safe)
又与特定的操作有关。

为保证类型安全(type safe)，Java引入边界通配符(Bounded Wild Cards)来增强范型(Generics)系统：

- *B<? extends T>*使当前类型*B*在参数*T*上是协变(Covariance)的，编译器保证只能读取(read)类型*B*中的数据，这时类型*B*只能为生产者(producer);
- *B<? super T>*使当前类型*B*在参数*T*上是逆变(Contravariant)的，编译器保证只能读取(write)类型*B*中的数据，这时类型*B*只能为消费者(consumer)。

以上信息概括起来，就是*PECS*原则，以下内容引用自Effective Java, 3rd Edition, Item 31: *Use bounded wildcards to increase API flexibility*.

> For maximum flexibility, use wildcard types on input parameters that represent producers or consumers. If an input
> parameter is both a producer and a consumer, then wildcard types will do you no good: you need an exact type match,
> which is what you get without any wildcards. Here is a mnemonic to help you remember which wildcard type to use:
> ***PECS stands for producer-extends, consumer-super.***

下面示例中的*copyFrom*与*copyTo*方法依照*PECS*原则创建

```
public class PoorBox<T> {
    private T value;
    public T getValue() {return value;}
    public void setValue(T t) {this.value = t;}
    
    public void copyFrom(PoorBox<? extends T> source) {
        this.value = source.getValue();
    }

    public void copyTo(PoorBox<? super T> dest) {
        dest.setValue(this.value);
    }
}
```

## Kotlin中的型变

Kotlin按照自己的设计思路，结合Java语言中范型(Generics)系统的优缺点，给出了自己的实现方式。

1. Kotlin把数组也设计成了范型类，统一了设计思路，避免使用两套不同的规则。
2. Kotlin引入了[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html)
   类，为所有类型设定了一个下界。注意，这里说Nothing是所有类型的下界，而不是子类，并且Nothing不能被实例化。
3. Kotlin引入了*声明处型变(Declaration-site variance)*，使得符合*PECS*原则的类型，在声明处即可获得型变。
4. Kotlin引入了*使用处型变(Use-site variance)*和*类型投影(Type Projections)*，使得不符合*PECS*原则的类型，在使用处(作为函数参数)即可获得型变。
5. Kotlin引入了*泛型约束(Generic constraints)*，相比于Java的类型上界(upper bound)只能指定单一上界，Kotlin中可以定义多个上界(upper bound)，这在约束类型实现多个接口时十分有用。

## 类型擦除(Type Erasure)

## Spring对范型依赖的处理(Spring Generics Dependency)
