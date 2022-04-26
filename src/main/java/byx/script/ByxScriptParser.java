package byx.script;

import byx.script.ast.*;
import byx.script.ast.expr.*;
import byx.script.ast.stmt.*;
import byx.script.parserc.Pair;
import byx.script.parserc.Parser;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.Value;

import java.util.*;
import java.util.stream.Collectors;

import static byx.script.parserc.Parsers.*;

/**
 * ByxParser解析器
 * 将ByxScript脚本解析成抽象语法树
 */
public class ByxScriptParser {
    // 词法元素

    // 空白字符
    private static final Parser<?> ws = chs(' ', '\t', '\r', '\n').map(Objects::toString);

    // 行注释
    private static final Parser<?> lineComment = string("//").skip(not('\n').many()).skip(ch('\n'));

    // 块注释
    private static final Parser<?> blockComment = string("/*").and(any().manyUntil(string("*/"))).and(string("*/"));

    // 可忽略元素
    private static final Parser<?> ignorable = oneOf(ws, lineComment, blockComment).many();

    // 字母
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));

    // 数字
    private static final Parser<Character> digit = range('0', '9');

    // 数字串
    private static final Parser<String> digits = digit.many1().map(ByxScriptParser::join);

    // 下划线
    private static final Parser<Character> underline = ch('_');

    // 整数
    private static final Parser<String> integer = digits.surroundBy(ignorable);

    // 浮点数
    private static final Parser<String> decimal = seq(digits, ch('.'), digits)
            .map(rs -> "" + rs.get(0) + rs.get(1) + rs.get(2))
            .surroundBy(ignorable);

    // 字符串
    private static final Parser<String> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\'')).map(ByxScriptParser::join).surroundBy(ignorable);

    // 布尔值
    private static final Parser<String> bool = strings("true", "false").surroundBy(ignorable);

    // 符号
    private static final Parser<String> assign = string("=").surroundBy(ignorable);
    private static final Parser<String> semi = string(";").surroundBy(ignorable);
    private static final Parser<String> comma = string(",").surroundBy(ignorable);
    private static final Parser<String> colon = string(":").surroundBy(ignorable);
    private static final Parser<String> dot = string(".").surroundBy(ignorable);
    private static final Parser<String> lp = string("(").surroundBy(ignorable);
    private static final Parser<String> rp = string(")").surroundBy(ignorable);
    private static final Parser<String> lb = string("{").surroundBy(ignorable);
    private static final Parser<String> rb = string("}").surroundBy(ignorable);
    private static final Parser<String> ls = string("[").surroundBy(ignorable);
    private static final Parser<String> rs = string("]").surroundBy(ignorable);
    private static final Parser<String> add = string("+").surroundBy(ignorable);
    private static final Parser<String> sub = string("-").surroundBy(ignorable);
    private static final Parser<String> mul = string("*").surroundBy(ignorable);
    private static final Parser<String> div = string("/").surroundBy(ignorable);
    private static final Parser<String> rem = string("%").surroundBy(ignorable);
    private static final Parser<String> gt = string(">").surroundBy(ignorable);
    private static final Parser<String> get = string(">=").surroundBy(ignorable);
    private static final Parser<String> lt = string("<").surroundBy(ignorable);
    private static final Parser<String> let = string("<=").surroundBy(ignorable);
    private static final Parser<String> equ = string("==").surroundBy(ignorable);
    private static final Parser<String> neq = string("!=").surroundBy(ignorable);
    private static final Parser<String> and = string("&&").surroundBy(ignorable);
    private static final Parser<String> or = string("||").surroundBy(ignorable);
    private static final Parser<String> not = string("!").surroundBy(ignorable);
    private static final Parser<String> arrow = string("=>").surroundBy(ignorable);
    private static final Parser<String> inc = string("++").surroundBy(ignorable);
    private static final Parser<String> dec = string("--").surroundBy(ignorable);
    private static final Parser<String> addAssign = string("+=").surroundBy(ignorable);
    private static final Parser<String> subAssign = string("-=").surroundBy(ignorable);
    private static final Parser<String> mulAssign = string("*=").surroundBy(ignorable);
    private static final Parser<String> divAssign = string("/=").surroundBy(ignorable);
    private static final Parser<String> assignOp = oneOf(assign, addAssign, subAssign, mulAssign, divAssign);

    // 关键字
    private static final Parser<String> import_ = string("import").surroundBy(ignorable);
    private static final Parser<String> var_ = string("var").surroundBy(ignorable);
    private static final Parser<String> if_ = string("if").surroundBy(ignorable);
    private static final Parser<String> else_ = string("else").surroundBy(ignorable);
    private static final Parser<String> for_ = string("for").surroundBy(ignorable);
    private static final Parser<String> while_ = string("while").surroundBy(ignorable);
    private static final Parser<String> break_ = string("break").surroundBy(ignorable);
    private static final Parser<String> continue_ = string("continue").surroundBy(ignorable);
    private static final Parser<String> return_ = string("return").surroundBy(ignorable);
    private static final Parser<String> function_ = string("function").surroundBy(ignorable);
    private static final Parser<String> undefined_ = string("undefined").surroundBy(ignorable);

    private static final Set<String> kw = Set.of("import", "var", "if", "else", "for", "while", "break", "continue", "return", "function", "undefined");

    // 标识符
    private static final Parser<String> identifier = oneOf(alpha, underline)
            .and(oneOf(digit, alpha, underline).many())
            .map(p -> p.getFirst() + join(p.getSecond()))
            .then(s -> kw.contains(s) ? fail("cannot use keyword as identifier") : success(s))
            .surroundBy(ignorable);

    // 前向引用
    private static final Parser<Statement> lazyStmt = lazy(ByxScriptParser::getStmt);
    private static final Parser<List<Statement>> stmts = lazyStmt.skip(semi.optional()).many();
    private static final Parser<Expr> lazyPrimaryExpr = lazy(ByxScriptParser::getPrimaryExpr);
    private static final Parser<Expr> lazyExpr = lazy(ByxScriptParser::getExpr);

    // 表达式

    // 整数字面量
    private static final Parser<Expr> integerLiteral = integer.map(s -> new Literal(Value.of(Integer.parseInt(s))));

    // 浮点数字面量
    private static final Parser<Expr> doubleLiteral = decimal.map(s -> new Literal(Value.of(Double.parseDouble(s))));

    // 字符串字面量
    private static final Parser<Expr> stringLiteral = string.map(s -> new Literal(Value.of(s)));

    // 布尔值字面量
    private static final Parser<Expr> boolLiteral = bool.map(s -> new Literal(Value.of(Boolean.parseBoolean(s))));

    // undefined字面量
    private static final Parser<Expr> undefinedLiteral = undefined_.map(() -> new Literal(Value.undefined()));

    private static final Parser<List<String>> idList = separateBy(comma, identifier).ignoreDelimiter().optional(Collections.emptyList());

    // 函数字面量
    private static final Parser<List<String>> singleParamList = identifier.map(s -> List.of(s));
    private static final Parser<List<String>> multiParamList = skip(lp).and(idList).skip(rp);
    private static final Parser<List<String>> paramList = singleParamList.or(multiParamList);
    private static final Parser<Expr> callableLiteral = paramList.skip(arrow).and(oneOf(
            skip(lb).and(stmts).skip(rb.fatal()).map(Block::new),
            lazyExpr.map(Return::new)
    )).map(p -> new CallableLiteral(p.getFirst(), p.getSecond()));

    // 对象字面量
    private static final Parser<Pair<String, Expr>> fieldPair = oneOf(
            identifier.skip(colon).and(lazyExpr),
            identifier.skip(lp).and(idList).skip(rp.and(lb)).and(stmts).skip(rb)
                    .map(p -> new Pair<>(p.getFirst().getFirst(), new CallableLiteral(p.getFirst().getSecond(), new Block(p.getSecond())))),
            identifier.map(id -> new Pair<>(id, new Var(id)))

    );
    private static final Parser<List<Pair<String, Expr>>> fieldList = separateBy(comma, fieldPair).ignoreDelimiter().optional(Collections.emptyList());
    private static final Parser<Expr> objLiteral = skip(lb)
            .and(fieldList)
            .skip(rb.fatal())
            .map(ps -> new ObjectLiteral(ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));

    private static final Parser<List<Expr>> exprList = separateBy(comma, lazyExpr).ignoreDelimiter().optional(Collections.emptyList());

    // 列表字面量
    private static final Parser<Expr> listLiteral = skip(ls)
            .and(exprList)
            .skip(rs.fatal())
            .map(ListLiteral::new);

    // 变量
    private static final Parser<Expr> var = identifier.map(Var::new);

    // 下标
    private static final Parser<Expr> subscript = skip(ls)
            .and(lazyExpr)
            .skip(rs.fatal());

    // 字段访问
    private static final Parser<String> fieldAccess = skip(dot)
            .and(identifier.fatal());

    // 调用列表
    private static final Parser<List<Expr>> callList = skip(lp)
            .and(exprList)
            .skip(rp.fatal());

    // 表达式

    private static final Parser<Expr> bracketExpr = skip(lp).and(lazyExpr).skip(rp);
    private static final Parser<Expr> negExpr = skip(sub).and(lazyPrimaryExpr).map(e -> new UnaryExpr(UnaryOp.Neg, e));
    private static final Parser<Expr> notExpr = skip(not).and(lazyPrimaryExpr).map(e -> new UnaryExpr(UnaryOp.Not, e));

    private static final Parser<Expr> primaryExpr = oneOf(
            doubleLiteral,
            integerLiteral,
            stringLiteral,
            boolLiteral,
            undefinedLiteral,
            callableLiteral,
            var,
            objLiteral,
            listLiteral,
            bracketExpr,
            negExpr,
            notExpr
    ).and(oneOf(callList, fieldAccess, subscript).many()).map(ByxScriptParser::buildExprElem);
    private static final Parser<Expr> multiplicativeExpr = separateBy(mul.or(div).or(rem), primaryExpr).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> additiveExpr = separateBy(add.or(sub), multiplicativeExpr).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> relationalExpr = separateBy(let.or(lt).or(get).or(gt).or(equ).or(neq), additiveExpr).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> andExpr = separateBy(and, relationalExpr).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> expr = separateBy(or, andExpr).map(ByxScriptParser::buildExpr);

    // 语句

    // 变量声明
    private static final Parser<Statement> varDeclare = skip(var_)
            .and(identifier.fatal())
            .skip(assign.fatal())
            .and(expr)
            .map(p -> new VarDeclare(p.getFirst(), p.getSecond()));

    // 函数声明
    private static final Parser<Statement> funcDeclare = skip(function_)
            .and(identifier.fatal())
            .skip(lp.fatal())
            .and(idList)
            .skip(rp.fatal().and(lb.fatal()))
            .and(stmts)
            .skip(rb.fatal())
            .map(p -> {
                String functionName = p.getFirst().getFirst();
                List<String> params = p.getFirst().getSecond();
                Statement body = new Block(p.getSecond());
                return new VarDeclare(functionName, new CallableLiteral(params, body));
            });

    private static final Parser<Expr> assignable = identifier.and(oneOf(subscript, fieldAccess).many())
            .map(ByxScriptParser::buildAssignable);

    // 赋值语句
    private static final Parser<Statement> assignStmt = assignable.and(assignOp).and(expr)
            .map(ByxScriptParser::buildAssignStatement);

    // 自增语句
    private static final Parser<Statement> preInc = skip(inc).and(assignable)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Add, e, new Literal(Value.of(1)))));
    private static final Parser<Statement> postInc = assignable.skip(inc)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Add, e, new Literal(Value.of(1)))));

    // 自减语句
    private static final Parser<Statement> preDec = skip(dec).and(assignable)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Sub, e, new Literal(Value.of(1)))));
    private static final Parser<Statement> postDec = assignable.skip(dec)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Sub, e, new Literal(Value.of(1)))));

    // 代码块
    private static final Parser<Statement> block = skip(lb)
            .and(stmts)
            .skip(rb.fatal())
            .map(Block::new);

    // if语句
    private static final Parser<Statement> ifStmt = skip(if_.and(lp.fatal()))
            .and(expr)
            .skip(rp.fatal().and(lb.fatal()))
            .and(stmts)
            .skip(rb.fatal())
            .and(skip(else_.and(if_).and(lp.fatal())).and(expr).skip(rp.fatal().and(lb.fatal())).and(stmts).skip(rb.fatal()).many())
            .and(skip(else_.and(lb.fatal())).and(stmts).skip(rb.fatal()).optional(Collections.emptyList()))
            .map(p -> {
                List<Pair<Expr, Statement>> cases = new ArrayList<>();
                cases.add(new Pair<>(p.getFirst().getFirst().getFirst(), new Block(p.getFirst().getFirst().getSecond())));
                for (Pair<Expr, List<Statement>> pp : p.getFirst().getSecond()) {
                    cases.add(new Pair<>(pp.getFirst(), new Block(pp.getSecond())));
                }
                Statement elseBranch = new Block(p.getSecond());
                return new If(cases, elseBranch);
            });

    // for语句
    private static final Parser<Statement> forStmt = skip(for_.and(lp))
            .and(lazyStmt)
            .skip(semi.fatal())
            .and(expr)
            .skip(semi.fatal())
            .and(lazyStmt)
            .skip(rp.fatal().and(lb.fatal()))
            .and(stmts)
            .skip(rb.fatal())
            .map(p -> {
                Statement init = p.getFirst().getFirst().getFirst();
                Expr cond = p.getFirst().getFirst().getSecond();
                Statement update = p.getFirst().getSecond();
                Statement body = new Block(p.getSecond());
                return new For(init, cond, update, body);
            });

    // while语句
    private static final Parser<Statement> whileStmt = skip(while_.and(lp.fatal()))
            .and(expr)
            .skip(rp.fatal().and(lb.fatal()))
            .and(stmts)
            .skip(rb.fatal())
            .map(p -> new While(p.getFirst(), new Block(p.getSecond())));

    // break语句
    private static final Parser<Statement> breakStmt = break_.map(Break::new);

    // continue语句
    private static final Parser<Statement> continueStmt = continue_.map(Continue::new);

    // return语句
    private static final Parser<Statement> returnStmt = skip(return_).and(expr.optional()).map(Return::new);

    // 函数调用语句
    private static final Parser<Statement> exprStmt = expr.map(ExprStatement::new);

    private static final Parser<Statement> stmt = oneOf(
            varDeclare,
            funcDeclare,
            ifStmt,
            forStmt,
            whileStmt,
            breakStmt,
            continueStmt,
            returnStmt,
            block,
            preInc,
            preDec,
            assignStmt,
            postInc,
            postDec,
            exprStmt
    );



    // 导入声明
    private static final Parser<String> importName = oneOf(digit, alpha, underline, ch('/')).many1().surroundBy(ignorable)
            .map(ByxScriptParser::join);
    private static final Parser<List<String>> imports = skip(import_).and(importName).many();

    // 程序
    private static final Parser<Program> program = imports.and(stmts)
            .map(p -> new Program(p.getFirst(), p.getSecond()));

    private static Parser<Expr> getExpr() {
        return expr;
    }

    private static Parser<Expr> getPrimaryExpr() {
        return primaryExpr;
    }

    private static Parser<Statement> getStmt() {
        return stmt;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    @SuppressWarnings("unchecked")
    private static Expr buildExprElem(Pair<Expr, List<Object>> p) {
        Expr e = p.getFirst();
        for (Object o : p.getSecond()) {
            if (o instanceof List) {
                e = new Call(e, (List<Expr>) o);
            } else if (o instanceof String) {
                e = new FieldAccess(e, (String) o);
            } else if (o instanceof Expr) {
                e = new Subscript(e, (Expr) o);
            }
        }
        return e;
    }

    private static Expr buildExpr(Pair<Expr, List<Pair<String, Expr>>> r) {
        Expr expr = r.getFirst();
        for (Pair<String, Expr> p : r.getSecond()) {
            String op = p.getFirst();
            switch (op) {
                case "+" -> expr = new BinaryExpr(BinaryOp.Add, expr, p.getSecond());
                case "-" -> expr = new BinaryExpr(BinaryOp.Sub, expr, p.getSecond());
                case "*" -> expr = new BinaryExpr(BinaryOp.Mul, expr, p.getSecond());
                case "/" -> expr = new BinaryExpr(BinaryOp.Div, expr, p.getSecond());
                case "%" -> expr = new BinaryExpr(BinaryOp.Rem, expr, p.getSecond());
                case ">" -> expr = new BinaryExpr(BinaryOp.GreaterThan, expr, p.getSecond());
                case ">=" -> expr = new BinaryExpr(BinaryOp.GreaterEqualThan, expr, p.getSecond());
                case "<" -> expr = new BinaryExpr(BinaryOp.LessThan, expr, p.getSecond());
                case "<=" -> expr = new BinaryExpr(BinaryOp.LessEqualThan, expr, p.getSecond());
                case "==" -> expr = new BinaryExpr(BinaryOp.Equal, expr, p.getSecond());
                case "!=" -> expr = new BinaryExpr(BinaryOp.NotEqual, expr, p.getSecond());
                case "&&" -> expr = new BinaryExpr(BinaryOp.And, expr, p.getSecond());
                case "||" -> expr = new BinaryExpr(BinaryOp.Or, expr, p.getSecond());
                default -> throw new InterpretException("unknown binary operator: " + op);
            }
        }
        return expr;
    }

    private static Expr buildAssignable(Pair<String, List<Object>> p) {
        Expr e = new Var(p.getFirst());
        for (Object o : p.getSecond()) {
            if (o instanceof Expr) {
                e = new Subscript(e, (Expr) o);
            } else if (o instanceof String) {
                e = new FieldAccess(e, (String) o);
            }
        }
        return e;
    }

    private static Statement buildAssignStatement(Pair<Pair<Expr, String>, Expr> p) {
        Expr lhs = p.getFirst().getFirst();
        Expr rhs = p.getSecond();
        String op = p.getFirst().getSecond();
        switch (op) {
            case "=":
                return new Assign(lhs, rhs);
            case "+=":
                return new Assign(lhs, new BinaryExpr(BinaryOp.Add, lhs, rhs));
            case "-=":
                return new Assign(lhs, new BinaryExpr(BinaryOp.Sub, lhs, rhs));
            case "*=":
                return new Assign(lhs, new BinaryExpr(BinaryOp.Mul, lhs, rhs));
            case "/=":
                return new Assign(lhs, new BinaryExpr(BinaryOp.Div, lhs, rhs));
        }
        throw new RuntimeException("invalid assign expression: " + op);
    }

    /**
     * 解析脚本
     * @param script 脚本字符串
     * @return 抽象语法树
     */
    public static Program parse(String script) {
        return program.parse(script);
    }
}
