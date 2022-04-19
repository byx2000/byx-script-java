package byx.script.runtime.builtin;

import byx.script.runtime.InterpretException;
import byx.script.runtime.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * 内建集合对象
 */
public class Set implements Value {
    private final java.util.Set<Value> set = new HashSet<>();
    private final Map<String, Value> fields = Map.of(
            "add", Value.of(args -> {
                if (args.size() < 1) {
                    throw new InterpretException("method add need at least 1 argument");
                }
                set.addAll(args);
                return UNDEFINED;
            }),
            "remove", Value.of(args -> {
                if (args.size() != 1) {
                    throw new InterpretException("method add need 1 argument");
                }
                return Value.of(set.remove(args.get(0)));
            }),
            "contains", Value.of(args -> {
                if (args.size() != 1) {
                    throw new InterpretException("method add need 1 argument");
                }
                return Value.of(set.contains(args.get(0)));
            }),
            "size", Value.of(args -> Value.of(set.size())),
            "isEmpty", Value.of(args -> Value.of(set.isEmpty())),
            "toList", Value.of(args -> Value.of(new ArrayList<>(set)))
    );

    public java.util.Set<Value> getSet() {
        return set;
    }

    @Override
    public String toString() {
        return "Set";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof Set) {
            return Value.of(set.equals(((Set) rhs).getSet()));
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
