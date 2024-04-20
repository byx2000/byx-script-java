package byx.script.core.interpreter.value;

import byx.script.core.util.ValueUtils;

import java.util.Objects;

public class BoolValue implements Value {
    public static final BoolValue TRUE = new BoolValue(true);
    public static final BoolValue FALSE = new BoolValue(false);

    public static BoolValue of(boolean b) {
        return b ? TRUE : FALSE;
    }

    private final boolean value;

    private BoolValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolValue boolValue = (BoolValue) o;
        return value == boolValue.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ValueUtils.valueToString(this);
    }

    @Override
    public String typeId() {
        return "bool";
    }
}
