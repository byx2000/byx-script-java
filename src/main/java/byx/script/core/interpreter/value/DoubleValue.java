package byx.script.core.interpreter.value;

public record DoubleValue(double value) implements Value {
    @Override
    public Value add(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value + ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value + ((DoubleValue) rhs).value());
        } else if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value sub(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value - ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value - ((DoubleValue) rhs).value());
        }
        return Value.super.sub(rhs);
    }

    @Override
    public Value mul(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value * ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value * ((DoubleValue) rhs).value());
        }
        return Value.super.mul(rhs);
    }

    @Override
    public Value div(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value / ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value / ((DoubleValue) rhs).value());
        }
        return Value.super.div(rhs);
    }


    @Override
    public Value neg() {
        return new DoubleValue(-value);
    }

    @Override
    public Value lessThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value < ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value < ((DoubleValue) rhs).value());
        }
        return Value.super.lessThan(rhs);
    }

    @Override
    public Value lessEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value <= ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value <= ((DoubleValue) rhs).value());
        }
        return Value.super.lessEqualThan(rhs);
    }

    @Override
    public Value greaterThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value > ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value > ((DoubleValue) rhs).value());
        }
        return Value.super.greaterThan(rhs);
    }

    @Override
    public Value greaterEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value >= ((IntegerValue) rhs).value());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value >= ((DoubleValue) rhs).value());
        }
        return Value.super.greaterEqualThan(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value == ((DoubleValue) rhs).value());
        }
        return BoolValue.FALSE;
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value != ((DoubleValue) rhs).value());
        }
        return BoolValue.TRUE;
    }
}
