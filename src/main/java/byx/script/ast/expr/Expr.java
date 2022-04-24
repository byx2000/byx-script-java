package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 表达式节点基类
 */
public interface Expr {
    /**
     * 对表达式求值
     * @param scope 当前作用域
     * @return 求值结果
     */
    Value eval(Scope scope);
}
