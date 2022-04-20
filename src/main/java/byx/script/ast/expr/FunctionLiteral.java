package byx.script.ast.expr;

import byx.script.ast.stmt.Statement;
import byx.script.runtime.control.ReturnException;
import byx.script.runtime.Scope;
import byx.script.runtime.value.UndefinedValue;
import byx.script.runtime.value.Value;

import java.util.List;

public class FunctionLiteral implements Expr {
    private final List<String> params;
    private final Statement body;

    public FunctionLiteral(List<String> params, Statement body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(args -> {
            // 传递实参
            Scope newScope = new Scope(scope);
            for (int i = 0; i < params.size(); ++i) {
                if (i < args.size()) {
                    newScope.declareVar(params.get(i), args.get(i));
                } else {
                    newScope.declareVar(params.get(i), UndefinedValue.INSTANCE);
                }
            }

            // 执行函数体
            try {
                body.execute(newScope);
            } catch (ReturnException e) {
                return e.getRetVal();
            }
            return UndefinedValue.INSTANCE;
        });
    }
}
