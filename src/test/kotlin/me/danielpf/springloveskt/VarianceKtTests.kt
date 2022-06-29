package me.danielpf.springloveskt

import me.danielpf.springloveskt.variance.Box
import me.danielpf.springloveskt.variance.InBox
import me.danielpf.springloveskt.variance.OutBox
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class VarianceKtTests {

    private val logger = KotlinLogging.logger {}

    @Test
    fun test_variant_box() {

        val intOutBox = OutBox(10)
        // no problem, OutBox is covariant, Number > Int
        val numberOutBox: OutBox<Number> = intOutBox
        logger.info { "IntOutBox: ${intOutBox.value}, NumberOutBox: ${numberOutBox.value}" }
        Assertions.assertEquals(intOutBox.value, numberOutBox.value)

        val charsBox = InBox<CharSequence>("Foo")
        // here you can't access value directly, as value is private to disable invariant
        charsBox.setValue("Bar")
        // no problem, InBox is contravariant, String < CharSequence
        val strBox: InBox<String> = charsBox
        logger.info { "charsBox: $charsBox, strBox: $strBox" }
        Assertions.assertEquals(charsBox.toString(), strBox.toString())

    }

    @Test
    fun test_in_out_projection_box() {

        val numberBox = Box<Number>(15)
        val intBox = Box(10)
        // out projection, Box<Number> > Box<Int>
        numberBox.copyFrom(intBox)
        Assertions.assertEquals(numberBox.value, intBox.value)

        // in projection, Box<Int> > Box<Number>
        intBox.copyTo(numberBox)
        Assertions.assertEquals(numberBox.value, intBox.value)

    }

    @Test
    fun test_star_projection_box() {

        val starOutBox: OutBox<*> = OutBox("out")
        // Foo<*> is equivalent to Foo<out TUpper>.
        // This means that when the T is unknown you can safely read values of TUpper from Foo<*>
        // val str: String = starOutBox.value is not allowed
        Assertions.assertEquals(starOutBox.value, "out")

        val starInOutBox: InBox<*> = InBox("in")
        // Foo<*> is equivalent to Foo<in Nothing>.
        // This means there is nothing you can write to Foo<*> in a safe way when T is unknown.
        // starInOutBox.setValue("xxx") is not allowed

        val starBox: Box<*> = Box("box")
        // Foo<*> is equivalent to Foo<out TUpper> for reading values and to Foo<in Nothing> for writing values.
        Assertions.assertEquals(starBox.value, "box")
        // starBox.value = "ok" is not allowed

    }
}