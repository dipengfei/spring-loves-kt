package me.danielpf.springloveskt.variance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Arrays;

@SpringBootTest
@Slf4j
public class VarianceTests {

    @Autowired
    private Jackson2ObjectMapperBuilder objectMapperBuilder;

    @Test
    public void test_poor_box() {
        var integerPoorBox = new PoorBox<Integer>();
        integerPoorBox.setValue(20);

        // it's not compiled, as Generics in Java is invariant, PoorBox<Integer> is not subtype of PoorBox<Number>
        // PoorBox<Number> numberPoorBox = integerPoorBox;

        var numberPoorBox = new PoorBox<Number>();
        numberPoorBox.copyFrom(integerPoorBox);
        Assertions.assertEquals(integerPoorBox.getValue(), numberPoorBox.getValue());
        // you can't copy value from Integer Box by calling poorCopyFrom even though you're a Number Box.
        // numberPoorBox.poorCopyFrom(integerPoorBox) is not working

        var doublePoorBox = new PoorBox<Double>();
        doublePoorBox.setValue(1.0d);
        doublePoorBox.copyTo(numberPoorBox);
        Assertions.assertEquals(doublePoorBox.getValue(), numberPoorBox.getValue());
        // you can't copy value to Number Box by calling poorCopyTo even though you're a Double Box.
        // doublePoorBox.poorCopyTo(numberPoorBox) is not working
    }

    @Test
    public void test_array() {
        // it can be compiled, as Array in Java is covariant
        Object[] objs = new Integer[10];

        Assertions.assertThrowsExactly(ArrayStoreException.class, () -> {
            // it can be compiled, but error occurs in runtime.
            objs[0] = "abc";
            log.info("objs[0]: {}", objs[0]);
        });

        // same case
        Assertions.assertThrowsExactly(ArrayStoreException.class, () -> Arrays.fill(objs, "1"));

    }

    @Test
    public void test_type_erased_casting() {
        PoorBox<Number> poorBox = new PoorBox<>();
        poorBox.setValue(1.2f);

        // upper cast, no problem
        Object obj = poorBox;

        @SuppressWarnings("unchecked")
        PoorBox<Integer> casted = PoorBox.class.cast(obj);
        Assertions.assertThrowsExactly(ClassCastException.class, () -> {
            Integer i = casted.getValue();
            log.info("get content from box: {}", i);
        });
    }

    @Test
    public void test_type_erased_json_deserialization() {

        @Language("JSON")
        var json = "{\"value\" : 1.5}";
        var objectMapper = this.objectMapperBuilder.build();

        try {
            @SuppressWarnings("unchecked")
            var doublePoorBox = (PoorBox<Double>) objectMapper.readValue(json, PoorBox.class);
            Assertions.assertEquals(doublePoorBox.getValue(), Double.valueOf("1.5"));

            @SuppressWarnings("unchecked")
            var integerPoorBox = (PoorBox<Integer>) objectMapper.readValue(json, PoorBox.class);
            Assertions.assertThrowsExactly(ClassCastException.class, () -> {
                Integer value = integerPoorBox.getValue();
                log.info("value from box: {}", value);
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_type_safe_json_deserialization() {
        @Language("JSON")
        var json = "{\"value\" : 1.5}";
        var objectMapper = this.objectMapperBuilder.build();

        try {
            var doublePoorBox = objectMapper.readValue(json, new TypeReference<PoorBox<Double>>() {});
            Assertions.assertEquals(doublePoorBox.getValue(), Double.valueOf("1.5"));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
