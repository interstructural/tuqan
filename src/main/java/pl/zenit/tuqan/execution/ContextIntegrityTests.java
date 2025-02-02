package pl.zenit.tuqan.execution;

import java.util.ArrayList;
import java.util.List;

import pl.zenit.tuqan.execution.relationshiprules.LedgerRestrictions;
import pl.zenit.tuqan.execution.relationshiprules.MirageRestrictions;
import pl.zenit.tuqan.execution.relationshiprules.ScopeRestrictions;
import pl.zenit.tuqan.execution.relationshiprules.VirtualRestrictions;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.ReservedNoUseKeywords;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

class ContextIntegrityTests implements TestSupplier {

    private final TuqanContext context;

    public ContextIntegrityTests(TuqanContext context) {
        this.context = context;
    }
    
    @Override public List<RunnableAsTest> getTests() {
        
        List<RunnableAsTest> tests = new ArrayList<>();

        for ( TuqanScopeclass scope : context.getAllScopes() ) {
            tests.add(()-> enumReferentExistance(scope));
            tests.add(()-> scopeReferentExistance(scope));
            tests.add(()-> testNameUniqueness(scope));
            tests.add(() -> forbiddenKeywordsTest(scope));
            tests.add(() -> testRelationshipConsistency(scope));
        }

        return tests;
    }

    private void testRelationshipConsistency(TuqanScopeclass scope) throws TestFuckup {
        new VirtualRestrictions(context).test(scope);
        new ScopeRestrictions(context).test(scope);
        new LedgerRestrictions(context).test(scope);
        new MirageRestrictions(context).test(scope);
    }

    private void enumReferentExistance(TuqanScopeclass scope) throws TestFuckup {
        for ( TuqanField field : scope.getFields() ) {
            boolean isEnum = field.getType().getDataType() == field.getType().getDataType().ENUM;
            if (!isEnum)
                continue;

            String enumType = field.getType().getInfo().getTargetName();
            TuqanEnumclass enumObject = context.findEnum(enumType);
            if ( enumObject == null ) {
                throw new TestFuckup(scope.getName() + "."
                                                 + field.getName()
                                                 + " has unknown enum type \""
                                                 + field.getType().getInfo().getTargetName() + "\"");
            }
        }
    }

    private void scopeReferentExistance(TuqanScopeclass scope) throws TestFuckup {
        for ( TuqanField field : scope.getFields() ) {
            if ( field.getType().isRelation()) {
                String targetScope = field.getType().getInfo().getTargetName();
                TuqanScopeclass foundScope = context.findScope(targetScope);

                if (foundScope == null) {
                    throw new TestFuckup(scope.getName() + "."
                                                     + field.getName()
                                                     + " has unknown target scope \""
                                                     + field.getType().getInfo().getTargetName() + "\"");
                }
            }
        }
    }

    private void forbiddenKeywordsTest(TuqanScopeclass scope) throws TestFuckup {
        for (String word : ReservedNoUseKeywords.get())
            if (word.equalsIgnoreCase(scope.getName()))
                throw new TestFuckup("keyword " + word.toUpperCase() + " is reserved");

        for (TuqanField field : scope.getFields())
            for (String word : ReservedNoUseKeywords.get())
                if (word.equalsIgnoreCase(field.getName()))
                    throw new TestFuckup("keyword " + word.toUpperCase() + " in " + scope.getName() + " is reserved");
    }

    private void testNameUniqueness(TuqanScopeclass scope) throws TestFuckup {
        List<String> known = new ArrayList<>();
        for (TuqanField field : scope.getFields()) {
            for (String s : known) 
            if (s.equalsIgnoreCase(field.getName())) {
                throw new TestFuckup("multiple declaration of " + field.getName() + " in " + scope.getName());
            }
            known.add(field.getName());
        }
    }

}
