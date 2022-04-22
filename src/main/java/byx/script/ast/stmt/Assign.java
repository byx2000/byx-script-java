package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.ast.expr.FieldAccess;
import byx.script.ast.expr.Subscript;
import byx.script.ast.expr.Var;
import byx.script.runtime.Scope;

/**
 * 赋值语句
 */
public class Assign implements Statement {
    private final Expr lhs, rhs;

    public Assign(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void execute(Scope scope) {
        if (lhs instanceof Var e) {
            // 变量赋值
            scope.setVar(e.getVarName(), rhs.eval(scope));
        } else if (lhs instanceof FieldAccess e) {
            // 字段赋值
            e.getExpr().eval(scope).fieldAssign(e.getField(), rhs.eval(scope));
        } else if (lhs instanceof Subscript e) {
            // 数组下标赋值
            e.getExpr().eval(scope).subscriptAssign(e.getSubscript().eval(scope), rhs.eval(scope));
        }
    }
}
