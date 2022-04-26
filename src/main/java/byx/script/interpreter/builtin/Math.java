package byx.script.interpreter.builtin;

import byx.script.interpreter.value.DoubleValue;
import byx.script.interpreter.value.FieldReadableValue;
import byx.script.interpreter.value.Value;

/**
 * Native.Math
 */
public class Math extends FieldReadableValue {
    public static final Math INSTANCE = new Math();

    private Math() {
        setCallableField("sin", DoubleValue.class, x -> Value.of(java.lang.Math.sin(x.getValue())));
        setCallableField("cos", DoubleValue.class, x -> Value.of(java.lang.Math.cos(x.getValue())));
        setCallableField("tan", DoubleValue.class, x -> Value.of(java.lang.Math.tan(x.getValue())));
        setCallableField("pow", DoubleValue.class, DoubleValue.class, (x, n) -> Value.of(java.lang.Math.pow(x.getValue(), n.getValue())));
        setCallableField("exp", DoubleValue.class, x -> Value.of(java.lang.Math.exp(x.getValue())));
        setCallableField("ln", DoubleValue.class, x -> Value.of(java.lang.Math.log(x.getValue())));
        setCallableField("log10", DoubleValue.class, x -> Value.of(java.lang.Math.log10(x.getValue())));
        setCallableField("sqrt", DoubleValue.class, x -> Value.of(java.lang.Math.sqrt(x.getValue())));
        setCallableField("round", DoubleValue.class, x -> Value.of((int) java.lang.Math.round(x.getValue())));
        setCallableField("ceil", DoubleValue.class, x -> Value.of((int) java.lang.Math.ceil(x.getValue())));
        setCallableField("floor", DoubleValue.class, x -> Value.of((int) java.lang.Math.floor(x.getValue())));
    }
}
