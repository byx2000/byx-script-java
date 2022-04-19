package byx.script.runtime.builtin;

import byx.script.runtime.InterpretException;
import byx.script.runtime.Value;

import java.util.HashMap;

/**
 * 内建哈希表对象
 */
public class Map implements Value {
    private final java.util.Map<Value, Value> map = new HashMap<>();
    private final java.util.Map<String, Value> fields = java.util.Map.of(
            "put", Value.of(args -> {
                if (args.size() != 2) {
                    throw new InterpretException("method put need 2 arguments");
                }
                Value previous = map.put(args.get(0), args.get(1));
                return previous == null ? UNDEFINED : previous;
            }),
            "get", Value.of(args -> {
                if (args.size() != 1) {
                    throw new InterpretException("method put need 1 argument");
                }
                Value v = map.get(args.get(0));
                return v == null ? UNDEFINED : v;
            }),
            "remove", Value.of(args -> {
                if (args.size() != 1) {
                    throw new InterpretException("method put need 1 argument");
                }
                Value previous = map.remove(args.get(0));
                return previous == null ? UNDEFINED : previous;
            }),
            "containsKey", Value.of(args -> {
                if (args.size() != 1) {
                    throw new InterpretException("method put need 1 argument");
                }
                return Value.of(map.containsKey(args.get(0)));
            }),
            "size", Value.of(args -> Value.of(map.size()))
    );

    public java.util.Map<Value, Value> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Map";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof Map) {
            return Value.of(map.equals(((Map) rhs).getMap()));
        }
        return Value.super.equal(rhs);
    }

    @Override
    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        return Value.super.getField(field);
    }
}
