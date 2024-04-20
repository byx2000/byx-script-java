package byx.script.core.interpreter.builtin;

import byx.script.core.interpreter.Cont;
import byx.script.core.interpreter.value.CallableValue;
import byx.script.core.interpreter.value.Value;

import java.util.List;

/**
 * 内建函数
 */
public interface BuiltinFunction extends CallableValue {
    /**
     * 内建函数名
     */
    String name();

    /**
     * 内建函数执行逻辑
     * @param args 参数
     * @return 返回值
     */
    Value onCall(List<Value> args);

    @Override
    default Cont<Value> apply(List<Value> args) {
        return Cont.value(onCall(args));
    }
}
