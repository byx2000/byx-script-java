package byx.script.runtime.builtin;

import byx.script.runtime.exception.InterpretException;
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
        setFields(java.util.Map.of(
                "put", Value.of(args -> {
                    if (args.size() != 2) {
                        throw new InterpretException("method put need 2 arguments");
                    }
                    Value previous = map.put(args.get(0), args.get(1));
                    return previous == null ? UndefinedValue.INSTANCE : previous;
                }),
                "get", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("method put need 1 argument");
                    }
                    Value v = map.get(args.get(0));
                    return v == null ? UndefinedValue.INSTANCE : v;
                }),
                "remove", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("method put need 1 argument");
                    }
                    Value previous = map.remove(args.get(0));
                    return previous == null ? UndefinedValue.INSTANCE : previous;
                }),
                "containsKey", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("method put need 1 argument");
                    }
                    return Value.of(map.containsKey(args.get(0)));
                }),
                "size", Value.of(args -> Value.of(map.size()))
        ));
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
