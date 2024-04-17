package byx.script.core.interpreter;

import byx.script.core.common.Pair;
import byx.script.core.interpreter.exception.*;
import byx.script.core.interpreter.value.*;
import byx.script.core.parser.ast.Program;
import byx.script.core.parser.ast.expr.*;
import byx.script.core.parser.ast.stmt.*;

import java.util.*;
import java.util.function.Consumer;

public class ByxScriptEvaluator {
    private static final ThreadLocal<FunctionStack> FUNCTION_STACK_HOLDER = new ThreadLocal<>();

    private record TryContext(
        String catchVar,
        Statement catchBody,
        Consumer<Void> contAfterTry,
        Scope scope,
        FunctionFrame frame,
        Consumer<Void> breakCont,
        Consumer<Void> continueCont
    ) {}
    private static final ThreadLocal<ArrayDeque<TryContext>> TRY_STACK_HOLDER = new ThreadLocal<>();

    public static void execute(Program program, Scope scope) {
        FUNCTION_STACK_HOLDER.set(new FunctionStack());
        TRY_STACK_HOLDER.set(new ArrayDeque<>());
        execStmtList(program.stmts(), scope).run(v -> {});
        FUNCTION_STACK_HOLDER.remove();
        TRY_STACK_HOLDER.remove();
    }

    private static Cont<Value> evalExpr(Expr node, Scope scope) {
        checkThreadInterrupt();

        if (node instanceof Literal n) {
            return Cont.value(n.value());
        } else if (node instanceof ListLiteral n) {
            return evalExprList(n.elems(), scope).map(ListValue::new);
        } else if (node instanceof ObjectLiteral n) {
            return evalObjectLiteral(new ArrayList<>(n.fields().entrySet()), new HashMap<>(), 0, scope);
        } else if (node instanceof CallableLiteral n) {
            return Cont.value(makeCallableValue(n, scope));
        } else if (node instanceof Var n) {
            return Cont.value(scope.getVar(n.varName()));
        } else if (node instanceof UnaryExpr n) {
            return evalExpr(n.expr(), scope)
                .map(v -> switch (n.op()) {
                    case Not -> evalNot(v);
                    case Neg -> evalNeg(v);
                });
        } else if (node instanceof BinaryExpr n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            return switch (n.op()) {
                case Add -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalAdd(v1, v2)));
                case Sub -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalSub(v1, v2)));
                case Mul -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalMul(v1, v2)));
                case Div -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalDiv(v1, v2)));
                case Rem -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalRem(v1, v2)));
                case LessThan -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalLessThan(v1, v2)));
                case LessEqualThan -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalLessEqualThan(v1, v2)));
                case GreaterThan -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalGreaterThan(v1, v2)));
                case GreaterEqualThan -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalGreaterEqualThan(v1, v2)));
                case Equal -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalEqual(v1, v2)));
                case NotEqual -> evalExpr(lhs, scope).flatMap(v1 -> evalExpr(rhs, scope).map(v2 -> evalNotEqual(v1, v2)));
                case And -> evalAnd(lhs, rhs, scope);
                case Or -> evalOr(lhs, rhs, scope);
            };
        } else if (node instanceof FieldAccess n) {
            return evalExpr(n.expr(), scope)
                .map(v -> getField(v, n.field()));
        } else if (node instanceof Subscript n) {
            return evalExpr(n.expr(), scope)
                .flatMap(v -> evalExpr(n.subscript(), scope)
                    .map(s -> evalSubscript(v, s)));
        } else if (node instanceof Call n) {
            return evalExpr(n.expr(), scope)
                .flatMap(callee -> evalExprList(n.args(), scope)
                    .flatMap(args -> evalCall(callee, args)));
        }

        throw new ByxScriptRuntimeException("unknown expression type: " + node);
    }

    private static Cont<Void> execStmt(Statement node, Scope scope) {
        checkThreadInterrupt();

        FunctionStack functionStack = FUNCTION_STACK_HOLDER.get();
        FunctionFrame frame = functionStack.peek();

        if (node instanceof VarDeclare n) {
            return evalExpr(n.value(), scope).map(v -> {
                scope.declareVar(n.varName(), v);
                return null;
            });
        } else if (node instanceof Assign n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            if (lhs instanceof Var e) {
                // 变量赋值
                return evalExpr(rhs, scope).map(v -> {
                    scope.setVar(e.varName(), v);
                    return null;
                });
            } else if (lhs instanceof FieldAccess e) {
                // 字段赋值
                return evalExpr(e.expr(), scope).flatMap(v1 ->
                    evalExpr(rhs, scope).map(v2 -> {
                        setField(v1, e.field(), v2);
                        return null;
                    }));
            } else if (lhs instanceof Subscript e) {
                // 数组下标赋值
                return evalExpr(e.expr(), scope).flatMap(v1 ->
                    evalExpr(e.subscript(), scope).flatMap(v2 ->
                        evalExpr(rhs, scope).map(v3 -> {
                            setSubscript(v1, v2, v3);
                            return null;
                        })));
            }
        } else if (node instanceof If n) {
            return execIf(n, 0, scope);
        } else if (node instanceof For n) {
            return cont -> {
                frame.pushBreakStack(cont);
                Scope newScope = new Scope(scope);
                frame.pushContinueStack(vv -> execStmt(n.update(), newScope)
                    .flatMap(v -> execFor(n, newScope))
                    .map(v -> {
                        frame.popBreakStack();
                        frame.popContinueStack();
                        return (Void) null;
                    }).run(cont));
                execStmt(n.init(), newScope)
                    .flatMap(v -> execFor(n, newScope))
                    .map(v -> {
                        frame.popBreakStack();
                        frame.popContinueStack();
                        return (Void) null;
                    }).run(cont);
            };
        } else if (node instanceof While n) {
            return cont -> {
                frame.pushBreakStack(cont);
                frame.pushContinueStack(vv -> execWhile(n, scope).map(v -> {
                    frame.popBreakStack();
                    frame.popContinueStack();
                    return (Void) null;
                }).run(cont));
                execWhile(n, scope).map(v -> {
                    frame.popBreakStack();
                    frame.popContinueStack();
                    return (Void) null;
                }).run(cont);
            };
        } else if (node instanceof Block n) {
            return execStmtList(n.stmts(), new Scope(scope));
        } else if (node instanceof Break) {
            return cont -> {
                if (frame.isBreakStackEmpty()) {
                    throw new ByxScriptRuntimeException("break statement only allow in loop");
                }
                frame.popBreakStack().accept(null);
            };
        } else if (node instanceof Continue) {
            return cont -> {
                if (frame.isContinueStackEmpty()) {
                    throw new ByxScriptRuntimeException("continue statement only allow in loop");
                }
                frame.peekContinueStack().accept(null);
            };
        } else if (node instanceof Return n) {
            return evalExpr(n.retVal(), scope).flatMap(v -> cont -> {
                if (functionStack.isEmpty()) {
                    throw new ByxScriptRuntimeException("return statement only allow in function");
                }
                functionStack.pop().getReturnCont().accept(v);
            });
        } else if (node instanceof ExprStatement n) {
            return evalExpr(n.expr(), scope).map(v -> null);
        } else if (node instanceof Try n) {
            return cont -> {
                TRY_STACK_HOLDER.get().push(new TryContext(n.catchVar(), n.catchBody(), cont, scope, frame,
                    frame.peekBreakStack(), frame.peekContinueStack()));
                execStmt(n.tryBody(), scope).run(cont);
            };
        } else if (node instanceof Throw n) {
            return evalExpr(n.expr(), scope).flatMap(ByxScriptEvaluator::execThrow);
        }

        throw new ByxScriptRuntimeException("unknown statement type: " + node);
    }

    // 检测线程中断状态
    private static void checkThreadInterrupt() {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException();
        }
    }

    private static Cont<List<Value>> doEvalExprList(List<Expr> exprs, List<Value> values, int i, Scope scope) {
        if (i == exprs.size()) {
            return Cont.value(values);
        } else {
            return evalExpr(exprs.get(i), scope).flatMap(v -> {
                values.add(v);
                return doEvalExprList(exprs, values, i + 1, scope);
            });
        }
    }

    private static Cont<List<Value>> evalExprList(List<Expr> exprs, Scope scope) {
        return doEvalExprList(exprs, new ArrayList<>(), 0, scope);
    }

    private static Cont<Value> evalObjectLiteral(List<Map.Entry<String, Expr>> entries, Map<String, Value> fields, int i, Scope scope) {
        if (i == entries.size()) {
            return Cont.value(new ObjectValue(fields));
        } else {
            Map.Entry<String, Expr> entry = entries.get(i);
            String key = entry.getKey();
            Expr e = entry.getValue();
            return evalExpr(e, scope).flatMap(v -> {
                fields.put(key, v);
                return evalObjectLiteral(entries, fields, i + 1, scope);
            });
        }
    }

    private static Cont<Void> execIf(If node, int i, Scope scope) {
        if (i == node.cases().size()) {
            return execStmt(node.elseBranch(), scope);
        } else {
            Pair<Expr, Statement> p = node.cases().get(i);
            return evalExpr(p.first(), scope).flatMap(v -> {
                if (getCondition(v)) {
                    return execStmt(p.second(), scope);
                } else {
                    return execIf(node, i + 1, scope);
                }
            });
        }
    }

    private static Cont<Void> execFor(For node, Scope scope) {
        return evalExpr(node.cond(), scope).flatMap(v -> {
            if (getCondition(v)) {
                return execStmt(node.body(), scope)
                    .flatMap(x -> execStmt(node.update(), scope))
                    .flatMap(x -> execFor(node, scope));
            } else {
                return Cont.value(null);
            }
        });
    }

    private static Cont<Void> execWhile(While node, Scope scope) {
        return evalExpr(node.cond(), scope).flatMap(v -> {
            if (getCondition(v)) {
                return execStmt(node.body(), scope)
                    .flatMap(x -> execWhile(node, scope));
            } else {
                return Cont.value(null);
            }
        });
    }

    private static Cont<Void> execStmtList(List<Statement> stmts, Scope scope) {
        return doExecStmtList(stmts, 0, scope);
    }

    private static Cont<Void> doExecStmtList(List<Statement> stmts, int i, Scope scope) {
        if (i == stmts.size()) {
            return Cont.value(null);
        } else {
            return execStmt(stmts.get(i), scope)
                .flatMap(x -> doExecStmtList(stmts, i + 1, scope));
        }
    }

    private static Cont<Void> execThrow(Value valueToThrow) {
        return cont -> {
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
            execStmt(tryContext.catchBody, newScope).run(tryContext.contAfterTry);
        };
    }

    private static CallableValue makeCallableValue(CallableLiteral node, Scope scope) {
        List<String> params = node.params();
        Statement body = node.body();
        return args -> {
            // 传递实参
            Scope newScope = new Scope(scope);
            for (int i = 0; i < params.size(); ++i) {
                if (i < args.size()) {
                    newScope.declareVar(params.get(i), args.get(i));
                } else {
                    newScope.declareVar(params.get(i), NullValue.INSTANCE);
                }
            }

            return execStmt(body, newScope).map(v -> NullValue.INSTANCE);
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

    private static Cont<Value> evalAnd(Expr lhs, Expr rhs, Scope scope) {
        return evalExpr(lhs, scope).flatMap(v1 -> {
            // 实现短路特性
            if (v1 instanceof BoolValue v && !v.getValue()) {
                return Cont.value(BoolValue.FALSE);
            }

            return evalExpr(rhs, scope).map(v2 -> {
                if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
                    return BoolValue.of(b1.getValue() && b2.getValue());
                }

                throw buildBinaryOpUnsupportedException("&&", v1, v2);
            });
        });
    }

    private static Cont<Value> evalOr(Expr lhs, Expr rhs, Scope scope) {
        return evalExpr(lhs, scope).flatMap(v1 -> {
            // 实现短路特性
            if (v1 instanceof BoolValue v && v.getValue()) {
                return Cont.value(BoolValue.TRUE);
            }

            return evalExpr(rhs, scope).map(v2 -> {
                if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
                    return BoolValue.of(b1.getValue() || b2.getValue());
                }

                throw buildBinaryOpUnsupportedException("||", v1, v2);
            });
        });
    }

    private static Cont<Value> evalCall(Value v, List<Value> args) {
        if (v instanceof CallableValue c) {
            return cont -> {
                FunctionStack functionStack = FUNCTION_STACK_HOLDER.get();
                functionStack.push(cont);
                try {
                    c.apply(args).map(vv -> {
                        functionStack.pop();
                        return vv;
                    }).run(cont);
                } catch (BuiltinFunctionException e) {
                    execThrow(e.getValue()).run(x -> {});
                }
            };
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
