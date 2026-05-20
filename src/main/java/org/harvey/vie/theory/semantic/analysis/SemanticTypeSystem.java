package org.harvey.vie.theory.semantic.analysis;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

// TODO 为什么要封装一下? 多麻烦? 存在意义呢?
public class SemanticTypeSystem {
    private final TypeResolver resolver = new TypeResolver();
    private final TypeConversionRule conversionRule = new TypeConversionRule();

    public SemanticType literalType(SourceToken token) {
        return resolver.literalType(token);
    }

    public SemanticType typeToken(SourceToken token) {
        return resolver.typeToken(token);
    }

    public int integerLiteral(SourceToken token) {
        return resolver.integerLiteral(token);
    }


    public boolean canImplicitlyConvert(SemanticType from, SemanticType to) {
        return conversionRule.canImplicitlyConvert(from, to);
    }

    public boolean requiresImplicitCast(SemanticType from, SemanticType to) {
        return conversionRule.requiresImplicitCast(from, to);
    }

    public SemanticType commonBinaryType(SemanticType left, SemanticType right) {
        return conversionRule.commonBinaryType(left, right);
    }
}
