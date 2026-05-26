package org.harvey.vie.theory.semantic.structure;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of declared struct types.
 *
 * @author Temper
 */
public class StructContext {
    private final Map<IdentifierKey, StructRecord> records = new LinkedHashMap<>();

    public boolean exists(SourceToken token) {
        return exists(IdentifierKey.generate(token));
    }

    public boolean exists(IdentifierKey key) {
        return records.containsKey(key);
    }

    public StructRecord get(SourceToken token) {
        return get(IdentifierKey.generate(token));
    }

    public StructRecord get(IdentifierKey key) {
        return records.get(key);
    }

    public void register(StructRecord record) {
        records.put(record.getNameKey(), record);
    }

    public int size() {
        return records.size();
    }

    public Collection<StructRecord> records() {
        return records.values();
    }
}
