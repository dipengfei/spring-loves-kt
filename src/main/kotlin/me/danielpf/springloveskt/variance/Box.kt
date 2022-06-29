package me.danielpf.springloveskt.variance

class OutBox<out T>(val value: T) {
    override fun toString() = "OutBox[$value]"
}

class InBox<in T>(private var value: T) {

    fun setValue(value: T) {
        this.value = value
    }

    override fun toString() = "InBox[$value]"
}

class Box<T>(var value: T? = null) {

    fun isEmpty() = this.value == null

    fun clear() {
        this.value = null
    }

    fun copyFrom(source: Box<out T>) {
        this.value = source.value
    }

    fun copyTo(dest: Box<in T>) {
        dest.value = this.value
    }

    override fun toString() = "Box[$value]"
}
