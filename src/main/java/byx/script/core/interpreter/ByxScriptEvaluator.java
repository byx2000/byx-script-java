package byx.script.core.interpreter;

import byx.script.core.common.Pair;
import byx.script.core.interpreter.exception.*;
import byx.script.core.interpreter.value.*;
import byx.script.core.parser.ast.Program;
import byx.script.core.parser.ast.expr.*;
import byx.script.core.parser.ast.stmt.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ByxScript求值器
 */
public class ByxScriptEvaluator {
    public static void execute(Program program, Scope scope) {
        program.stmts().forEach(s -> executeStatement(s, scope));
    }

    private static Value evalExpr(Expr node, Scope scope) {
        checkThreadInterrupt();

        if (node instanceof Literal n) {
            return n.value();
        } else if (node instanceof ListLiteral n) {
            List<Value> elems = n.elems().stream()
                .map(e -> evalExpr(e, scope))
                .collect(Collectors.toList());
            return new ListValue(elems);
        } else if (node instanceof ObjectLiteral n) {
            Map<String, Value> fields = n.fields().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> evalExpr(e.getValue(), scope)
                ));
            return new ObjectValue(fields);
        } else if (node instanceof CallableLiteral n) {
            return makeCallableValue(n, scope);
        } else if (node instanceof Var n) {
            return scope.getVar(n.varName());
        } else if (node instanceof UnaryExpr n) {
            Value v = evalExpr(n.expr(), scope);
            return switch (n.op()) {
                case Not -> evalNot(v);
                case Neg -> evalNeg(v);
            };
        } else if (node instanceof BinaryExpr n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            return switch (n.op()) {
                case Add -> evalAdd(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case Sub -> evalSub(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case Mul -> evalMul(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case Div -> evalDiv(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case Rem -> evalRem(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case LessThan -> evalLessThan(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case LessEqualThan -> evalLessEqualThan(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case GreaterThan -> evalGreaterThan(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case GreaterEqualThan -> evalGreaterEqualThan(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case Equal -> evalEqual(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case NotEqual -> evalNotEqual(evalExpr(lhs, scope), evalExpr(rhs, scope));
                case And -> evalAnd(scope, lhs, rhs);
                case Or -> evalOr(scope, lhs, rhs);
            };
        } else if (node instanceof FieldAccess n) {
            return getField(evalExpr(n.expr(), scope), n.field());
        } else if (node instanceof Subscript n) {
            return evalSubscript(evalExpr(n.expr(), scope), evalExpr(n.subscript(), scope));
        } else if (node instanceof Call n) {
            Value callee = evalExpr(n.expr(), scope);
            List<Value> args = n.args().stream().map(p -> evalExpr(p, scope)).toList();
            return evalCall(callee, args);
        }

        throw new ByxScriptRuntimeException("unknown expression type: " + node);
    }

    private static void executeStatement(Statement node, Scope scope) {
        checkThreadInterrupt();

        if (node instanceof VarDeclare n) {
            scope.declareVar(n.varName(), evalExpr(n.value(), scope));
        } else if (node instanceof Assign n) {
            Expr lhs = n.lhs();
            Expr rhs = n.rhs();
            if (lhs instanceof Var e) {
                // 变量赋值
                scope.setVar(e.varName(), evalExpr(rhs, scope));
            } else if (lhs instanceof FieldAccess e) {
                // 字段赋值
                setField(evalExpr(e.expr(), scope), e.field(), evalExpr(rhs, scope));
            } else if (lhs instanceof Subscript e) {
                // 数组下标赋值
                setSubscript(evalExpr(e.expr(), scope), evalExpr(e.subscript(), scope), evalExpr(rhs, scope));
            }
        } else if (node instanceof If n) {
            List<Pair<Expr, Statement>> cases = n.cases();
            for (Pair<Expr, Statement> p : cases) {
                if (getCondition(evalExpr(p.first(), scope))) {
                    executeStatement(p.second(), scope);
                    return;
                }
            }
            executeStatement(n.elseBranch(), scope);
        } else if (node instanceof For n) {
            Statement init = n.init();
            Expr cond = n.cond();
            Statement update = n.update();
            Statement body = n.body();
            Scope newScope = new Scope(scope);
            for (executeStatement(init, newScope); getCondition(evalExpr(cond, newScope)); executeStatement(update, newScope)) {
                try {
                    executeStatement(body, newScope);
                } catch (BreakException e) {
                    break;
                } catch (ContinueException ignored) {}
            }
        } else if (node instanceof While n) {
            Expr cond = n.cond();
            Statement body = n.body();
            while (getCondition(evalExpr(cond, scope))) {
                try {
                    executeStatement(body, scope);
                } catch (BreakException e) {
                    break;
                } catch (ContinueException ignored) {}
            }
        } else if (node instanceof Block n) {
            Scope newScope = new Scope(scope);
            n.stmts().forEach(s -> executeStatement(s, newScope));
        } else if (node instanceof Break) {
            throw BreakException.INSTANCE;
        } else if (node instanceof Continue) {
            throw ContinueException.INSTANCE;
        } else if (node instanceof Return n) {
            throw new ReturnException(evalExpr(n.retVal(), scope));
        } else if (node instanceof ExprStatement n) {
            evalExpr(n.expr(), scope);
        } else if (node instanceof Try n) {
            try {
                executeStatement(n.tryBody(), scope);
            } catch (ThrowException e) {
                Scope newScope = new Scope(scope);
                newScope.declareVar(n.catchVar(), e.getValue());
                executeStatement(n.catchBody(), newScope);
            }
        } else if (node instanceof Throw n) {
            throw new ThrowException(evalExpr(n.expr(), scope));
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

            // 执行函数体
            try {
                executeStatement(body, newScope);
            } catch (ReturnException e) {
                return e.getRetVal();
            }
            return NullValue.INSTANCE;
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

    private static Value evalAnd(Scope scope, Expr lhsExpr, Expr rhsExpr) {
        Value lhs = evalExpr(lhsExpr, scope);

        // 实现短路特性
        if (lhs instanceof BoolValue v && !v.getValue()) {
            return BoolValue.FALSE;
        }

        Value rhs = evalExpr(rhsExpr, scope);
        if (lhs instanceof BoolValue v1 && rhs instanceof BoolValue v2) {
            return BoolValue.of(v1.getValue() && v2.getValue());
        }

        throw buildBinaryOpUnsupportedException("&&", lhs, rhs);
    }

    private static Value evalOr(Scope scope, Expr lhsExpr, Expr rhsExpr) {
        Value lhs = evalExpr(lhsExpr, scope);

        // 实现短路特性
        if (lhs instanceof BoolValue v && v.getValue()) {
            return BoolValue.TRUE;
        }

        Value rhs = evalExpr(rhsExpr, scope);
        if (lhs instanceof BoolValue v1 && rhs instanceof BoolValue v2) {
            return BoolValue.of(v1.getValue() || v2.getValue());
        }

        throw buildBinaryOpUnsupportedException("||", lhs, rhs);
    }

    private static Value evalCall(Value v, List<Value> args) {
        if (v instanceof CallableValue c) {
            return c.apply(args);
        }

        throw new ByxScriptRuntimeException(String.format("%s is not callable", v.typeId()));
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
