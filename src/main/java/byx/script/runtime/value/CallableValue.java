package byx.script.runtime.value;

import byx.script.runtime.Value;

import java.util.List;
import java.util.function.Function;

public class CallableValue extends Value {
    private final Function<List<Value>, Value> callable;

    public CallableValue(Function<List<Value>, Value> callable) {
        this.callable = callable;
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
