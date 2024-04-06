package byx.script.core.interpreter.value;

import java.util.List;
import java.util.function.Function;

public interface CallableValue extends Value, Function<List<Value>, Value> {
    @Override
    default String typeId() {
        return "callable";
    }
}
