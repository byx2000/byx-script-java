package byx.script.runtime;

import java.util.*;

public class ListValue implements Value {
    private final LinkedList<Value> value;
    private final Map<String, Value> FIELDS;

    public ListValue(List<Value> value) {
        this.value = new LinkedList<>(value);
        FIELDS = Map.of(
                "addLast", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("addLast method require 1 argument");
                    }
                    this.value.addLast(args.get(0));
                    return UNDEFINED;
                }),
                "removeLast", Value.of(args -> this.value.removeLast()),
                "addFirst", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("addFirst method require 1 argument");
                    }
                    this.value.addFirst(args.get(0));
                    return UNDEFINED;
                }),
                "removeFirst", Value.of(args -> this.value.removeFirst()),
                "remove", Value.of(args -> {
                    if (args.size() != 1 || !(args.get(0) instanceof IntegerValue)) {
                        throw new InterpretException("remove method require 1 integer argument");
                    }
                    int index = ((IntegerValue) args.get(0)).getValue();
                    return this.value.remove(index);
                }),
                "insert", Value.of(args -> {
                    if (args.size() != 2 || !(args.get(0) instanceof IntegerValue)) {
                        throw new InterpretException("substring method require 2 arguments");
                    }
                    int index = ((IntegerValue) args.get(0)).getValue();
                    this.value.add(index, args.get(1));
                    return UNDEFINED;
                }),
                "length", Value.of(args -> Value.of(this.value.size())),
                "isEmpty", Value.of(args -> Value.of(this.value.isEmpty())),
                "copy", Value.of(args -> Value.of(new ArrayList<>(this.value)))
        );
    }

    public List<Value> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListValue listValue = (ListValue) o;
        return Objects.equals(value, listValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "List";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof UndefinedValue) {
            return Value.of(false);
        }
        return Value.super.equal(rhs);
    }

    @Override
    public Value getField(String field) {
        if (FIELDS.containsKey(field)) {
            return FIELDS.get(field);
        }
        return Value.super.getField(field);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).getValue();
            return value.get(index);
        }
        return Value.super.subscript(sub);
    }

    @Override
    public void subscriptAssign(Value subscript, Value value) {
        if (!(subscript instanceof IntegerValue)) {
            throw new InterpretException("subscript must be integer");
        }
        int index = ((IntegerValue) subscript).getValue();
        this.value.set(index, value);
    }
}
