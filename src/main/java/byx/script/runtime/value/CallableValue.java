package byx.script.runtime.value;

import java.util.List;
import java.util.function.Function;

public class CallableValue implements Value {
    private final Function<List<Value>, Value> callable;

    public CallableValue(Function<List<Value>, Value> callable) {
        this.callable = callable;
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof UndefinedValue) {
            return Value.of(false);
        }
        return Value.super.equal(rhs);
    }

    @Override
    public Value call(List<Value> args) {
        return callable.apply(args);
    }

    @Override
    public String toString() {
        return "Callable";
    }
}