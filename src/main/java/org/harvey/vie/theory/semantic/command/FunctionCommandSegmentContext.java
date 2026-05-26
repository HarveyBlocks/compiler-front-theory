package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Temper
 */
public class FunctionCommandSegmentContext {
    private final Map<IdentifierKey, FunctionCommandSegment> segments = new LinkedHashMap<>();

    public void register(FunctionCommandSegment segment) {
        segments.put(segment.getFunction().getNameKey(), segment);
    }

    public FunctionCommandSegment get(IdentifierKey key) {
        return segments.get(key);
    }

    public Collection<FunctionCommandSegment> values() {
        return segments.values();
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }

    public List<FunctionCommandSegment> snapshot() {
        return List.copyOf(segments.values());
    }
}
