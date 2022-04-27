package byx.script.interpreter.value;

import java.util.List;
import java.util.Map;

public class ObjectValue extends AbstractValue {
    public ObjectValue(Map<String, Value> fields) {
        setFields(fields);
    }

    @Override
    public String toString() {
        return "Object";
    }

    @Override
    public Value equal(Value rhs) {
        if (hasField("_equal")) {
            return getField("_equal").call(List.of(rhs));
        }
        return super.equal(rhs);
    }

    @Override
    public Value add(Value rhs) {
        if (hasField("_add")) {
            return getField("_add").call(List.of(rhs));
        }
        return super.add(rhs);
    }

    @Override
    public Value sub(Value rhs) {
        if (hasField("_sub")) {
            return getField("_sub").call(List.of(rhs));
        }
        return super.add(rhs);
    }

    @Override
    public Value mul(Value rhs) {
        if (hasField("_mul")) {
            return getField("_mul").call(List.of(rhs));
        }
        return super.add(rhs);
    }

    @Override
    public Value div(Value rhs) {
        if (hasField("_div")) {
            return getField("_div").call(List.of(rhs));
        }
        return super.add(rhs);
    }
}
