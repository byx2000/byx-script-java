package byx.script.core.interpreter.value;

import byx.script.core.util.ValueUtils;

public class NullValue implements Value {
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    @Override
    public String typeId() {
        return "null";
    }

    @Override
    public String toString() {
        return ValueUtils.valueToString(this);
    }
}
