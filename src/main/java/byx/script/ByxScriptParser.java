package byx.script;

import byx.script.ast.Program;
import byx.script.ast.expr.*;
import byx.script.ast.stmt.*;
import byx.script.parserc.Pair;
import byx.script.parserc.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static byx.script.parserc.Parsers.*;

/**
 * ByxParser解析器
 */
public class ByxScriptParser {
    // 词法元素

    // 空白字符
    private static final Parser<?> w = chs(' ', '\t', '\r', '\n').map(Objects::toString);

    // 行注释
    private static final Parser<?> lineComment = string("//").skip(not('\n').many()).skip(ch('\n'));

    // 块注释
    private static final Parser<?> blockComment = string("/*").and(any().manyUntil(string("*/"))).and(string("*/"));

    // 可忽略元素
    private static final Parser<?> ignorable = oneOf(w, lineComment, blockComment).many();

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
            .then(s -> kw.contains(s) ? fail() : empty(s))
            .surroundBy(ignorable);

    // 前向引用
    private static final Parser<Statement> lazyStmt = lazy(ByxScriptParser::getStmt);
    private static final Parser<List<Statement>> stmts = lazyStmt.skip(semi.optional()).many();
    private static final Parser<Expr> lazyExprElem = lazy(ByxScriptParser::getExprElem);
    private static final Parser<Expr> lazyExpr = lazy(ByxScriptParser::getExpr);

    // 表达式

    // 整数字面量
    private static final Parser<Expr> integerLiteral = integer.map(s -> new IntegerLiteral(Integer.parseInt(s)));

    // 浮点数字面量
    private static final Parser<Expr> doubleLiteral = decimal.map(s -> new DoubleLiteral(Double.parseDouble(s)));

    // 字符串字面量
    private static final Parser<Expr> stringLiteral = string.map(StringLiteral::new);

    // 布尔值字面量
    private static final Parser<Expr> boolLiteral = bool.map(s -> new BoolLiteral(Boolean.parseBoolean(s)));

    // undefined字面量
    private static final Parser<Expr> undefinedLiteral = undefined_.map(UndefinedLiteral::new);

    private static final Parser<List<String>> idList = separateBy(comma, identifier).ignoreDelimiter().optional(Collections.emptyList());

    // 函数字面量
    private static final Parser<List<String>> singleParamList = identifier.map(s -> List.of(s));
    private static final Parser<List<String>> multiParamList = skip(lp).and(idList).skip(rp);
    private static final Parser<List<String>> paramList = singleParamList.or(multiParamList);
    private static final Parser<Expr> exprFuncLiteral = paramList.skip(arrow).and(lazyExpr).failIf(assignOp.or(inc).or(dec))
            .map(p -> new FunctionLiteral(p.getFirst(), new Return(p.getSecond())));
    private static final Parser<Expr> stmtFuncLiteral = paramList.skip(arrow).and(lazyStmt)
            .map(p -> new FunctionLiteral(p.getFirst(), p.getSecond()));
    private static final Parser<Expr> emptyFuncLiteral = paramList.skip(arrow).skip(lb).skip(rb)
            .map(p -> new FunctionLiteral(p, EmptyStatement.INSTANCE));
    private static final Parser<Expr> funcLiteral = oneOf(emptyFuncLiteral, exprFuncLiteral, stmtFuncLiteral);

    // 对象字面量
    private static final Parser<Pair<String, Expr>> fieldPair = oneOf(
            identifier.skip(colon).and(lazyExpr),
            identifier.skip(lp).and(idList).skip(rp.and(lb)).and(stmts).skip(rb)
                    .map(p -> new Pair<>(p.getFirst().getFirst(), new FunctionLiteral(p.getFirst().getSecond(), new Block(p.getSecond())))),
            identifier.map(id -> new Pair<>(id, new Var(id)))

    );
    private static final Parser<List<Pair<String, Expr>>> fieldList = separateBy(comma, fieldPair).ignoreDelimiter().optional(Collections.emptyList());
    private static final Parser<Expr> objLiteral = skip(lb).and(fieldList).skip(rb)
            .map(ps -> new ObjectLiteral(ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));

    private static final Parser<List<Expr>> exprList = separateBy(comma, lazyExpr).ignoreDelimiter().optional(Collections.emptyList());

    // 列表字面量
    private static final Parser<Expr> listLiteral = skip(ls).and(exprList).skip(rs)
            .map(ListLiteral::new);

    // 变量
    private static final Parser<Expr> var = identifier.map(Var::new);

    // 下标
    private static final Parser<Expr> subscript = skip(ls).and(lazyExpr).skip(rs);

    // 字段访问
    private static final Parser<String> fieldAccess = skip(dot).and(identifier);

    // 实参列表
    private static final Parser<List<Expr>> argList = skip(lp).and(exprList).skip(rp);

    // 表达式

    private static final Parser<Expr> exprElem = oneOf(
            doubleLiteral,
            integerLiteral,
            stringLiteral,
            boolLiteral,
            undefinedLiteral,
            funcLiteral,
            var,
            objLiteral,
            listLiteral,
            skip(lp).and(lazyExpr).skip(rp), // 括号
            skip(sub).and(lazyExprElem).map(Neg::new), // 负号（-）
            skip(not).and(lazyExprElem).map(Not::new) // 非（!）
    ).and(oneOf(argList, fieldAccess, subscript).many()).map(ByxScriptParser::buildExprElem);
    private static final Parser<Expr> e1 = separateBy(mul.or(div).or(rem), exprElem).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> e2 = separateBy(add.or(sub), e1).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> e3 = separateBy(let.or(lt).or(get).or(gt).or(equ).or(neq), e2).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> e4 = separateBy(and, e3).map(ByxScriptParser::buildExpr);
    private static final Parser<Expr> expr = separateBy(or, e4).map(ByxScriptParser::buildExpr);

    // 语句

    // 变量声明
    private static final Parser<Statement> varDeclare = skip(var_).and(identifier).skip(assign).and(expr)
            .map(p -> new VarDeclaration(p.getFirst(), p.getSecond()));

    // 函数声明
    private static final Parser<Statement> funcDeclare = skip(function_).and(identifier).skip(lp).and(idList).skip(rp.and(lb)).and(stmts).skip(rb)
            .map(p -> {
                String functionName = p.getFirst().getFirst();
                List<String> params = p.getFirst().getSecond();
                Statement body = new Block(p.getSecond());
                return new VarDeclaration(functionName, new FunctionLiteral(params, body));
            });

    private static final Parser<Expr> assignable = identifier.and(oneOf(subscript, fieldAccess).manyUntil(assignOp))
            .map(ByxScriptParser::buildAssignable);

    // 赋值语句
    private static final Parser<Statement> assignStmt = assignable.and(assignOp).and(expr)
            .map(ByxScriptParser::buildAssignStatement);

    // 自增语句
    private static final Parser<Statement> incStmt = assignable.skip(inc).or(skip(inc).and(assignable))
            .map(e -> new AssignStatement(e, new Add(e, new IntegerLiteral(1))));

    // 自减语句
    private static final Parser<Statement> decStmt = assignable.skip(dec).or(skip(dec).and(assignable))
            .map(e -> new AssignStatement(e, new Sub(e, new IntegerLiteral(1))));

    // 代码块
    private static final Parser<Statement> block = skip(lb).and(stmts).skip(rb).map(Block::new);

    // if-else语句
    private static final Parser<Statement> ifelse = skip(if_.and(lp)).and(expr).skip(rp).and(lazyStmt).and(skip(else_).and(lazyStmt).optional(EmptyStatement.INSTANCE))
            .map(p -> new IfElse(p.getFirst().getFirst(), p.getFirst().getSecond(), p.getSecond()));

    // for循环
    private static final Parser<Statement> forLoop = skip(for_.and(lp)).and(lazyStmt).skip(semi).and(expr).skip(semi).and(lazyStmt).skip(rp).and(lazyStmt)
            .map(p -> new ForLoop(p.getFirst().getFirst().getFirst(), p.getFirst().getFirst().getSecond(), p.getFirst().getSecond(), p.getSecond()));

    // while循环
    private static final Parser<Statement> whileLoop = skip(while_.and(lp)).and(expr).skip(rp).and(lazyStmt)
            .map(p -> new WhileLoop(p.getFirst(), p.getSecond()));

    // break语句
    private static final Parser<Statement> breakStmt = break_.map(Break::new);

    // continue语句
    private static final Parser<Statement> continueStmt = continue_.map(Continue::new);

    // return语句
    private static final Parser<Statement> returnStmt = skip(return_).and(expr.optional()).map(Return::new);

    // 表达式语句
    private static final Parser<Statement> exprStmt = expr.map(ExprStatement::new);

    private static final Parser<Statement> stmt = oneOf(
            varDeclare,
            funcDeclare,
            assignStmt,
            incStmt,
            decStmt,
            block,
            ifelse,
            forLoop,
            whileLoop,
            breakStmt,
            continueStmt,
            returnStmt,
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

    private static Parser<Expr> getExprElem() {
        return exprElem;
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
            if (o instanceof List) { // 函数调用
                e = new Call(e, (List<Expr>) o);
            } else if (o instanceof String) { // 属性访问
                e = new FieldAccess(e, (String) o);
            } else if (o instanceof Expr) { // 下标访问
                e = new Subscript(e, (Expr) o);
            }
        }
        return e;
    }

    private static Expr buildExpr(Pair<Expr, List<Pair<String, Expr>>> r) {
        Expr expr = r.getFirst();
        for (Pair<String, Expr> p : r.getSecond()) {
            switch (p.getFirst()) {
                case "+" -> expr = new Add(expr, p.getSecond());
                case "-" -> expr = new Sub(expr, p.getSecond());
                case "*" -> expr = new Mul(expr, p.getSecond());
                case "/" -> expr = new Div(expr, p.getSecond());
                case "%" -> expr = new Rem(expr, p.getSecond());
                case ">" -> expr = new GreaterThan(expr, p.getSecond());
                case ">=" -> expr = new GreaterEqualThan(expr, p.getSecond());
                case "<" -> expr = new LessThan(expr, p.getSecond());
                case "<=" -> expr = new LessEqualThan(expr, p.getSecond());
                case "==" -> expr = new Equal(expr, p.getSecond());
                case "!=" -> expr = new NotEqual(expr, p.getSecond());
                case "&&" -> expr = new And(expr, p.getSecond());
                case "||" -> expr = new Or(expr, p.getSecond());
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
                return new AssignStatement(lhs, rhs);
            case "+=":
                return new AssignStatement(lhs, new Add(lhs, rhs));
            case "-=":
                return new AssignStatement(lhs, new Sub(lhs, rhs));
            case "*=":
                return new AssignStatement(lhs, new Mul(lhs, rhs));
            case "/=":
                return new AssignStatement(lhs, new Div(lhs, rhs));
        }
        throw new RuntimeException("invalid assign expression: " + op);
    }

    /**
     * 解析脚本
     * @param script 脚本字符串
     * @return Program对象
     */
    public static Program parse(String script) {
        return program.parse(script);
    }
}
