package byx.script.core.interpreter.value;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface CallableValue extends Value, BiConsumer<List<Value>, Consumer<Value>> {
    @Override
    default String typeId() {
        return "callable";
    }
}
