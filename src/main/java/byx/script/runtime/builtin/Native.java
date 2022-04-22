package byx.script.runtime.builtin;

import byx.script.runtime.value.FieldReadableValue;
import byx.script.runtime.value.Value;

/**
 * 内建对象Native：包含所有注册的本地变量
 */
public class Native extends FieldReadableValue {
    public void addNative(String name, Value value) {
        setField(name, value);
    }
}
