package org.harvey.vie.theory.semantic.display;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.function.FunctionParameter;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.value.ConstantValue;

import java.util.StringJoiner;

/**
 * Shared display formatting for semantic reports and console output.
 *
 * @author Temper
 */
public final class SemanticDisplaySupport {
    /**
     * 函数功能：创建 SemanticDisplaySupport 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private SemanticDisplaySupport() {
    }

    /**
     * 函数功能：格式化函数签名。
     * 输入：
     * - function：FunctionRecord 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    public static String formatFunctionSignature(FunctionRecord function, Iterable<StructRecord> structTable) {
        StringJoiner joiner = new StringJoiner(", ");
        for (FunctionParameter parameter : function.getParameters()) {
            joiner.add(formatType(parameter.getType(), structTable) +
                       " " +
                       SourceTokenStringMapping.utf8(parameter.getNameToken()));
        }
        return formatType(function.getSignature().getReturnType(), structTable) +
               " " +
               formatFunctionName(function) +
               "(" +
               joiner +
               ")";
    }

    /**
     * 函数功能：格式化函数名称。
     * 输入：
     * - function：FunctionRecord 类型参数。
     * 输出：字符串结果。
     */

    public static String formatFunctionName(FunctionRecord function) {
        return SourceTokenStringMapping.utf8(function.getSignature().getNameToken());
    }

    /**
     * 函数功能：格式化标识符记录。
     * 输入：
     * - record：IdentifierRecord 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    public static String formatIdentifierRecord(IdentifierRecord record, Iterable<StructRecord> structTable) {
        return "record=" +
               record.getNo() +
               " offset=" +
               record.getOffset() +
               " type=" +
               formatType(record.getDeclaredType(), structTable) +
               " name=" +
               SourceTokenStringMapping.utf8(record.getLexeme()) +
               " initialized=" +
               record.isInitialized() +
               " constant=" +
               formatConstant(record.getConstantValue());
    }

    /**
     * 函数功能：格式化结构体记录。
     * 输入：
     * - record：StructRecord 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    public static String formatStructRecord(StructRecord record, Iterable<StructRecord> structTable) {
        return "index=" + record.getTableIndex() + " name=" + record.displayName();
    }

    /**
     * 函数功能：格式化结构体字段。
     * 输入：
     * - field：StructField 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    public static String formatStructField(StructField field, Iterable<StructRecord> structTable) {
        return "offset=" +
               field.getOffset() +
               " type=" +
               formatType(field.getType(), structTable) +
               " name=" +
               SourceTokenStringMapping.utf8(field.getNameToken());
    }

    /**
     * 函数功能：格式化语义类型。
     * 输入：
     * - type：SemanticType 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    public static String formatType(SemanticType type, Iterable<StructRecord> structTable) {
        if (type == null) {
            return "<none>";
        }
        StringBuilder builder = new StringBuilder(baseTypeName(type, structTable));
        for (int i = 0; i < type.getDimensions().size(); i++) {
            builder.append("[]");
        }
        return builder.toString();
    }

    /**
     * 函数功能：格式化常量值。
     * 输入：
     * - value：ConstantValue 类型参数。
     * 输出：字符串结果。
     */

    private static String formatConstant(ConstantValue value) {
        return value == null ? "<none>" : value.toString();
    }

    /**
     * 函数功能：获取基础类型名称。
     * 输入：
     * - type：SemanticType 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    private static String baseTypeName(SemanticType type, Iterable<StructRecord> structTable) {
        switch (type.getKind()) {
            case BOOLEAN:
                return "boolean";
            case CHARACTER:
                return "character";
            case INT32:
                return "int32";
            case FLOAT64:
                return "float64";
            case STRING:
                return "string";
            case VOID:
                return "void";
            case NULL:
                return "null";
            case STRUCT:
                return resolveStructName(type.getNamedTypeKey(), structTable);
            default:
                throw new IllegalStateException("Unknown type of: " + type.getKind());
        }
    }

    /**
     * 函数功能：解析结构体名称。
     * 输入：
     * - key：IdentifierKey 类型参数。
     * - structTable：Iterable<StructRecord> 类型参数。
     * 输出：字符串结果。
     */

    private static String resolveStructName(IdentifierKey key, Iterable<StructRecord> structTable) {
        if (key == null) {
            return "<anonymous-struct>";
        }
        for (StructRecord record : structTable) {
            if (key.equals(record.getNameKey())) {
                return record.displayName();
            }
        }
        return "<missing-struct>";
    }
}
