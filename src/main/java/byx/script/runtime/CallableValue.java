package byx.script.runtime;

import java.util.List;

public class CallableValue implements Value {
    private final Callable callable;

    public CallableValue(Callable callable) {
        this.callable = callable;
    }

    @Override
    public Value call(List<Value> args) {
        return callable.call(args);
    }

    @Override
    public String toString() {
        return "Callable";
    }
}
