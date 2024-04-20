package byx.script.core.interpreter.builtin;

import byx.script.core.interpreter.value.*;

import java.util.List;

import static byx.script.core.util.ValueUtils.checkArgument;

/**
 * 反射内建函数
 */
public class ReflectFunctions {
    public static final BuiltinFunction TYPE_ID = new BuiltinFunction() {
        @Override
        public String name() {
            return "typeId";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, Value.class);
            return new StringValue(args.get(0).typeId());
        }
    };

    public static final BuiltinFunction HASH_CODE = new BuiltinFunction() {
        @Override
        public String name() {
            return "hashCode";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, Value.class);
            return new IntegerValue(args.get(0).hashCode());
        }
    };

    public static final BuiltinFunction FIELDS = new BuiltinFunction() {
        @Override
        public String name() {
            return "fields";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, ObjectValue.class);
            ObjectValue obj = (ObjectValue) args.get(0);
            List<Value> fieldList = obj.getFields().keySet().stream().map(k -> (Value) new StringValue(k)).toList();
            return new ListValue(fieldList);
        }
    };

    public static final BuiltinFunction SET_FIELD = new BuiltinFunction() {
        @Override
        public String name() {
            return "setField";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, ObjectValue.class, StringValue.class, Value.class);
            ObjectValue obj = (ObjectValue) args.get(0);
            String field = ((StringValue) args.get(1)).value();
            Value value = args.get(2);
            obj.setField(field, value);
            return NullValue.INSTANCE;
        }
    };

    public static final BuiltinFunction GET_FIELD = new BuiltinFunction() {
        @Override
        public String name() {
            return "getField";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, ObjectValue.class, StringValue.class);
            ObjectValue obj = (ObjectValue) args.get(0);
            String field = ((StringValue) args.get(1)).value();
            return obj.getField(field);
        }
    };

    public static final BuiltinFunction HAS_FIELD = new BuiltinFunction() {
        @Override
        public String name() {
            return "hasField";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, ObjectValue.class, StringValue.class);
            ObjectValue obj = (ObjectValue) args.get(0);
            String field = ((StringValue) args.get(1)).value();
            return BoolValue.of(obj.hasField(field));
        }
    };
}
