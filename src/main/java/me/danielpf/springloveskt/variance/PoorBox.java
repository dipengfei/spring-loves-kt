package me.danielpf.springloveskt.variance;

public class PoorBox<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T t) {
        this.value = t;
    }

    public void clear() {
        this.value = null;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public void poorCopyFrom(PoorBox<T> source) {
        this.value = source.get();
    }

    public void poorCopyTo(PoorBox<T> dest) {
        dest.set(this.value);
    }



    /*
     * Effective Java - Item 31: Use bounded wild cards to increase API flexibility
     *
     * '? extends T' makes type PoorBox is covariant on parameter T, when it ONLY 'produce' data;
     * '? super T' makes type PoorBox is contravariant on parameter T, when it ONLY 'consume' data.
     *
     * PECS stands for Producer-Extends, Consumer-Super.
     *
     * */

    public void copyFrom(PoorBox<? extends T> source) {
        this.value = source.get();
    }

    public void copyTo(PoorBox<? super T> dest) {
        dest.set(this.value);
    }
}
