package byx.script.core.interpreter.value;

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
        return String.valueOf(value);
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value and(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value && ((BoolValue) rhs).getValue());
        }
        return Value.super.and(rhs);
    }

    @Override
    public Value or(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value || ((BoolValue) rhs).getValue());
        }
        return Value.super.or(rhs);
    }

    @Override
    public Value not() {
        return new BoolValue(!value);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value == ((BoolValue) rhs).getValue());
        }
        return new BoolValue(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value != ((BoolValue) rhs).getValue());
        }
        return new BoolValue(true);
    }
}
