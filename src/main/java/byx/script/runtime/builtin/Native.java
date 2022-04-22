package byx.script.runtime.builtin;

import byx.script.runtime.value.FieldReadableValue;
import byx.script.runtime.value.Value;

/**
 * 本地变量
 */
public class Native extends FieldReadableValue {
    public void addNative(String name, Value value) {
        setField(name, value);
    }
}
