package byx.script.common;

/**
 * 模拟两个元素的元组
 * @param <T> 第一个元素的类型
 * @param <U> 第二个元素的类型
 */
public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
