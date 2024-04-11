package byx.script.core.interpreter;

import byx.script.core.common.Pair;
import byx.script.core.interpreter.exception.*;
import byx.script.core.interpreter.value.*;
import byx.script.core.parser.ast.Program;
import byx.script.core.parser.ast.expr.*;
import byx.script.core.parser.ast.stmt.*;

import java.util.*;
import java.util.function.Consumer;

import static byx.script.core.interpreter.GuardUtils.*;

public class ByxScriptEvaluator {
    private static final ThreadLocal<FunctionStack> FUNCTION_STACK_HOLDER = new ThreadLocal<>();

    private record TryContext(
        String catchVar,
        Statement catchBody,
        Runnable contAfterTry,
        Scope scope,
        FunctionFrame frame,
        Runnable breakCont,
        Runnable continueCont
    ) {}
    private static final ThreadLocal<ArrayDeque<TryContext>> TRY_STACK_HOLDER = new ThreadLocal<>();

    public static void execute(Program program, Scope scope) {
        FUNCTION_STACK_HOLDER.set(new FunctionStack());
        TRY_STACK_HOLDER.set(new ArrayDeque<>());
        run(() -> execStmtList(program.stmts(), scope, () -> {}), 1000);
        FUNCTION_STACK_HOLDER.remove();
        TRY_STACK_HOLDER.remove();
    }

    private static void evalExpr(Expr node, Scope scope, Consumer<Value> cont) {
        guard(() -> evalExpr(node, scope, cont));
        checkThreadInterrupt();

        if (node instanceof Literal n) {
            cont.accept(n.value());
        } else if (node instanceof ListLiteral n) {
            evalExprList(n.elems(), scope, wrap(values -> cont.accept(new ListValue(values))));
        } else if (node instanceof ObjectLiteral n) {
            evalObjectLiteral(new ArrayList<>(n.fields().entrySet()), new HashMap<>(), 0, scope, cont);
        } else if (node instanceof CallableLiteral n) {
            cont.accept(makeCallableValue(n, scope));
        } else if (node instanceof Var n) {
            cont.accept(scope.getVar(n.varName()));
        } else if (node instanceof UnaryExpr n) {
            evalExpr(n.expr(), scope, wrap(v -> {
                switch (n.op()) {
                    case Not -> cont.accept(evalNot(v));
                    case Neg -> cont.accept(evalNeg(v));
                }
            }));
        } else if (node instanceof BinaryExpr n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            switch (n.op()) {
                case Add -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalAdd(v1, v2))))));
                case Sub -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalSub(v1, v2))))));
                case Mul -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalMul(v1, v2))))));
                case Div -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalDiv(v1, v2))))));
                case Rem -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalRem(v1, v2))))));
                case LessThan -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalLessThan(v1, v2))))));
                case LessEqualThan -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalLessEqualThan(v1, v2))))));
                case GreaterThan -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalGreaterThan(v1, v2))))));
                case GreaterEqualThan -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalGreaterEqualThan(v1, v2))))));
                case Equal -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalEqual(v1, v2))))));
                case NotEqual -> evalExpr(lhs, scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> cont.accept(evalNotEqual(v1, v2))))));
                case And -> evalAnd(lhs, rhs, scope, cont);
                case Or -> evalOr(lhs, rhs, scope, cont);
            }
        } else if (node instanceof FieldAccess n) {
            evalExpr(n.expr(), scope, wrap(v -> cont.accept(getField(v, n.field()))));
        } else if (node instanceof Subscript n) {
            evalExpr(n.expr(), scope, wrap(v -> evalExpr(n.subscript(), scope, wrap(s -> cont.accept(evalSubscript(v, s))))));
        } else if (node instanceof Call n) {
            evalExpr(n.expr(), scope, wrap(callee -> evalExprList(n.args(), scope, wrap(args -> evalCall(callee, args, cont)))));
        } else {
            throw new ByxScriptRuntimeException("unknown expression type: " + node);
        }
    }

    private static void execStmt(Statement node, Scope scope, Runnable cont) {
        guard(() -> execStmt(node, scope, cont));
        checkThreadInterrupt();

        FunctionStack functionStack = FUNCTION_STACK_HOLDER.get();
        FunctionFrame frame = functionStack.peek();

        if (node instanceof VarDeclare n) {
            evalExpr(n.value(), scope, wrap(v -> {
                scope.declareVar(n.varName(), v);
                cont.run();
            }));
        } else if (node instanceof Assign n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            if (lhs instanceof Var e) {
                // 变量赋值
                evalExpr(rhs, scope, wrap(v -> {
                    scope.setVar(e.varName(), v);
                    cont.run();
                }));
            } else if (lhs instanceof FieldAccess e) {
                // 字段赋值
                evalExpr(e.expr(), scope, wrap(v1 -> evalExpr(rhs, scope, wrap(v2 -> {
                    setField(v1, e.field(), v2);
                    cont.run();
                }))));
            } else if (lhs instanceof Subscript e) {
                // 数组下标赋值
                evalExpr(e.expr(), scope, wrap(v1 -> evalExpr(e.subscript(), scope, wrap(v2 -> evalExpr(rhs, scope, wrap(v3 -> {
                    setSubscript(v1, v2, v3);
                    cont.run();
                }))))));
            }
        } else if (node instanceof If n) {
            execIf(n, 0, scope, cont);
        } else if (node instanceof For n) {
            frame.pushBreakStack(cont);
            Scope newScope = new Scope(scope);
            frame.pushContinueStack(wrap(() -> execStmt(n.update(), newScope, wrap(() -> execFor(n, newScope, wrap(() -> {
                frame.popBreakStack();
                frame.popContinueStack();
                cont.run();
            }))))));
            execStmt(n.init(), newScope, wrap(() -> execFor(n, newScope, wrap(() -> {
                frame.popBreakStack();
                frame.popContinueStack();
                cont.run();
            }))));
        } else if (node instanceof While n) {
            frame.pushBreakStack(cont);
            frame.pushContinueStack(wrap(() -> execWhile(n, scope, wrap(() -> {
                frame.popBreakStack();
                frame.popContinueStack();
                cont.run();
            }))));
            execWhile(n, scope, wrap(() -> {
                frame.popBreakStack();
                frame.popContinueStack();
                cont.run();
            }));
        } else if (node instanceof Block n) {
            execStmtList(n.stmts(), new Scope(scope), cont);
        } else if (node instanceof Break) {
            if (frame.isBreakStackEmpty()) {
                throw new ByxScriptRuntimeException("break statement only allow in loop");
            }
            frame.popBreakStack().run();
        } else if (node instanceof Continue) {
            if (frame.isContinueStackEmpty()) {
                throw new ByxScriptRuntimeException("continue statement only allow in loop");
            }
            frame.peekContinueStack().run();
        } else if (node instanceof Return n) {
            evalExpr(n.retVal(), scope, wrap(v -> {
                if (functionStack.isEmpty()) {
                    throw new ByxScriptRuntimeException("return statement only allow in function");
                }
                functionStack.pop().getReturnCont().accept(v);
            }));
        } else if (node instanceof ExprStatement n) {
            evalExpr(n.expr(), scope, wrap(v -> cont.run()));
        } else if (node instanceof Try n) {
            TRY_STACK_HOLDER.get().push(new TryContext(n.catchVar(), n.catchBody(), cont, scope, frame,
                frame.peekBreakStack(), frame.peekContinueStack()));
            execStmt(n.tryBody(), scope, cont);
        } else if (node instanceof Throw n) {
            evalExpr(n.expr(), scope, wrap(ByxScriptEvaluator::execThrow));
        } else {
            throw new ByxScriptRuntimeException("unknown statement type: " + node);
        }
    }

    // 检测线程中断状态
    private static void checkThreadInterrupt() {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException();
        }
    }

    private static void doEvalExprList(List<Expr> exprs, List<Value> values, int i, Scope scope, Consumer<List<Value>> cont) {
        guard(() -> doEvalExprList(exprs, values, i, scope, cont));
        if (i == exprs.size()) {
            cont.accept(values);
        } else {
            evalExpr(exprs.get(i), scope, wrap(v -> {
                values.add(v);
                doEvalExprList(exprs, values, i + 1, scope, cont);
            }));
        }
    }

    private static void evalExprList(List<Expr> exprs, Scope scope, Consumer<List<Value>> cont) {
        guard(() -> evalExprList(exprs, scope, cont));
        doEvalExprList(exprs, new ArrayList<>(), 0, scope, cont);
    }

    private static void evalObjectLiteral(List<Map.Entry<String, Expr>> entries, Map<String, Value> fields, int i, Scope scope, Consumer<Value> cont) {
        guard(() -> evalObjectLiteral(entries, fields, i, scope, cont));
        if (i == entries.size()) {
            cont.accept(new ObjectValue(fields));
        } else {
            Map.Entry<String, Expr> entry = entries.get(i);
            String key = entry.getKey();
            Expr e = entry.getValue();
            evalExpr(e, scope, wrap(v -> {
                fields.put(key, v);
                evalObjectLiteral(entries, fields, i + 1, scope, cont);
            }));
        }
    }

    private static void execIf(If node, int i, Scope scope, Runnable cont) {
        guard(() -> execIf(node, i, scope, cont));
        if (i == node.cases().size()) {
            execStmt(node.elseBranch(), scope, cont);
        } else {
            Pair<Expr, Statement> p = node.cases().get(i);
            evalExpr(p.first(), scope, wrap(v -> {
                if (getCondition(v)) {
                    execStmt(p.second(), scope, cont);
                } else {
                    execIf(node, i + 1, scope, cont);
                }
            }));
        }
    }

    private static void execFor(For node, Scope scope, Runnable cont) {
        guard(() -> execFor(node, scope, cont));
        evalExpr(node.cond(), scope, wrap(v -> {
            if (getCondition(v)) {
                execStmt(node.body(), scope,
                    wrap(() -> execStmt(node.update(), scope,
                        wrap(() -> execFor(node, scope, cont)))));
            } else {
                cont.run();
            }
        }));
    }

    private static void execWhile(While node, Scope scope, Runnable cont) {
        guard(() -> execWhile(node, scope, cont));
        evalExpr(node.cond(), scope, wrap(v -> {
            if (getCondition(v)) {
                execStmt(node.body(), scope, wrap(() -> execWhile(node, scope, cont)));
            } else {
                cont.run();
            }
        }));
    }

    private static void execStmtList(List<Statement> stmts, Scope scope, Runnable cont) {
        guard(() -> execStmtList(stmts, scope, cont));
        doExecStmtList(stmts, 0, scope, cont);
    }

    private static void doExecStmtList(List<Statement> stmts, int i, Scope scope, Runnable cont) {
        guard(() -> doExecStmtList(stmts, i, scope, cont));
        if (i == stmts.size()) {
            cont.run();
        } else {
            execStmt(stmts.get(i), scope, wrap(() -> doExecStmtList(stmts, i + 1, scope, cont)));
        }
    }

    private static void execThrow(Value valueToThrow) {
        // 找到throw对应的try语句上下文
        ArrayDeque<TryContext> tryStack = TRY_STACK_HOLDER.get();
        if (tryStack.isEmpty()) {
            throw new ByxScriptRuntimeException("uncaught exception from script: " + valueToThrow);
        }
        TryContext tryContext = tryStack.pop();
        Scope newScope = new Scope(tryContext.scope);
        newScope.declareVar(tryContext.catchVar, valueToThrow);

        // 弹出函数帧
        FunctionStack functionStack = FUNCTION_STACK_HOLDER.get();
        functionStack.popUntil(tryContext.frame);
        FunctionFrame frame = functionStack.peek();
        frame.popBreakStackUntil(tryContext.breakCont);
        frame.popContinueStackUntil(tryContext.continueCont);

        // 执行catch分支
        execStmt(tryContext.catchBody, newScope, wrap(tryContext.contAfterTry));
    }

    private static CallableValue makeCallableValue(CallableLiteral node, Scope scope) {
        List<String> params = node.params();
        Statement body = node.body();
        return new CallableValue() {
            @Override
            public void accept(List<Value> args, Consumer<Value> cont) {
                guard(() -> this.accept(args, cont));

                // 传递实参
                Scope newScope = new Scope(scope);
                for (int i = 0; i < params.size(); ++i) {
                    if (i < args.size()) {
                        newScope.declareVar(params.get(i), args.get(i));
                    } else {
                        newScope.declareVar(params.get(i), NullValue.INSTANCE);
                    }
                }

                // 执行函数体
                execStmt(body, newScope, wrap(() -> cont.accept(NullValue.INSTANCE)));
            }
        };
    }

    private static ByxScriptRuntimeException buildUnaryOpUnsupportedException(String op, Value v) {
        return new ByxScriptRuntimeException(String.format("unsupported operator %s on %s", op, v.typeId()));
    }

    private static ByxScriptRuntimeException buildBinaryOpUnsupportedException(String op, Value lhs, Value rhs) {
        return new ByxScriptRuntimeException(String.format("unsupported operator %s between %s and %s",
            op, lhs.typeId(), rhs.typeId()));
    }

    private static Value evalNot(Value value) {
        if (value instanceof BoolValue v) {
            return BoolValue.of(!v.getValue());
        }
        throw buildUnaryOpUnsupportedException("!", value);
    }

    private static Value evalNeg(Value value) {
        if (value instanceof IntegerValue v) {
            return new IntegerValue(-v.value());
        } else if (value instanceof DoubleValue v) {
            return new DoubleValue(-v.value());
        }
        throw buildUnaryOpUnsupportedException("-", value);
    }

    private static Value evalAdd(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new IntegerValue(v1.value() + v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() + v2.value());
            } else if (rhs instanceof StringValue v2) {
                return new StringValue(v1.value() + v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new DoubleValue(v1.value() + v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() + v2.value());
            } else if (rhs instanceof StringValue v2) {
                return new StringValue(v1.value() + v2.value());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return new StringValue(v1.value() + v2.value());
            } else if (rhs instanceof IntegerValue v2) {
                return new StringValue(v1.value() + v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new StringValue(v1.value() + v2.value());
            } else if (rhs instanceof BoolValue v2) {
                return new StringValue(v1.value() + v2.getValue());
            } else if (rhs instanceof NullValue) {
                return new StringValue(v1.value() + "null");
            }
        } else if (lhs instanceof BoolValue v1) {
            if (rhs instanceof StringValue v2) {
                return new StringValue(v1.getValue() + v2.value());
            }
        } else if (lhs instanceof NullValue) {
            if (rhs instanceof StringValue v) {
                return new StringValue("null" + v.value());
            }
        }

        throw buildBinaryOpUnsupportedException("+", lhs, rhs);
    }

    private static Value evalSub(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new IntegerValue(v1.value() - v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() - v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new DoubleValue(v1.value() - v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() - v2.value());
            }
        }

        throw buildBinaryOpUnsupportedException("-", lhs, rhs);
    }

    private static Value evalMul(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new IntegerValue(v1.value() * v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() * v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new DoubleValue(v1.value() * v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() * v2.value());
            }
        }

        throw buildBinaryOpUnsupportedException("*", lhs, rhs);
    }

    private static Value evalDiv(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new IntegerValue(v1.value() / v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() / v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return new DoubleValue(v1.value() / v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return new DoubleValue(v1.value() / v2.value());
            }
        }

        throw buildBinaryOpUnsupportedException("/", lhs, rhs);
    }

    private static Value evalRem(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1 && rhs instanceof IntegerValue v2) {
            return new IntegerValue(v1.value() % v2.value());
        }

        throw buildBinaryOpUnsupportedException("%", lhs, rhs);
    }

    private static Value evalLessThan(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() < v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() < v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() < v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() < v2.value());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return BoolValue.of(v1.value().compareTo(v2.value()) < 0);
            }
        }

        throw buildBinaryOpUnsupportedException("<", lhs, rhs);
    }

    private static Value evalLessEqualThan(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() <= v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() <= v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() <= v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() <= v2.value());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return BoolValue.of(v1.value().compareTo(v2.value()) <= 0);
            }
        }

        throw buildBinaryOpUnsupportedException("<=", lhs, rhs);
    }

    private static Value evalGreaterThan(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() > v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() > v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() > v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() > v2.value());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return BoolValue.of(v1.value().compareTo(v2.value()) > 0);
            }
        }

        throw buildBinaryOpUnsupportedException(">", lhs, rhs);
    }

    private static Value evalGreaterEqualThan(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() >= v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() >= v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() >= v2.value());
            } else if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() >= v2.value());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return BoolValue.of(v1.value().compareTo(v2.value()) >= 0);
            }
        }

        throw buildBinaryOpUnsupportedException(">=", lhs, rhs);
    }

    private static Value evalEqual(Value lhs, Value rhs) {
        if (lhs instanceof IntegerValue v1) {
            if (rhs instanceof IntegerValue v2) {
                return BoolValue.of(v1.value() == v2.value());
            }
        } else if (lhs instanceof DoubleValue v1) {
            if (rhs instanceof DoubleValue v2) {
                return BoolValue.of(v1.value() == v2.value());
            }
        } else if (lhs instanceof BoolValue v1) {
            if (rhs instanceof BoolValue v2) {
                return BoolValue.of(v1.getValue() == v2.getValue());
            }
        } else if (lhs instanceof StringValue v1) {
            if (rhs instanceof StringValue v2) {
                return BoolValue.of(v1.value().compareTo(v2.value()) == 0);
            }
        } else if (lhs instanceof ListValue v1) {
            if (rhs instanceof ListValue v2) {
                return BoolValue.of(v1.getElems().equals(v2.getElems()));
            }
        }

        return BoolValue.of(lhs == rhs);
    }

    private static Value evalNotEqual(Value lhs, Value rhs) {
        Value ret = evalEqual(lhs, rhs);
        if (ret instanceof BoolValue v) {
            return BoolValue.of(!v.getValue());
        }
        return BoolValue.TRUE;
    }

    private static void evalAnd(Expr lhs, Expr rhs, Scope scope, Consumer<Value> cont) {
        guard(() -> evalAnd(lhs, rhs, scope, cont));
        evalExpr(lhs, scope, wrap(v1 -> {
            // 实现短路特性
            if (v1 instanceof BoolValue v && !v.getValue()) {
                cont.accept(BoolValue.FALSE);
                return;
            }

            evalExpr(rhs, scope, wrap(v2 -> {
                if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
                    cont.accept(BoolValue.of(b1.getValue() && b2.getValue()));
                    return;
                }

                throw buildBinaryOpUnsupportedException("&&", v1, v2);
            }));
        }));
    }

    private static void evalOr(Expr lhs, Expr rhs, Scope scope, Consumer<Value> cont) {
        guard(() -> evalOr(lhs, rhs, scope, cont));
        evalExpr(lhs, scope, wrap(v1 -> {
            // 实现短路特性
            if (v1 instanceof BoolValue v && v.getValue()) {
                cont.accept(BoolValue.TRUE);
                return;
            }

            evalExpr(rhs, scope, wrap(v2 -> {
                if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
                    cont.accept(BoolValue.of(b1.getValue() || b2.getValue()));
                    return;
                }

                throw buildBinaryOpUnsupportedException("||", v1, v2);
            }));
        }));
    }

    private static void evalCall(Value v, List<Value> args, Consumer<Value> cont) {
        if (v instanceof CallableValue c) {
            FunctionStack functionStack = FUNCTION_STACK_HOLDER.get();
            functionStack.push(cont);
            try {
                c.accept(args, wrap(r -> {
                    functionStack.pop();
                    cont.accept(r);
                }));
            } catch (BuiltinFunctionException e) {
                execThrow(e.getValue());
            }
        } else {
            throw new ByxScriptRuntimeException(String.format("%s is not callable", v.typeId()));
        }
    }

    private static Value getField(Value value, String field) {
        if (value instanceof ObjectValue v) {
            Map<String, Value> fields = v.getFields();
            if (!fields.containsKey(field)) {
                throw new ByxScriptRuntimeException(String.format("field %s not exist", field));
            }
            return fields.get(field);
        }

        throw new ByxScriptRuntimeException(String.format("unsupported field access: %s", value.typeId()));
    }

    private static Value evalSubscript(Value value, Value sub) {
        if (value instanceof ListValue v) {
            if (sub instanceof IntegerValue s) {
                int index = s.value();
                return v.getElems().get(index);
            } else {
                throw new ByxScriptRuntimeException("subscript must be integer");
            }
        } else if (value instanceof StringValue v) {
            if (sub instanceof IntegerValue s) {
                int index = s.value();
                return new StringValue(String.valueOf(v.value().charAt(index)));
            } else {
                throw new ByxScriptRuntimeException("subscript must be integer");
            }
        }

        throw new ByxScriptRuntimeException(String.format("unsupported subscript: %s", value.typeId()));
    }

    private static void setField(Value value, String field, Value rhs) {
        if (value instanceof ObjectValue v) {
            v.setField(field, rhs);
            return;
        }
        throw new ByxScriptRuntimeException(String.format("unsupported field assign: %s", value.typeId()));
    }

    private static void setSubscript(Value value, Value sub, Value rhs) {
        if (value instanceof ListValue v) {
            if (sub instanceof IntegerValue s) {
                int index = s.value();
                v.getElems().set(index, rhs);
                return;
            } else {
                throw new ByxScriptRuntimeException("subscript must be integer");
            }
        }

        throw new ByxScriptRuntimeException(String.format("unsupported subscript assign: %s", value.typeId()));
    }

    private static boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new ByxScriptRuntimeException("condition of if, while, for statement must be bool value");
    }
}
