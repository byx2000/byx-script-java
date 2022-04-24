package byx.script.ast;

import byx.script.ast.expr.*;
import byx.script.ast.stmt.*;
import byx.script.parserc.Pair;
import byx.script.runtime.Scope;
import byx.script.runtime.control.BreakException;
import byx.script.runtime.control.ContinueException;
import byx.script.runtime.control.ReturnException;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.value.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对抽象语法树求值
 */
public class EvaluatorVisitor implements ASTVisitor<Value, Scope> {
    private static final BreakException BREAK_EXCEPTION = new BreakException();
    private static final ContinueException CONTINUE_EXCEPTION = new ContinueException();

    /**
     * 对Program节点求值
     * @param program Program节点
     * @param scope 作用域
     */
    public void eval(Program program, Scope scope) {
        program.visit(this, scope);
    }

    @Override
    public Value visit(Scope scope, Program node) {
        node.getStmts().forEach(s -> s.visit(this, scope));
        return null;
    }

    @Override
    public Value visit(Scope scope, VarDeclaration node) {
        scope.declareVar(node.getVarName(), node.getValue().visit(this, scope));
        return null;
    }

    @Override
    public Value visit(Scope scope, Assign node) {
        Expr lhs = node.getLhs();
        Expr rhs = node.getRhs();
        if (lhs instanceof Var e) {
            // 变量赋值
            scope.setVar(e.getVarName(), rhs.visit(this, scope));
        } else if (lhs instanceof FieldAccess e) {
            // 字段赋值
            e.getExpr().visit(this, scope).fieldAssign(e.getField(), rhs.visit(this, scope));
        } else if (lhs instanceof Subscript e) {
            // 数组下标赋值
            e.getExpr().visit(this, scope).subscriptAssign(e.getSubscript().visit(this, scope), rhs.visit(this, scope));
        }
        return null;
    }

    private boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new InterpretException("condition of if, while, for statement must be bool value");
    }

    @Override
    public Value visit(Scope scope, If node) {
        List<Pair<Expr, Statement>> cases = node.getCases();
        Statement elseBranch = node.getElseBranch();
        for (Pair<Expr, Statement> p : cases) {
            if (getCondition(p.getFirst().visit(this, scope))) {
                p.getSecond().visit(this, scope);
                return null;
            }
        }
        elseBranch.visit(this, scope);
        return null;
    }

    @Override
    public Value visit(Scope scope, For node) {
        Statement init = node.getInit();
        Expr cond = node.getCond();
        Statement update = node.getUpdate();
        Statement body = node.getBody();
        scope = new Scope(scope);
        for (init.visit(this, scope); getCondition(cond.visit(this, scope)); update.visit(this, scope)) {
            try {
                body.visit(this, scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) {}
        }
        return null;
    }

    @Override
    public Value visit(Scope scope, While node) {
        Expr cond = node.getCond();
        Statement body = node.getBody();
        while (getCondition(cond.visit(this, scope))) {
            try {
                body.visit(this, scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) {}
        }
        return null;
    }

    @Override
    public Value visit(Scope scope, Block node) {
        List<Statement> stmts = node.getStmts();
        scope = new Scope(scope);
        for (Statement s : stmts) {
            s.visit(this, scope);
        }
        return null;
    }

    @Override
    public Value visit(Scope scope, Break node) {
        throw BREAK_EXCEPTION;
    }

    @Override
    public Value visit(Scope scope, Continue node) {
        throw CONTINUE_EXCEPTION;
    }

    @Override
    public Value visit(Scope scope, Return node) {
        Expr retVal = node.getRetVal();
        if (retVal != null) {
            throw new ReturnException(retVal.visit(this, scope));
        }
        throw new ReturnException(Value.undefined());
    }

    @Override
    public Value visit(Scope scope, ExprStatement node) {
        node.getExpr().visit(this, scope);
        return null;
    }

    @Override
    public Value visit(Scope scope, Literal node) {
        return node.getValue();
    }

    @Override
    public Value visit(Scope scope, ListLiteral node) {
        return Value.of(node.getElems().stream().map(e -> e.visit(this, scope)).collect(Collectors.toList()));
    }

    @Override
    public Value visit(Scope scope, ObjectLiteral node) {
        return Value.of(node.getFields().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().visit(this, scope))));
    }

    @Override
    public Value visit(Scope scope, FunctionLiteral node) {
        List<String> params = node.getParams();
        Statement body = node.getBody();
        return Value.of(args -> {
            // 传递实参
            Scope newScope = new Scope(scope);
            for (int i = 0; i < params.size(); ++i) {
                if (i < args.size()) {
                    newScope.declareVar(params.get(i), args.get(i));
                } else {
                    newScope.declareVar(params.get(i), Value.undefined());
                }
            }

            // 执行函数体
            try {
                body.visit(this, newScope);
            } catch (ReturnException e) {
                return e.getRetVal();
            }
            return Value.undefined();
        });
    }

    @Override
    public Value visit(Scope scope, Var node) {
        return scope.getVar(node.getVarName());
    }

    @Override
    public Value visit(Scope scope, UnaryExpr node) {
        Expr e = node.getExpr();
        return switch (node.getOp()) {
            case Not -> e.visit(this, scope).not();
            case Neg -> e.visit(this, scope).neg();
        };
    }

    private Value evalAnd(Scope scope, Expr lhs, Expr rhs) {
        Value v1 = lhs.visit(this, scope);
        // 实现短路特性
        if (v1 instanceof BoolValue && !((BoolValue) v1).getValue()) {
            return Value.of(false);
        }
        return v1.and(rhs.visit(this, scope));
    }

    private Value evalOr(Scope scope, Expr lhs, Expr rhs) {
        Value v2 = lhs.visit(this, scope);
        // 实现短路特性
        if (v2 instanceof BoolValue && ((BoolValue) v2).getValue()) {
            return Value.of(true);
        }
        return v2.or(rhs.visit(this, scope));
    }

    @Override
    public Value visit(Scope scope, BinaryExpr node) {
        Expr lhs = node.getLhs();
        Expr rhs = node.getRhs();
        return switch (node.getOp()) {
            case Add -> lhs.visit(this, scope).add(rhs.visit(this, scope));
            case Sub -> lhs.visit(this, scope).sub(rhs.visit(this, scope));
            case Mul -> lhs.visit(this, scope).mul(rhs.visit(this, scope));
            case Div -> lhs.visit(this, scope).div(rhs.visit(this, scope));
            case Rem -> lhs.visit(this, scope).rem(rhs.visit(this, scope));
            case LessThan -> lhs.visit(this, scope).lessThan(rhs.visit(this, scope));
            case LessEqualThan -> lhs.visit(this, scope).lessEqualThan(rhs.visit(this, scope));
            case GreaterThan -> lhs.visit(this, scope).greaterThan(rhs.visit(this, scope));
            case GreaterEqualThan -> lhs.visit(this, scope).greaterEqualThan(rhs.visit(this, scope));
            case Equal -> lhs.visit(this, scope).equal(rhs.visit(this, scope));
            case NotEqual -> lhs.visit(this, scope).notEqual(rhs.visit(this, scope));
            case And -> evalAnd(scope, lhs, rhs);
            case Or -> evalOr(scope, lhs, rhs);
        };
    }

    @Override
    public Value visit(Scope scope, FieldAccess node) {
        return node.getExpr().visit(this, scope).getField(node.getField());
    }

    @Override
    public Value visit(Scope scope, Subscript node) {
        return node.getExpr().visit(this, scope).subscript(node.getSubscript().visit(this, scope));
    }

    @Override
    public Value visit(Scope scope, Call node) {
        return node.getExpr().visit(this, scope).call(node.getArgs().stream()
                .map(p -> p.visit(this, scope))
                .collect(Collectors.toList()));
    }
}
