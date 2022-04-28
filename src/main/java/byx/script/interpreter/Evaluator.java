package byx.script.interpreter;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.Program;
import byx.script.common.Pair;
import byx.script.parser.ast.expr.*;
import byx.script.parser.ast.stmt.*;
import byx.script.interpreter.value.BoolValue;
import byx.script.interpreter.value.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽象语法树求值器
 */
public class Evaluator implements ASTVisitor<Value, Scope> {
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
    public Value visit(Program node, Scope scope) {
        node.getStmts().forEach(s -> s.visit(this, scope));
        return null;
    }

    @Override
    public Value visit(VarDeclare node, Scope scope) {
        scope.declareVar(node.getVarName(), node.getValue().visit(this, scope));
        return null;
    }

    @Override
    public Value visit(Assign node, Scope scope) {
        Expr lhs = node.getLhs();
        Expr rhs = node.getRhs();
        if (lhs instanceof Var e) {
            // 变量赋值
            scope.setVar(e.getVarName(), rhs.visit(this, scope));
        } else if (lhs instanceof FieldAccess e) {
            // 字段赋值
            e.getExpr().visit(this, scope).setField(e.getField(), rhs.visit(this, scope));
        } else if (lhs instanceof Subscript e) {
            // 数组下标赋值
            e.getExpr().visit(this, scope).setSubscript(e.getSubscript().visit(this, scope), rhs.visit(this, scope));
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
    public Value visit(If node, Scope scope) {
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
    public Value visit(For node, Scope scope) {
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
    public Value visit(While node, Scope scope) {
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
    public Value visit(Block node, Scope scope) {
        List<Statement> stmts = node.getStmts();
        scope = new Scope(scope);
        for (Statement s : stmts) {
            s.visit(this, scope);
        }
        return null;
    }

    @Override
    public Value visit(Break node, Scope scope) {
        throw BREAK_EXCEPTION;
    }

    @Override
    public Value visit(Continue node, Scope scope) {
        throw CONTINUE_EXCEPTION;
    }

    @Override
    public Value visit(Return node, Scope scope) {
        throw new ReturnException(node.getRetVal().visit(this, scope));
    }

    @Override
    public Value visit(ExprStatement node, Scope scope) {
        node.getExpr().visit(this, scope);
        return null;
    }

    @Override
    public Value visit(Try node, Scope scope) {
        try {
            node.getTryBranch().visit(this, scope);
        } catch (ThrowException e) {
            Scope newScope = new Scope(scope);
            newScope.declareVar(node.getCatchVar(), e.getValue());
            node.getCatchBranch().visit(this, newScope);
        } finally {
            node.getFinallyBranch().visit(this, scope);
        }
        return null;
    }

    @Override
    public Value visit(Throw node, Scope scope) {
        throw new ThrowException(node.getExpr().visit(this, scope));
    }

    @Override
    public Value visit(Literal node, Scope scope) {
        return node.getValue();
    }

    @Override
    public Value visit(ListLiteral node, Scope scope) {
        return Value.of(node.getElems().stream().map(e -> e.visit(this, scope)).collect(Collectors.toList()));
    }

    @Override
    public Value visit(ObjectLiteral node, Scope scope) {
        return Value.of(node.getFields().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().visit(this, scope))));
    }

    @Override
    public Value visit(CallableLiteral node, Scope scope) {
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
    public Value visit(Var node, Scope scope) {
        return scope.getVar(node.getVarName());
    }

    @Override
    public Value visit(UnaryExpr node, Scope scope) {
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
    public Value visit(BinaryExpr node, Scope scope) {
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
    public Value visit(FieldAccess node, Scope scope) {
        return node.getExpr().visit(this, scope).getField(node.getField());
    }

    @Override
    public Value visit(Subscript node, Scope scope) {
        return node.getExpr().visit(this, scope).subscript(node.getSubscript().visit(this, scope));
    }

    @Override
    public Value visit(Call node, Scope scope) {
        return node.getExpr().visit(this, scope).call(node.getArgs().stream()
                .map(p -> p.visit(this, scope))
                .collect(Collectors.toList()));
    }
}
