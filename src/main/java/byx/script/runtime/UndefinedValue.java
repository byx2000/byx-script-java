package byx.script.runtime;

public class UndefinedValue implements Value {
    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue("undefined" + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        return Value.of(rhs instanceof UndefinedValue);
    }

    @Override
    public Value notEqual(Value rhs) {
        return Value.of(!(rhs instanceof UndefinedValue));
    }
}
