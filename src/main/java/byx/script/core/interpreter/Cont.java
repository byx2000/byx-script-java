package byx.script.core.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static byx.script.core.util.GuardUtils.wrap;
import static byx.script.core.util.GuardUtils.wrapCont;

/**
 * 封装Continuation
 */
public interface Cont<T> {
    void run(Consumer<T> c);

    static <T> Cont<T> value(T value) {
        return c -> c.accept(value);
    }

    static <T> Cont<T> lazy(Supplier<T> valueSupplier) {
        return c -> c.accept(valueSupplier.get());
    }

    static <T> Cont<List<T>> list(List<Cont<T>> conts) {
        return list(conts, new ArrayList<>(), 0);
    }

    private static <T> Cont<List<T>> list(List<Cont<T>> conts, List<T> values, int i) {
        if (i == conts.size()) {
            return value(values);
        } else {
            return conts.get(i).flatMap(v -> {
                values.add(v);
                return list(conts, values, i + 1);
            });
        }
    }

    default <U> Cont<U> map(Function<T, U> mapper) {
        return wrapCont(c -> run(wrap(v -> c.accept(mapper.apply(v)))));
    }

    default <U> Cont<U> flatMap(Function<T, Cont<U>> mapper) {
        return wrapCont(c -> run(wrap(v -> {
            Cont<U> next = mapper.apply(v);
            next.run(c);
        })));
    }
}