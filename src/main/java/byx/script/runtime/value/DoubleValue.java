package byx.script.runtime.value;

import byx.script.runtime.Value;

import java.util.Objects;

public class DoubleValue extends Value {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleValue that = (DoubleValue) o;
        return Double.compare(that.value, value) == 0;
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
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value + ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value + ((DoubleValue) rhs).getValue());
        } else if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        }
        return super.add(rhs);
    }

    @Override
    public Value sub(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value - ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value - ((DoubleValue) rhs).getValue());
        }
        return super.sub(rhs);
    }

    @Override
    public Value mul(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value * ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value * ((DoubleValue) rhs).getValue());
        }
        return super.mul(rhs);
    }

    @Override
    public Value div(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value / ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value / ((DoubleValue) rhs).getValue());
        }
        return super.div(rhs);
    }


    @Override
    public Value neg() {
        return new DoubleValue(-value);
    }

    @Override
    public Value lessThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value < ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value < ((DoubleValue) rhs).getValue());
        }
        return super.lessThan(rhs);
    }

    @Override
    public Value lessEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value <= ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value <= ((DoubleValue) rhs).getValue());
        }
        return super.lessEqualThan(rhs);
    }

    @Override
    public Value greaterThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value > ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value > ((DoubleValue) rhs).getValue());
        }
        return super.greaterThan(rhs);
    }

    @Override
    public Value greaterEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value >= ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value >= ((DoubleValue) rhs).getValue());
        }
        return super.greaterEqualThan(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value == ((DoubleValue) rhs).getValue());
        }
        return BoolValue.of(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value != ((DoubleValue) rhs).getValue());
        }
        return BoolValue.of(true);
    }
}
