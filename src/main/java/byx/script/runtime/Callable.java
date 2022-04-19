package byx.script.runtime;

import java.util.List;

public interface Callable {
    Value call(List<Value> args);
}
