package byx.script.core.interpreter.value;

import java.util.List;
import java.util.Objects;

import static byx.script.core.util.ValueUtils.checkArgument;

public class StringValue extends ObjectValue {
    private final String value;

    public StringValue(String value) {
        this.value = value;

        // 添加内建属性
        setCallableField("length", this::length);
        setCallableField("substring", this::substring);
        setCallableField("concat", this::concat);
        setCallableField("charAt", this::charAt);
        setCallableField("codeAt", this::codeAt);
        setCallableField("compareTo", this::compareTo);
        setCallableField("toInt", this::toInt);
        setCallableField("toDouble", this::toDouble);
        setCallableField("toBool", this::toBool);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private Value length(List<Value> args) {
        return new IntegerValue(value.length());
    }

    private Value substring(List<Value> args) {
        checkArgument("substring", args, IntegerValue.class, IntegerValue.class);
        int begin = ((IntegerValue) args.get(0)).value();
        int end = ((IntegerValue) args.get(1)).value();
        return new StringValue(value.substring(begin, end));
    }

    private Value concat(List<Value> args) {
        checkArgument("concat", args, StringValue.class);
        String s = ((StringValue) args.get(0)).value();
        return new StringValue(value.concat(s));
    }

    private Value charAt(List<Value> args) {
        checkArgument("charAt", args, IntegerValue.class);
        int index = ((IntegerValue) args.get(0)).value();
        return new StringValue(String.valueOf(value.charAt(index)));
    }

    private Value codeAt(List<Value> args) {
        checkArgument("codeAt", args, IntegerValue.class);
        int index = ((IntegerValue) args.get(0)).value();
        return new IntegerValue(value.charAt(index));
    }

    private Value compareTo(List<Value> args) {
        checkArgument("compareTo", args, StringValue.class);
        String s = ((StringValue) args.get(0)).value();
        return new IntegerValue(value.compareTo(s));
    }

    private Value toInt(List<Value> args) {
        return new IntegerValue(Integer.parseInt(value));
    }

    private Value toDouble(List<Value> args) {
        return new DoubleValue(Double.parseDouble(value));
    }

    private Value toBool(List<Value> args) {
        return BoolValue.of(Boolean.parseBoolean(value));
    }

    @Override
    public String typeId() {
        return "string";
    }
}
