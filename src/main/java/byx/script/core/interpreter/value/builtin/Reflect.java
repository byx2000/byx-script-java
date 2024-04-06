package byx.script.core.interpreter.value.builtin;

import byx.script.core.interpreter.value.*;

import java.util.List;

/**
 * Native.Reflect
 */
public class Reflect extends ObjectValue {
    public static final Reflect INSTANCE = new Reflect();

    private Reflect() {
        // 添加内建属性
        setCallableField("typeId", this::typeId);
        setCallableField("hashCode", this::hashCode);
        setCallableField("fields", this::fields);
        setCallableField("setField", this::setField);
        setCallableField("getField", this::getField);
        setCallableField("hasField", this::hasField);
    }

    private Value typeId(List<Value> args) {
        checkArgument("typeId", args, Value.class);
        return new StringValue(args.get(0).typeId());
    }

    private Value hashCode(List<Value> args) {
        checkArgument("hashCode", args, Value.class);
        return new IntegerValue(args.get(0).hashCode());
    }

    private Value fields(List<Value> args) {
        checkArgument("fields", args, ObjectValue.class);
        ObjectValue obj = (ObjectValue) args.get(0);
        List<Value> fieldList = obj.getFields().keySet().stream().map(k -> (Value) new StringValue(k)).toList();
        return new ListValue(fieldList);
    }

    private Value setField(List<Value> args) {
        checkArgument("setField", args, ObjectValue.class, StringValue.class, Value.class);
        ObjectValue obj = (ObjectValue) args.get(0);
        String field = ((StringValue) args.get(1)).getValue();
        Value value = args.get(2);
        obj.setField(field, value);
        return NullValue.INSTANCE;
    }

    private Value getField(List<Value> args) {
        checkArgument("getField", args, ObjectValue.class, StringValue.class);
        ObjectValue obj = (ObjectValue) args.get(0);
        String field = ((StringValue) args.get(1)).getValue();
        return obj.getField(field);
    }

    private Value hasField(List<Value> args) {
        checkArgument("hasField", args, ObjectValue.class, StringValue.class);
        ObjectValue obj = (ObjectValue) args.get(0);
        String field = ((StringValue) args.get(1)).getValue();
        return BoolValue.of(obj.hasField(field));
    }
}
