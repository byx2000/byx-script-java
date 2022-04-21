package byx.script.runtime.value;

import byx.script.runtime.exception.InterpretException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public abstract class FieldReadableValue implements Value {
    private final Map<String, Value> fields = new HashMap<>();

    public Map<String, Value> getFields() {
        return fields;
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
            return UndefinedValue.INSTANCE;
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
                throw new InterpretException(String.format("function %s need 1 argument with type %s", field, t));
            }
            return func.apply(t.cast(p));
        });
    }

    protected <T extends Value> void setCallableFieldNoReturn(String field, Class<T> t, Consumer<T> func) {
        setCallableField(field, t, p -> {
            func.accept(p);
            return UndefinedValue.INSTANCE;
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
                throw new InterpretException(String.format("function %s need 2 arguments with type %s and %s", field, t1, t2));
            }
            return func.apply(t1.cast(p1), t2.cast(p2));
        });
    }

    protected <T1 extends Value, T2 extends Value> void setCallableFieldNoReturn(String field, Class<T1> t1, Class<T2> t2, BiConsumer<T1, T2> func) {
        setCallableField(field, t1, t2, (p1, p2) -> {
            func.accept(p1, p2);
            return UndefinedValue.INSTANCE;
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
