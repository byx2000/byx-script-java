package byx.script.core.interpreter.value.builtin;

import byx.script.core.interpreter.value.DoubleValue;
import byx.script.core.interpreter.value.IntegerValue;
import byx.script.core.interpreter.value.ObjectValue;
import byx.script.core.interpreter.value.Value;

import java.util.List;

/**
 * Native.Math
 */
public class Math extends ObjectValue {
    public static final Math INSTANCE = new Math();

    private Math() {
        // 添加内建属性
        setCallableField("sin_d", this::sin);
        setCallableField("cos_d", this::cos);
        setCallableField("tan_d", this::tan);
        setCallableField("pow_d", this::pow);
        setCallableField("exp_d", this::exp);
        setCallableField("ln_d", this::ln);
        setCallableField("log10_d", this::log10);
        setCallableField("sqrt_d", this::sqrt);
        setCallableField("round_d", this::round);
        setCallableField("ceil_d", this::ceil);
        setCallableField("floor_d", this::floor);
    }

    private Value sin(List<Value> args) {
        checkArgument("sin_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.sin(x));
    }

    private Value cos(List<Value> args) {
        checkArgument("cos_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.cos(x));
    }

    private Value tan(List<Value> args) {
        checkArgument("tan_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.tan(x));
    }

    private Value pow(List<Value> args) {
        checkArgument("pow_d", args, DoubleValue.class, DoubleValue.class);
        double a = ((DoubleValue) args.get(0)).value();
        double b = ((DoubleValue) args.get(1)).value();
        return new DoubleValue(java.lang.Math.pow(a, b));
    }
    private Value exp(List<Value> args) {
        checkArgument("exp_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.exp(x));
    }

    private Value ln(List<Value> args) {
        checkArgument("ln_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.log(x));
    }

    private Value log10(List<Value> args) {
        checkArgument("log10_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.log10(x));
    }

    private Value sqrt(List<Value> args) {
        checkArgument("sqrt_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new DoubleValue(java.lang.Math.sqrt(x));
    }

    private Value round(List<Value> args) {
        checkArgument("round_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new IntegerValue((int) java.lang.Math.round(x));
    }

    private Value ceil(List<Value> args) {
        checkArgument("ceil_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new IntegerValue((int) java.lang.Math.ceil(x));
    }

    private Value floor(List<Value> args) {
        checkArgument("floor_d", args, DoubleValue.class);
        double x = ((DoubleValue) args.get(0)).value();
        return new IntegerValue((int) java.lang.Math.floor(x));
    }
}
