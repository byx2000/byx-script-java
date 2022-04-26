package byx.script.interpreter.builtin;

import byx.script.interpreter.value.FieldReadableValue;
import byx.script.interpreter.value.Value;

/**
 * 内建对象Native：包含所有注册的本地变量
 */
public class Native extends FieldReadableValue {
    public void addNative(String name, Value value) {
        setField(name, value);
    }
}
