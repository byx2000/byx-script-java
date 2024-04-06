package byx.script.core.interpreter.value;

import byx.script.core.interpreter.exception.ByxScriptRuntimeException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ListValue extends ObjectValue {
    private final LinkedList<Value> elems;

    public ListValue(List<Value> elems) {
        this.elems = new LinkedList<>(elems);

        // 添加内建属性
        setCallableField("length", this::length);
        setCallableField("isEmpty", this::isEmpty);
        setCallableField("addLast", this::addLast);
        setCallableField("removeLast", this::removeLast);
        setCallableField("addFirst", this::addFirst);
        setCallableField("removeFirst", this::removeFirst);
        setCallableField("remove", this::remove);
        setCallableField("remove", this::remove);
        setCallableField("insert", this::insert);
        setCallableField("copy", this::copy);
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
    public Value equal(Value rhs) {
        if (rhs instanceof ListValue) {
            return BoolValue.of(elems.equals(((ListValue) rhs).getElems()));
        }
        return super.equal(rhs);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).value();
            return elems.get(index);
        }
        return super.subscript(sub);
    }

    @Override
    public void setSubscript(Value subscript, Value rhs) {
        if (!(subscript instanceof IntegerValue)) {
            throw new ByxScriptRuntimeException("subscript must be integer");
        }
        int index = ((IntegerValue) subscript).value();
        this.elems.set(index, rhs);
    }

    private Value length(List<Value> args) {
        return new IntegerValue(elems.size());
    }

    private Value isEmpty(List<Value> args) {
        return BoolValue.of(elems.isEmpty());
    }

    private Value addLast(List<Value> args) {
        args.forEach(elems::addLast);
        return NullValue.INSTANCE;
    }

    private Value removeLast(List<Value> args) {
        return elems.removeLast();
    }

    private Value addFirst(List<Value> args) {
        args.forEach(elems::addFirst);
        return NullValue.INSTANCE;
    }

    private Value removeFirst(List<Value> args) {
        return elems.removeFirst();
    }

    private Value remove(List<Value> args) {
        checkArgument("remove", args, IntegerValue.class);
        int index = ((IntegerValue) args.get(0)).value();
        return this.elems.remove(index);
    }

    private Value insert(List<Value> args) {
        checkArgument("insert", args, IntegerValue.class, Value.class);
        int index = ((IntegerValue) args.get(0)).value();
        Value element = args.get(1);
        elems.add(index, element);
        return NullValue.INSTANCE;
    }

    private Value copy(List<Value> args) {
        return new ListValue(new ArrayList<>(elems));
    }
}
