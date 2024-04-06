package byx.script.core.interpreter.value;

import byx.script.core.interpreter.exception.ByxScriptRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * 封装ByxScript运行时的值类型
 */
public interface Value {
    Map<Class<? extends Value>, String> TYPE_ID_MAP = Map.of(
            Value.class, "any",
            NullValue.class, "null",
            IntegerValue.class, "integer",
            DoubleValue.class, "double",
            BoolValue.class, "bool",
            StringValue.class, "string",
            ListValue.class, "list",
            CallableValue.class, "callable",
            ObjectValue.class, "object"
    );

    default String typeId() {
        return TYPE_ID_MAP.getOrDefault(getClass(), "unknown");
    }

    /**
     * 加（+）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value add(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator + between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 减（-）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value sub(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator - between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 乘（*）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value mul(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator * between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 除（/）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value div(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator / between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 取余（%）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value rem(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator %% between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 相反数（-）
     * @return 运算结果
     */
    default Value neg() {
        throw new ByxScriptRuntimeException(String.format("unsupported operator - on %s", typeId()));
    }

    /**
     * 小于（<）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value lessThan(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator < between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 小于等于（<=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value lessEqualThan(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator <= between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 大于（>）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value greaterThan(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator > between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 大于等于（>=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value greaterEqualThan(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator >= between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 等于（==）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value equal(Value rhs) {
        return BoolValue.of(this == rhs);
    }

    /**
     * 不等于（!=）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value notEqual(Value rhs) {
        return BoolValue.of(this != rhs);
    }

    /**
     * 与（&&）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value and(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator && between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 或（||）
     * @param rhs rhs
     * @return 运算结果
     */
    default Value or(Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported operator || between %s and %s", typeId(), rhs.typeId()));
    }

    /**
     * 非（!）
     * @return 运算结果
     */
    default Value not() {
        throw new ByxScriptRuntimeException(String.format("unsupported operator ! on %s", this.typeId()));
    }

    /**
     * 函数调用（value(...value)）
     * @param args 参数列表
     * @return 返回值
     */
    default Value call(List<Value> args) {
        throw new ByxScriptRuntimeException(String.format("%s is not callable", this));
    }

    /**
     * 获取属性（expr.field）
     * @param field 属性名
     * @return 属性值
     */
    default Value getField(String field) {
        throw new ByxScriptRuntimeException(String.format("field %s not exist", field));
    }

    /**
     * 设置属性（value.field = rhs）
     * @param field 属性名
     * @param rhs 属性值
     */
    default void setField(String field, Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported field assign: %s", this));
    }

    /**
     * 获取下标（value[sub]）
     * @param sub 下标值
     * @return 下标对应的值
     */
    default Value subscript(Value sub) {
        throw new ByxScriptRuntimeException(String.format("unsupported subscript: %s", this));
    }

    /**
     * 设置下标（value[sub] = rhs）
     * @param subscript 下标值
     * @param rhs rhs
     */
    default void setSubscript(Value subscript, Value rhs) {
        throw new ByxScriptRuntimeException(String.format("unsupported subscript assign: %s", this));
    }
}
