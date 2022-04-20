package byx.script.runtime;

import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 封装ByxScript运行时的值类型
 */
public abstract class Value {
    private final Map<String, Value> fields = new HashMap<>();

    public Map<String, Value> getFields() {
        return fields;
    }

    protected void setField(String field, Value value) {
        fields.put(field, value);
    }

    protected void setFields(Map<String, Value> fieldsToAdd) {
        fields.putAll(fieldsToAdd);
    }

    public static Value of(int val) {
        return new IntegerValue(val);
    }

    public static Value of (double val) {
        return new DoubleValue(val);
    }

    public static Value of(boolean val) {
        return BoolValue.of(val);
    }

    public static Value of (String val) {
        return new StringValue(val);
    }

    public static Value of(Function<List<Value>, Value> callable) {
        return new CallableValue(callable);
    }

    public static Value of(Map<String, Value> val) {
        return new ObjectValue(val);
    }

    public static Value of(List<Value> val) {
        return new ListValue(val);
    }

    /**
     * 加（+）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value add(Value rhs) {
        throw new InterpretException(String.format("unsupported operator + between %s and %s", this, rhs));
    }

    /**
     * 减（-）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value sub(Value rhs) {
        throw new InterpretException(String.format("unsupported operator - between %s and %s", this, rhs));
    }

    /**
     * 乘（*）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value mul(Value rhs) {
        throw new InterpretException(String.format("unsupported operator * between %s and %s", this, rhs));
    }

    /**
     * 除（/）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value div(Value rhs) {
        throw new InterpretException(String.format("unsupported operator / between %s and %s", this, rhs));
    }

    /**
     * 取余（%）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value rem(Value rhs) {
        throw new InterpretException(String.format("unsupported operator %% between %s and %s", this, rhs));
    }

    /**
     * 相反数（-）
     * @return 运算结果
     */
    public Value neg() {
        throw new InterpretException(String.format("unsupported operator - on %s", this));
    }

    /**
     * 小于（<）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value lessThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator < between %s and %s", this, rhs));
    }

    /**
     * 小于等于（<=）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value lessEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator <= between %s and %s", this, rhs));
    }

    /**
     * 大于（>）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value greaterThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator > between %s and %s", this, rhs));
    }

    /**
     * 大于等于（>=）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value greaterEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator >= between %s and %s", this, rhs));
    }

    /**
     * 等于（==）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value equal(Value rhs) {
        throw new InterpretException(String.format("unsupported operator == between %s and %s", this, rhs));
    }

    /**
     * 不等于（!=）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value notEqual(Value rhs) {
        Value v = equal(rhs);
        if (v instanceof BoolValue) {
            return Value.of(!((BoolValue) v).getValue());
        }
        throw new InterpretException(String.format("unsupported operator != between %s and %s", this, rhs));
    }

    /**
     * 与（&&）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value and(Value rhs) {
        throw new InterpretException(String.format("unsupported operator && between %s and %s", this, rhs));
    }

    /**
     * 或（||）
     * @param rhs rhs
     * @return 运算结果
     */
    public Value or(Value rhs) {
        throw new InterpretException(String.format("unsupported operator || between %s and %s", this, rhs));
    }

    /**
     * 非（!）
     * @return 运算结果
     */
    public Value not() {
        throw new InterpretException(String.format("unsupported operator ! on %s", this));
    }

    /**
     * 函数调用（value(...value)）
     * @param args 参数列表
     * @return 返回值
     */
    public Value call(List<Value> args) {
        throw new InterpretException(String.format("%s is not callable", this));
    }

    /**
     * 获取属性（expr.field）
     * @param field 属性名
     * @return 属性值
     */
    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        throw new InterpretException(String.format("field %s not exist", field));
    }

    /**
     * 设置属性（value.field = rhs）
     * @param field 属性名
     * @param rhs 属性值
     */
    public void fieldAssign(String field, Value rhs) {
        throw new InterpretException(String.format("unsupported field assign: %s", this));
    }

    /**
     * 获取下标（value[sub]）
     * @param sub 下标值
     * @return 下标对应的值
     */
    public Value subscript(Value sub) {
        throw new InterpretException(String.format("unsupported subscript: %s", this));
    }

    /**
     * 下标赋值（value[sub] = rhs）
     * @param subscript 下标值
     * @param rhs rhs
     */
    public void subscriptAssign(Value subscript, Value rhs) {
        throw new InterpretException(String.format("unsupported subscript assign: %s", this));
    }
}
