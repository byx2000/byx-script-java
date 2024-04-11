package byx.script.core.interpreter;

import byx.script.core.interpreter.value.Value;

import java.util.ArrayDeque;
import java.util.function.Consumer;

public class FunctionFrame {
    private final Consumer<Value> returnCont;
    private final ArrayDeque<Runnable> breakStack = new ArrayDeque<>();
    private final ArrayDeque<Runnable> continueStack = new ArrayDeque<>();

    public FunctionFrame(Consumer<Value> returnCont) {
        this.returnCont = returnCont;
    }

    public Consumer<Value> getReturnCont() {
        return returnCont;
    }

    public boolean isBreakStackEmpty() {
        return breakStack.isEmpty();
    }

    public Runnable peekBreakStack() {
        return breakStack.peek();
    }

    public Runnable popBreakStack() {
        return breakStack.pop();
    }

    public void popBreakStackUntil(Runnable cont) {
        while (!breakStack.isEmpty() && breakStack.peek() != cont) {
            breakStack.pop();
        }
    }

    public void pushBreakStack(Runnable cont) {
        breakStack.push(cont);
    }

    public boolean isContinueStackEmpty() {
        return continueStack.isEmpty();
    }

    public Runnable peekContinueStack() {
        return continueStack.peek();
    }

    public Runnable popContinueStack() {
        return continueStack.pop();
    }

    public void popContinueStackUntil(Runnable cont) {
        while (!continueStack.isEmpty() && continueStack.peek() != cont) {
            continueStack.pop();
        }
    }

    public void pushContinueStack(Runnable cont) {
        continueStack.push(cont);
    }
}
