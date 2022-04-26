package byx.script.interpreter.value;

import byx.script.interpreter.InterpretException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 封装ByxScript运行时的值类型
 */
public interface Value {
    static Value of(int val) {
        return new IntegerValue(val);
    }

    static Value of (double val) {
        return new DoubleValue(val);
    }

    static Value of(boolean val) {
        return BoolValue.of(val);
    }

    static Value of (String val) {
        return new StringValue(val);
    }

    static Value of(Function<List<Value>, Value> callable) {
        return new CallableValue(callable);
    }

    static Value of(Map<String, Value> val) {
        return new ObjectValue(val);
    }

    static Value of(List<Value> val) {
        return new ListValue(val);
    }

    static Value undefined() {
        return UndefinedValue.INSTANCE;
    }

    /**
     * 加（+）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value add(Value rhs) {
        throw new InterpretException(String.format("unsupported operator + between %s and %s", this, rhs));
    }

    /**
     * 减（-）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value sub(Value rhs) {
        throw new InterpretException(String.format("unsupported operator - between %s and %s", this, rhs));
    }

    /**
     * 乘（*）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value mul(Value rhs) {
        throw new InterpretException(String.format("unsupported operator * between %s and %s", this, rhs));
    }

    /**
     * 除（/）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value div(Value rhs) {
        throw new InterpretException(String.format("unsupported operator / between %s and %s", this, rhs));
    }

    /**
     * 取余（%）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value rem(Value rhs) {
        throw new InterpretException(String.format("unsupported operator %% between %s and %s", this, rhs));
    }

    /**
     * 相反数（-）
     * @return 运算结果
     */
    default Value neg() {
        throw new InterpretException(String.format("unsupported operator - on %s", this));
    }

    /**
     * 小于（<）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value lessThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator < between %s and %s", this, rhs));
    }

    /**
     * 小于等于（<=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value lessEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator <= between %s and %s", this, rhs));
    }

    /**
     * 大于（>）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value greaterThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator > between %s and %s", this, rhs));
    }

    /**
     * 大于等于（>=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value greaterEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator >= between %s and %s", this, rhs));
    }

    /**
     * 等于（==）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value equal(Value rhs) {
        return Value.of(this == rhs);
    }

    /**
     * 不等于（!=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value notEqual(Value rhs) {
        return Value.of(this != rhs);
    }

    /**
     * 与（&&）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value and(Value rhs) {
        throw new InterpretException(String.format("unsupported operator && between %s and %s", this, rhs));
    }

    /**
     * 或（||）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value or(Value rhs) {
        throw new InterpretException(String.format("unsupported operator || between %s and %s", this, rhs));
    }

    /**
     * 非（!）
     * @return 运算结果
     */
    default Value not() {
        throw new InterpretException(String.format("unsupported operator ! on %s", this));
    }

    /**
     * 函数调用（value(...value)）
     * @param args 参数列表
     * @return 返回值
     */
    default Value call(List<Value> args) {
        throw new InterpretException(String.format("%s is not callable", this));
    }

    /**
     * 获取属性（expr.field）
     * @param field 属性名
     * @return 属性值
     */
    default Value getField(String field) {
        throw new InterpretException(String.format("field %s not exist", field));
    }

    /**
     * 设置属性（value.field = rhs）
     * @param field 属性名
     * @param rhs 属性值
     */
    default void fieldAssign(String field, Value rhs) {
        throw new InterpretException(String.format("unsupported field assign: %s", this));
    }

    /**
     * 获取下标（value[sub]）
     * @param sub 下标值
     * @return 下标对应的值
     */
    default Value subscript(Value sub) {
        throw new InterpretException(String.format("unsupported subscript: %s", this));
    }

    /**
     * 下标赋值（value[sub] = rhs）
     * @param subscript 下标值
     * @param rhs rhs
     */
    default void subscriptAssign(Value subscript, Value rhs) {
        throw new InterpretException(String.format("unsupported subscript assign: %s", this));
    }
}
