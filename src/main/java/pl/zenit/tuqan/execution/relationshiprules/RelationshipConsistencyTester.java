package pl.zenit.tuqan.execution.relationshiprules;

import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public abstract class RelationshipConsistencyTester {

    protected final TuqanContext context;

    public RelationshipConsistencyTester(TuqanContext context) {
        this.context = context;
    }

    public void test(TuqanScopeclass sourceScope) throws TestFuckup {
        for (TuqanField f : sourceScope.getFields()) {
            if (f.getType().isRelation()) {
                String targetScopeName = f.getType().getInfo().getTargetName();
                TuqanScopeclass targetScope = context.findScope(targetScopeName);
                if (targetScope == null) {
                    throw new TestFuckup(targetScopeName + " not found in this context (definied in " + sourceScope.getName() + ")");
                }
                else {
                    testRelation(sourceScope, f, targetScope);
                }
            }
        }
    }

    protected abstract void testRelation(TuqanScopeclass source, TuqanField relation, TuqanScopeclass target) throws TestFuckup;

}
