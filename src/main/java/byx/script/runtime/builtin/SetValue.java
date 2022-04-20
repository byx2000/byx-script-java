package byx.script.runtime.builtin;

import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.UndefinedValue;
import byx.script.runtime.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 内建集合对象
 */
public class SetValue extends Value {
    private final Set<Value> set = new HashSet<>();

    public SetValue() {
        setFields(Map.of(
                "add", Value.of(args -> {
                    if (args.size() < 1) {
                        throw new InterpretException("method add need at least 1 argument");
                    }
                    set.addAll(args);
                    return UndefinedValue.INSTANCE;
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
        ));
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
