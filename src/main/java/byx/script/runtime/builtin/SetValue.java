package byx.script.runtime.builtin;

import byx.script.runtime.value.FieldReadableValue;
import byx.script.runtime.value.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 内建集合对象
 */
public class SetValue extends FieldReadableValue {
    private final Set<Value> set = new HashSet<>();

    public SetValue() {
        setCallableFieldNoReturn("add", Value.class, set::add);
        setCallableField("remove", Value.class, e -> Value.of(set.remove(e)));
        setCallableField("contains", Value.class, e -> Value.of(set.contains(e)));
        setCallableField("size", () -> Value.of(set.size()));
        setCallableField("isEmpty", () -> Value.of(set.isEmpty()));
        setCallableField("toList", () -> Value.of(new ArrayList<>(set)));
    }

    public Set<Value> getSet() {
        return set;
    }

    @Override
    public String toString() {
        return "Set";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof SetValue) {
            return Value.of(set.equals(((SetValue) rhs).getSet()));
        }
        return super.equal(rhs);
    }
}
