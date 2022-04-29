package byx.script.interpreter.value;

import byx.script.interpreter.InterpretException;

import java.util.*;

public class ListValue extends AbstractValue {
    private final LinkedList<Value> elems;

    public ListValue(List<Value> elems) {
        this.elems = new LinkedList<>(elems);
        setCallableFieldNoReturn("addLast", Value.class, this.elems::addLast);
        setCallableField("removeLast", this.elems::removeLast);
        setCallableFieldNoReturn("addFirst", Value.class, this.elems::addFirst);
        setCallableField("removeFirst", this.elems::removeFirst);
        setCallableField("remove", IntegerValue.class, index -> this.elems.remove(index.getValue()));
        setCallableFieldNoReturn("insert", IntegerValue.class, Value.class, (index, e) -> this.elems.add(index.getValue(), e));
        setCallableField("length", () -> Value.of(this.elems.size()));
        setCallableField("isEmpty", () -> Value.of(this.elems.isEmpty()));
        setCallableField("copy", () -> Value.of(new ArrayList<>(this.elems)));
    }

    public List<Value> getElems() {
        return elems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListValue listValue = (ListValue) o;
        return Objects.equals(elems, listValue.elems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elems);
    }

    @Override
    public String toString() {
        return elems.toString();
    }

    @Override
    public String typeId() {
        return "list";
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof ListValue) {
            return Value.of(elems.equals(((ListValue) rhs).getElems()));
        }
        return super.equal(rhs);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).getValue();
            return elems.get(index);
        }
        return super.subscript(sub);
    }

    @Override
    public void setSubscript(Value subscript, Value rhs) {
        if (!(subscript instanceof IntegerValue)) {
            throw new InterpretException("subscript must be integer");
        }
        int index = ((IntegerValue) subscript).getValue();
        this.elems.set(index, rhs);
    }
}
