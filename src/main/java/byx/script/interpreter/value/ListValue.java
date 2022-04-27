package byx.script.interpreter.value;

import byx.script.interpreter.InterpretException;

import java.util.*;

public class ListValue extends AbstractValue {
    private final LinkedList<Value> value;

    public ListValue(List<Value> value) {
        this.value = new LinkedList<>(value);
        setCallableFieldNoReturn("addLast", Value.class, this.value::addLast);
        setCallableField("removeLast", this.value::removeLast);
        setCallableFieldNoReturn("addFirst", Value.class, this.value::addFirst);
        setCallableField("removeFirst", this.value::removeFirst);
        setCallableField("remove", IntegerValue.class, index -> this.value.remove(index.getValue()));
        setCallableFieldNoReturn("insert", IntegerValue.class, Value.class, (index, e) -> this.value.add(index.getValue(), e));
        setCallableField("length", () -> Value.of(this.value.size()));
        setCallableField("isEmpty", () -> Value.of(this.value.isEmpty()));
        setCallableField("copy", () -> Value.of(new ArrayList<>(this.value)));
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
        if (rhs instanceof ListValue) {
            return Value.of(value.equals(((ListValue) rhs).getValue()));
        }
        return super.equal(rhs);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).getValue();
            return value.get(index);
        }
        return super.subscript(sub);
    }

    @Override
    public void setSubscript(Value subscript, Value rhs) {
        if (!(subscript instanceof IntegerValue)) {
            throw new InterpretException("subscript must be integer");
        }
        int index = ((IntegerValue) subscript).getValue();
        this.value.set(index, rhs);
    }
}
