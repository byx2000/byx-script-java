package byx.script.core.interpreter.value;

import byx.script.core.interpreter.Cont;

import java.util.List;
import java.util.function.Function;

public interface CallableValue extends Value, Function<List<Value>, Cont<Value>> {
    @Override
    default String typeId() {
        return "callable";
    }
}
