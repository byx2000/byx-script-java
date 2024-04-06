package byx.script.core.interpreter.value;

import java.util.List;
import java.util.function.Function;

public class CallableValue implements Value {
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
        return callable.toString();
    }
}
