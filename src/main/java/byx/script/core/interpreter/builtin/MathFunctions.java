package byx.script.core.interpreter.builtin;

import byx.script.core.interpreter.exception.BuiltinFunctionException;
import byx.script.core.interpreter.value.DoubleValue;
import byx.script.core.interpreter.value.IntegerValue;
import byx.script.core.interpreter.value.Value;

import java.util.List;

import static byx.script.core.util.ValueUtils.buildArgumentExceptionMsg;
import static byx.script.core.util.ValueUtils.checkArgument;

/**
 * 数学内建函数
 */
public class MathFunctions {
    public static final BuiltinFunction SIN = new BuiltinFunction() {
        @Override
        public String name() {
            return "sin";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.sin(x));
        }
    };

    public static final BuiltinFunction COS = new BuiltinFunction() {
        @Override
        public String name() {
            return "cos";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.cos(x));
        }
    };

    public static final BuiltinFunction TAN = new BuiltinFunction() {
        @Override
        public String name() {
            return "tan";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.tan(x));
        }
    };

    public static final BuiltinFunction POW = new BuiltinFunction() {
        @Override
        public String name() {
            return "pow";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class), List.of(DoubleValue.class, IntegerValue.class));
            double a = getDouble(args.get(0));
            double b = getDouble(args.get(1));
            return new DoubleValue(Math.pow(a, b));
        }
    };

    public static final BuiltinFunction EXP = new BuiltinFunction() {
        @Override
        public String name() {
            return "exp";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.exp(x));
        }
    };

    public static final BuiltinFunction LN = new BuiltinFunction() {
        @Override
        public String name() {
            return "ln";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.log(x));
        }
    };

    public static final BuiltinFunction LOG10 = new BuiltinFunction() {
        @Override
        public String name() {
            return "log10";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.log10(x));
        }
    };

    public static final BuiltinFunction SQRT = new BuiltinFunction() {
        @Override
        public String name() {
            return "sqrt";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new DoubleValue(Math.sqrt(x));
        }
    };

    public static final BuiltinFunction ROUND = new BuiltinFunction() {
        @Override
        public String name() {
            return "round";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new IntegerValue((int) Math.round(x));
        }
    };

    public static final BuiltinFunction CEIL = new BuiltinFunction() {
        @Override
        public String name() {
            return "ceil";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new IntegerValue((int) Math.ceil(x));
        }
    };

    public static final BuiltinFunction FLOOR = new BuiltinFunction() {
        @Override
        public String name() {
            return "floor";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(DoubleValue.class, IntegerValue.class));
            double x = getDouble(args.get(0));
            return new IntegerValue((int) Math.floor(x));
        }
    };

    public static final BuiltinFunction ABS = new BuiltinFunction() {
        @Override
        public String name() {
            return "abs";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(IntegerValue.class, DoubleValue.class));
            Value value = args.get(0);
            if (value instanceof IntegerValue v) {
                return new IntegerValue(Math.abs(v.value()));
            } else if (args.get(0) instanceof DoubleValue v) {
                return new DoubleValue(Math.abs(v.value()));
            } else {
                throw new BuiltinFunctionException(buildArgumentExceptionMsg(name(), args, List.of(IntegerValue.class, DoubleValue.class)));
            }
        }
    };

    public static final BuiltinFunction MAX = new BuiltinFunction() {
        @Override
        public String name() {
            return "max";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(IntegerValue.class, DoubleValue.class), List.of(IntegerValue.class, DoubleValue.class));
            Value v1 = args.get(0);
            Value v2 = args.get(1);
            if (v1 instanceof IntegerValue a && v2 instanceof IntegerValue b) {
                return new IntegerValue(Math.max(a.value(), b.value()));
            } else if (v1 instanceof DoubleValue a && v2 instanceof DoubleValue b) {
                return new DoubleValue(Math.max(a.value(), b.value()));
            } else if (v1 instanceof IntegerValue a && v2 instanceof DoubleValue b) {
                return new DoubleValue(Math.max(a.value(), b.value()));
            } else if (v1 instanceof DoubleValue a && v2 instanceof IntegerValue b) {
                return new DoubleValue(Math.max(a.value(), b.value()));
            } else {
                throw new BuiltinFunctionException(buildArgumentExceptionMsg(name(), args, List.of(IntegerValue.class, DoubleValue.class), List.of(IntegerValue.class, DoubleValue.class)));
            }
        }
    };

    public static final BuiltinFunction MIN = new BuiltinFunction() {
        @Override
        public String name() {
            return "min";
        }

        @Override
        public Value onCall(List<Value> args) {
            checkArgument(name(), args, List.of(IntegerValue.class, DoubleValue.class), List.of(IntegerValue.class, DoubleValue.class));
            Value v1 = args.get(0);
            Value v2 = args.get(1);
            if (v1 instanceof IntegerValue a && v2 instanceof IntegerValue b) {
                return new IntegerValue(Math.min(a.value(), b.value()));
            } else if (v1 instanceof DoubleValue a && v2 instanceof DoubleValue b) {
                return new DoubleValue(Math.min(a.value(), b.value()));
            } else if (v1 instanceof IntegerValue a && v2 instanceof DoubleValue b) {
                return new DoubleValue(Math.min(a.value(), b.value()));
            } else if (v1 instanceof DoubleValue a && v2 instanceof IntegerValue b) {
                return new DoubleValue(Math.min(a.value(), b.value()));
            } else {
                throw new BuiltinFunctionException(buildArgumentExceptionMsg(name(), args, List.of(IntegerValue.class, DoubleValue.class), List.of(IntegerValue.class, DoubleValue.class)));
            }
        }
    };

    private static double getDouble(Value value) {
        if (value instanceof IntegerValue v) {
            return v.value();
        } else {
            return ((DoubleValue) value).value();
        }
    }
}
