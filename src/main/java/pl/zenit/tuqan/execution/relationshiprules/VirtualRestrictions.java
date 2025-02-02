package pl.zenit.tuqan.execution.relationshiprules;

import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class VirtualRestrictions extends RelationshipConsistencyTester {

    public VirtualRestrictions(TuqanContext context) {
        super(context);
    }

    @Override
    protected void testRelation(TuqanScopeclass source, TuqanField relation, TuqanScopeclass target) throws TestFuckup {
        if (target.isVirtual())
            throw new TestFuckup(source.getName() + " has virtual field " + relation.getName());
    }
}
