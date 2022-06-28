package me.danielpf.springloveskt.variance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class VarianceTests {

    @Test
    public void test_poor_box() {
        var integerPoorBox = new PoorBox<Integer>();
        integerPoorBox.set(20);

        // it's not compiled, as Generics in Java is invariant, PoorBox<Integer> is not subtype of PoorBox<Number>
        // PoorBox<Number> numberPoorBox = integerPoorBox;

        var numberPoorBox = new PoorBox<Number>();
        numberPoorBox.copyFrom(integerPoorBox);
        Assertions.assertEquals(integerPoorBox.get(), numberPoorBox.get());
        // you can't copy value from Integer Box by calling poorCopyFrom even though you're a Number Box.
        // numberPoorBox.poorCopyFrom(integerPoorBox) is not working

        var doublePoorBox = new PoorBox<Double>();
        doublePoorBox.set(1.0d);
        doublePoorBox.copyTo(numberPoorBox);
        Assertions.assertEquals(doublePoorBox.get(), numberPoorBox.get());
        // you can't copy value to Number Box by calling poorCopyTo even though you're a Double Box.
        // doublePoorBox.poorCopyTo(numberPoorBox) is not working
    }

    @Test
    public void test_array() {
        // it can be compiled, as Array in Java is covariant
        Object[] objs = new Integer[10];

        Assertions.assertThrowsExactly(ArrayStoreException.class, () -> {
            // it can be compiled, but error occurs in runtime.
            objs[0] = "0";
        });

        // same case
        Assertions.assertThrowsExactly(ArrayStoreException.class, () -> Arrays.fill(objs, "1"));

    }

}
