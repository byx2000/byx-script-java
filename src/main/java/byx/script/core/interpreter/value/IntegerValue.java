package byx.script.core.interpreter.value;

import byx.script.core.util.ValueUtils;

public record IntegerValue(int value) implements Value {
    @Override
    public String typeId() {
        return "integer";
    }

    @Override
    public String toString() {
        return ValueUtils.valueToString(this);
    }
}
