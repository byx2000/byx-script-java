package util;

import byx.script.core.interpreter.Cont;
import byx.script.core.interpreter.exception.JumpException;

import java.util.function.Consumer;

/**
 * 堆栈保护工具类
 */
public class GuardUtils {
    private static final ThreadLocal<Integer> CURRENT_DEPTH_HOLDER = new ThreadLocal<>();

    public static void run(Runnable runnable, int maxDepth) {
        while (true) {
            try {
                CURRENT_DEPTH_HOLDER.set(maxDepth);
                runnable.run();
                CURRENT_DEPTH_HOLDER.remove();
                return;
            } catch (JumpException e) {
                runnable = e.getRunnable();
            }
        }
    }

    public static void guard(Runnable restart) {
        int depth = CURRENT_DEPTH_HOLDER.get();
        if (depth <= 0) {
            throw new JumpException(restart);
        }
        CURRENT_DEPTH_HOLDER.set(depth - 1);
    }

    public static <T> Consumer<T> wrap(Consumer<T> consumer) {
        return new Consumer<>() {
            @Override
            public void accept(T t) {
                guard(() -> this.accept(t));
                consumer.accept(t);
            }
        };
    }

    public static <T> Cont<T> wrapCont(Cont<T> cont) {
        return new Cont<>() {
            @Override
            public void run(Consumer<T> c) {
                guard(() -> this.run(c));
                cont.run(c);
            }
        };
    }
}
