package me.danielpf.springloveskt.variance

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import kotlin.reflect.jvm.javaField


abstract class TypeNameAware {
    var typeName: String? = null
}

class OutBox<out T>(val value: T) : TypeNameAware() {
    override fun toString() = this.typeName?.let { "$typeName($value)" } ?: super.toString()
}

class InBox<in T>(private var value: T) : TypeNameAware() {

    fun setValue(value: T) {
        this.value = value
    }

    override fun toString() = this.typeName?.let { "$typeName($value)" } ?: super.toString()
}

class Box<T>(var value: T) : TypeNameAware() {

    fun copyFrom(source: Box<out T>) {
        this.value = source.value
    }

    fun copyTo(dest: Box<in T>) {
        dest.value = this.value
    }

    override fun toString() = this.typeName?.let { "$typeName($value)" } ?: super.toString()
}


@Configuration
class BoxConfig {

    private class BoxHolder {
        val intOutBox = OutBox(1)
        val numberOutBox = OutBox<Number>(1.5)
        val stringOutBox = OutBox("out_box")

        val intInBox = InBox(2)
        val numberInBox = InBox<Number>(2.6)
        val stringInBox = InBox("in_box")

        val intBox = Box(3)
        val numberBox = Box<Number>(3.7)
        val stringBox = Box("box")
    }

    private val boxHolder = BoxHolder()

    @Bean
    fun intOutBox() =
        boxHolder.intOutBox.apply { typeName = resolveTypeName(BoxHolder::intOutBox.javaField!!) }

    @Bean
    fun numberOutBox() =
        boxHolder.numberOutBox.apply { typeName = resolveTypeName(BoxHolder::numberOutBox.javaField!!) }

    @Bean
    fun stringOutBox() =
        boxHolder.stringOutBox.apply { typeName = resolveTypeName(BoxHolder::stringOutBox.javaField!!) }


    @Bean
    fun intInBox() = boxHolder.intInBox.apply { typeName = resolveTypeName(BoxHolder::intInBox.javaField!!) }

    @Bean
    fun numberInBox() = boxHolder.numberInBox.apply { typeName = resolveTypeName(BoxHolder::numberInBox.javaField!!) }

    @Bean
    fun stringInBox() = boxHolder.stringInBox.apply { typeName = resolveTypeName(BoxHolder::stringInBox.javaField!!) }

    @Bean
    fun intBox() = boxHolder.intBox.apply { typeName = resolveTypeName(BoxHolder::intBox.javaField!!) }

    @Bean
    fun numberBox() = boxHolder.numberBox.apply { typeName = resolveTypeName(BoxHolder::numberBox.javaField!!) }

    @Bean
    fun stringBox() = boxHolder.stringBox.apply { typeName = resolveTypeName(BoxHolder::stringBox.javaField!!) }

}

@Component
class BoxBeansHolder(

    val starOutBoxList: List<OutBox<*>>,
    val starInBoxList: List<InBox<*>>,
    val starBoxList: List<Box<*>>,

    val anyOutBoxList: List<OutBox<Any?>>,
    val anyInBoxList: List<InBox<Any?>>,
    val anyBoxList: List<Box<Any?>>,

    val nothingOutBoxList: List<OutBox<Nothing>>,
    val nothingInBoxList: List<InBox<Nothing>>,
    val nothingBoxList: List<Box<Nothing>>,

    val numberOutBoxList: List<OutBox<Number>>,
    val numberInBoxList: List<InBox<Number>>,
    val numberBoxList: List<Box<Number>>,

    val intOutBoxList: List<OutBox<Int>>,
    val intInBoxList: List<InBox<Int>>,
    val intBoxList: List<Box<Int>>,

    val intBoxOutProjectionList: List<Box<out Int>>,
    val numberBoxOutProjectionList: List<Box<out Number>>,

    val intBoxInProjectionList: List<Box<in Int>>,
    val numberBoxInProjectionList: List<Box<in Number>>
)

fun resolveTypeName(field: Field): String {

    val root = ResolvableType.forField(field)
    val layer1 = if (root.hasGenerics()) {
        root.generics[0]
    } else {
        null
    }
    val layer2 = layer1?.let {
        if (it.hasGenerics()) {
            layer1.generics[0]
        } else {
            null
        }
    }

    var result: String? = layer2?.resolve()?.simpleName.let { mapName(it) }
    result = layer1?.resolve()?.simpleName.let { mapName(it) }.let {
        if (layer2 == null) {
            it
        } else {
            "$it<$result>"
        }
    }

    result = root.resolve()?.simpleName.let {
        if (layer1 == null) {
            "$it"
        } else {
            "$it<$result>"
        }
    }
    return result

}

private fun mapName(name: String?) = when (name) {
    null -> "?"
    "Object" -> "Any?"
    "Integer" -> "Int"
    else -> name
}

