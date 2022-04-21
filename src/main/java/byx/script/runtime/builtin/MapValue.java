package byx.script.runtime.builtin;

import byx.script.runtime.value.FieldReadableValue;
import byx.script.runtime.value.UndefinedValue;
import byx.script.runtime.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 内建哈希表对象
 */
public class MapValue extends FieldReadableValue {
    private final Map<Value, Value> map = new HashMap<>();

    public MapValue() {
        setCallableField("put", Value.class, Value.class, (k, v) -> {
            Value previous = map.put(k, v);
            return previous == null ? UndefinedValue.INSTANCE : previous;
        });
        setCallableField("get", Value.class, k -> {
            Value v = map.get(k);
            return v == null ? UndefinedValue.INSTANCE : v;
        });
        setCallableField("remove", Value.class, k -> {
            Value previous = map.remove(k);
            return previous == null ? UndefinedValue.INSTANCE : previous;
        });
        setCallableField("containsKey", Value.class, k -> Value.of(map.containsKey(k)));
        setCallableField("size", () -> Value.of(map.size()));
    }

    public Map<Value, Value> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Map";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof MapValue) {
            return Value.of(map.equals(((MapValue) rhs).getMap()));
        }
        return super.equal(rhs);
    }
}
