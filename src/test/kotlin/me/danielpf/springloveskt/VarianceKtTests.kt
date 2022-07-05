package me.danielpf.springloveskt

import me.danielpf.springloveskt.variance.*
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.reflect.jvm.javaField

@SpringBootTest
class VarianceKtTests {

    private val logger = KotlinLogging.logger {}

    @Autowired
    lateinit var boxBeansHolder: BoxBeansHolder

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
        logger.info { "starOutBox: $starOutBox" }
        // Foo<*> is equivalent to Foo<out TUpper>.
        // This means that when the T is unknown you can safely read values of TUpper from Foo<*>
        // val str: String = starOutBox.value is not allowed
        val outBoxValue = starOutBox.value
        Assertions.assertEquals(outBoxValue, "out")

        val starInBox: InBox<*> = InBox("in")
        // Foo<*> is equivalent to Foo<in Nothing>.
        // This means there is nothing you can write to Foo<*> in a safe way when T is unknown.
        // starInOutBox.setValue("xxx") is not allowed
        logger.info { "starInBox: $starInBox" }

        val starBox: Box<*> = Box("box")
        // Foo<*> is equivalent to Foo<out TUpper> for reading values and to Foo<in Nothing> for writing values.
        Assertions.assertEquals(starBox.value, "box")
        // starBox.value = "ok" is not allowed
        logger.info { "starBox: $starBox" }

    }

    @Test
    fun test_star_box_list() {

        // * will be resolved as ?, like raw type

        logger.info { "starOutBoxList target: ${resolveTypeName(BoxBeansHolder::starOutBoxList.javaField!!)}" }
        logger.info { "starOutBoxList candidates: ${boxBeansHolder.starOutBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.starOutBoxList.size, 3)

        logger.info { "starOutBoxList target: ${resolveTypeName(BoxBeansHolder::starInBoxList.javaField!!)}" }
        logger.info { "starOutBoxList candidates: ${boxBeansHolder.starInBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.starInBoxList.size, 3)

        logger.info { "starOutBoxList target: ${resolveTypeName(BoxBeansHolder::starBoxList.javaField!!)}" }
        logger.info { "starOutBoxList candidates: ${boxBeansHolder.starBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.starBoxList.size, 3)
    }

    @Test
    fun test_any_box_list() {

        logger.info { "anyOutBoxList target: ${resolveTypeName(BoxBeansHolder::anyOutBoxList.javaField!!)}" }
        logger.info { "anyOutBoxList candidates: ${boxBeansHolder.anyOutBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.anyOutBoxList.size, 3)

        logger.info { "anyInBoxList target: ${resolveTypeName(BoxBeansHolder::anyInBoxList.javaField!!)}" }
        logger.info { "anyInBoxList candidates: ${boxBeansHolder.anyInBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.anyInBoxList.size, 0)

        logger.info { "anyBoxList target: ${resolveTypeName(BoxBeansHolder::anyBoxList.javaField!!)}" }
        logger.info { "anyBoxList candidates: ${boxBeansHolder.anyBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.anyBoxList.size, 0)
    }

    @Test
    fun test_nothing_box_list() {

        // nothing will be resolved as ?, like raw type

        logger.info { "nothingOutBoxList target: ${resolveTypeName(BoxBeansHolder::nothingOutBoxList.javaField!!)}" }
        logger.info { "nothingOutBoxList candidates: ${boxBeansHolder.nothingOutBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.nothingOutBoxList.size, 3)

        logger.info { "nothingInBoxList target: ${resolveTypeName(BoxBeansHolder::nothingInBoxList.javaField!!)}" }
        logger.info { "nothingInBoxList candidates: ${boxBeansHolder.nothingInBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.nothingInBoxList.size, 3)

        logger.info { "nothingBoxList target: ${resolveTypeName(BoxBeansHolder::nothingBoxList.javaField!!)}" }
        logger.info { "nothingBoxList candidates: ${boxBeansHolder.nothingBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.nothingBoxList.size, 3)
    }

    @Test
    fun test_number_box_list() {

        logger.info { "numberBoxOutList target: ${resolveTypeName(BoxBeansHolder::numberOutBoxList.javaField!!)}" }
        logger.info { "numberBoxOutList candidates: ${boxBeansHolder.numberOutBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.numberOutBoxList.size, 2)

        logger.info { "numberInBoxList target: ${resolveTypeName(BoxBeansHolder::numberInBoxList.javaField!!)}" }
        logger.info { "numberInBoxList candidates: ${boxBeansHolder.numberInBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.numberInBoxList.size, 1)

        logger.info { "numberBoxList target: ${resolveTypeName(BoxBeansHolder::numberBoxList.javaField!!)}" }
        logger.info { "numberBoxList candidates: ${boxBeansHolder.numberBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.numberBoxList.size, 1)
    }

    @Test
    fun test_int_box_list() {

        logger.info { "intOutBoxList target: ${resolveTypeName(BoxBeansHolder::intOutBoxList.javaField!!)}" }
        logger.info { "intOutBoxList candidates: ${boxBeansHolder.intOutBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.intOutBoxList.size, 1)

        logger.info { "intInBoxList target: ${resolveTypeName(BoxBeansHolder::intInBoxList.javaField!!)}" }
        logger.info { "intInBoxList candidates: ${boxBeansHolder.intInBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.intInBoxList.size, 2)

        logger.info { "intBoxList target: ${resolveTypeName(BoxBeansHolder::intBoxList.javaField!!)}" }
        logger.info { "intBoxList candidates: ${boxBeansHolder.intBoxList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.intBoxList.size, 1)
    }

    @Test
    fun test_box_out_projection_list() {

        logger.info { "intBoxOutProjectionList target: ${resolveTypeName(BoxBeansHolder::intBoxOutProjectionList.javaField!!)}" }
        logger.info { "intBoxOutProjectionList candidates: ${boxBeansHolder.intBoxOutProjectionList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.intBoxOutProjectionList.size, 1)

        logger.info { "numberBoxOutProjectionList target: ${resolveTypeName(BoxBeansHolder::numberBoxOutProjectionList.javaField!!)}" }
        logger.info { "numberBoxOutProjectionList candidates: ${boxBeansHolder.numberBoxOutProjectionList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.numberBoxOutProjectionList.size, 2)

    }

    @Test
    fun test_box_in_projection_list() {

        logger.info { "intBoxInProjectionList target: ${resolveTypeName(BoxBeansHolder::intBoxInProjectionList.javaField!!)}" }
        logger.info { "intBoxInProjectionList candidates: ${boxBeansHolder.intBoxInProjectionList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.intBoxInProjectionList.size, 2)

        logger.info { "numberBoxInProjectionList target: ${resolveTypeName(BoxBeansHolder::numberBoxInProjectionList.javaField!!)}" }
        logger.info { "numberBoxInProjectionList candidates: ${boxBeansHolder.numberBoxInProjectionList.joinToString { it.toString() }}" }
        Assertions.assertEquals(boxBeansHolder.numberBoxInProjectionList.size, 1)

    }

}