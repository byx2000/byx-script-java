package byx.script.interpreter.value;

import byx.script.interpreter.InterpretException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public abstract class FieldReadableValue implements Value {
    private final Map<String, Value> fields = new HashMap<>();

    public Map<String, Value> getFields() {
        return fields;
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    protected void setField(String field, Value value) {
        fields.put(field, value);
    }

    protected void setCallableField(String field, Function<List<Value>, Value> callable) {
        setField(field, Value.of(callable));
    }

    protected void setCallableFieldNoReturn(String field, Consumer<List<Value>> callable) {
        setCallableField(field, args -> {
            callable.accept(args);
            return Value.undefined();
        });
    }

    protected void setCallableField(String field, Supplier<Value> func) {
        setCallableField(field, args -> func.get());
    }

    protected <T extends Value> void setCallableField(String field, Class<T> t, Function<T, Value> func) {
        setCallableField(field, args -> {
            if (args.size() != 1) {
                throw new InterpretException(String.format("function %s need 1 argument", field));
            }
            Value p = args.get(0);
            if (!t.isAssignableFrom(p.getClass())) {
                throw new InterpretException(String.format("function %s need 1 argument with type %s", field, t.getSimpleName()));
            }
            return func.apply(t.cast(p));
        });
    }

    protected <T extends Value> void setCallableFieldNoReturn(String field, Class<T> t, Consumer<T> func) {
        setCallableField(field, t, p -> {
            func.accept(p);
            return Value.undefined();
        });
    }

    protected <T1 extends Value, T2 extends Value> void setCallableField(String field, Class<T1> t1, Class<T2> t2, BiFunction<T1, T2, Value> func) {
        setCallableField(field, args -> {
            if (args.size() != 2) {
                throw new InterpretException(String.format("function %s need 2 arguments", field));
            }
            Value p1 = args.get(0);
            Value p2 = args.get(1);
            if (!(t1.isAssignableFrom(p1.getClass()) && t2.isAssignableFrom(p2.getClass()))) {
                throw new InterpretException(String.format("function %s need 2 arguments with type %s and %s",
                        field, t1.getSimpleName(), t2.getSimpleName()));
            }
            return func.apply(t1.cast(p1), t2.cast(p2));
        });
    }

    protected <T1 extends Value, T2 extends Value> void setCallableFieldNoReturn(String field, Class<T1> t1, Class<T2> t2, BiConsumer<T1, T2> func) {
        setCallableField(field, t1, t2, (p1, p2) -> {
            func.accept(p1, p2);
            return Value.undefined();
        });
    }

    public interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }
    protected <T1 extends Value, T2 extends Value, T3 extends Value> void setCallableField(String field, Class<T1> t1, Class<T2> t2, Class<T3> t3, Function3<T1, T2, T3, Value> func) {
        setCallableField(field, args -> {
            if (args.size() != 3) {
                throw new InterpretException(String.format("function %s need 3 arguments", field));
            }
            Value p1 = args.get(0);
            Value p2 = args.get(1);
            Value p3 = args.get(2);
            if (!(t1.isAssignableFrom(p1.getClass()) && t2.isAssignableFrom(p2.getClass()) && t3.isAssignableFrom(p3.getClass()))) {
                throw new InterpretException(String.format("function %s need 2 arguments with type %s, %s and %s",
                        field, t1.getSimpleName(), t2.getSimpleName(), t3.getSimpleName()));
            }
            return func.apply(t1.cast(p1), t2.cast(p2), t3.cast(p3));
        });
    }

    public interface Consumer3<T1, T2, T3> {
        void accept(T1 t1, T2 t2, T3 t3);
    }

    protected <T1 extends Value, T2 extends Value, T3 extends Value> void setCallableFieldNoReturn(String field, Class<T1> t1, Class<T2> t2, Class<T3> t3, Consumer3<T1, T2, T3> func) {
        setCallableField(field, t1, t2, t3, (p1, p2, p3) -> {
            func.accept(p1, p2, p3);
            return Value.undefined();
        });
    }

    protected void setFields(Map<String, Value> fieldsToAdd) {
        fields.putAll(fieldsToAdd);
    }

    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        return Value.super.getField(field);
    }
}
