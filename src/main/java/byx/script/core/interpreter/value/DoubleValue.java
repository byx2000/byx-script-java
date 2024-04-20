package byx.script.core.interpreter.value;

import byx.script.core.util.ValueUtils;

public record DoubleValue(double value) implements Value {
    @Override
    public String typeId() {
        return "double";
    }

    @Override
    public String toString() {
        return ValueUtils.valueToString(this);
    }
}
