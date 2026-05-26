package org.harvey.vie.theory.semantic.identifier.table;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:05
 */
@Slf4j
public class IdentifierTableBuilder {

    private final List<ScopeBuilder> identifierTable = new ArrayList<>();
    private final IdGenerator recordIdGenerator = new IdGenerator();

    public IdentifierTableBuilder() {
        identifierTable.add(new ScopeBuilder(0));
    }

    public boolean existIdentifier(SourceToken identifierToken) {
        return getIdentifier(identifierToken) != null;
    }

    public IdentifierRecord getIdentifier(SourceToken identifierToken) {
        IdentifierKey identifierKey = IdentifierKey.generate(identifierToken);
        for (int i = identifierTable.size() - 1; i >= 0; i--) {
            IdentifierRecord identifierRecord = identifierTable.get(i).get(identifierKey);
            if (identifierRecord != null) {
                return identifierRecord;
            }
        }
        return null;
    }

    public IdentifierRecord registerIdentifier(
            HeadNode typeHeadNode,
            SemanticType declaredType,
            SourceToken identifierToken,
            boolean initialized,
            FunctionRecord ownerFunction,
            ConstantValue constantValue) {
        ScopeBuilder last = getLast();
        int no = recordIdGenerator.next();
        int offset = last.nextNo();
        IdentifierRecord identifierRecord = new IdentifierRecord(
                no,
                offset,
                typeHeadNode,
                declaredType,
                identifierToken.getLexeme(),
                initialized,
                ownerFunction,
                constantValue
        );
        last.put(IdentifierKey.generate(identifierToken), identifierRecord);
        log.trace("register identifier: " + identifierRecord.displayString());
        log.trace("now identifier table: \n" +
                  identifierTable.stream().map(r -> "\t" + r).collect(Collectors.joining("\n")));
        return identifierRecord;
    }


    public void scopeInto() {
        log.trace("scope into");
        ScopeBuilder last = getLast();
        identifierTable.add(new ScopeBuilder(last.currentId()));
    }

    /**
     * @return scope
     */
    public IdentifierRecord[] scopeExist() {
        log.trace("scope exist");
        return removeLast().records()
                .stream()
                .sorted(Comparator.comparingInt(IdentifierRecord::getNo))
                .toArray(IdentifierRecord[]::new);
    }

    private ScopeBuilder removeLast() {
        return identifierTable.remove(identifierTable.size() - 1);
    }

    private ScopeBuilder getLast() {
        return identifierTable.get(identifierTable.size() - 1);
    }

    private static class ScopeBuilder {
        private final Map<IdentifierKey, IdentifierRecord> map = new HashMap<>();
        private final IdGenerator idGenerator;

        public ScopeBuilder(int initialValue) {
            this.idGenerator = new IdGenerator(initialValue);
        }

        public boolean containsKey(IdentifierKey identifierKey) {
            return map.containsKey(identifierKey);
        }

        public void put(IdentifierKey identifierKey, IdentifierRecord identifierRecord) {
            map.put(identifierKey, identifierRecord);
        }

        public int currentId() {
            return idGenerator.current();
        }

        public Collection<IdentifierRecord> records() {
            return map.values();
        }

        public int nextNo() {
            return idGenerator.next();
        }

        public IdentifierRecord get(IdentifierKey identifierKey) {
            return map.get(identifierKey);
        }

        @Override
        public String toString() {
            return map.values().stream().map(IdentifierRecord::displayString).collect(Collectors.joining(",", "{", "}"));
        }
    }


}
