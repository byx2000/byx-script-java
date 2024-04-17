package byx.script.core.interpreter;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Cont<T> {
    void run(Consumer<T> c);

    static <T> Cont<T> value(T value) {
        return c -> c.accept(value);
    }

    /*static <T> Cont<List<T>> list(List<Cont<T>> contList) {
        return c -> {
            List<T> result = new ArrayList<>();
            contList.forEach(cont -> cont.run(result::add));
            c.accept(result);
        };
    }*/

    default <U> Cont<U> map(Function<T, U> mapper) {
        return c -> run(v -> c.accept(mapper.apply(v)));
    }

    default <U> Cont<U> flatMap(Function<T, Cont<U>> mapper) {
        return c -> run(v -> {
            Cont<U> next = mapper.apply(v);
            next.run(c);
        });
    }
}
