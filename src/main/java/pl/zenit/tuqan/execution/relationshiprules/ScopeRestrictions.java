package pl.zenit.tuqan.execution.relationshiprules;

import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class ScopeRestrictions extends RelationshipConsistencyTester {

    public ScopeRestrictions(TuqanContext context) {
        super(context);
    }

    @Override
    protected void testRelation(TuqanScopeclass source, TuqanField relation, TuqanScopeclass target) throws TestFuckup {
        if (source.isLedger() || source.isMirage())
            return;

        DataType relationType = relation.getType().getDataType();
        switch (relationType) {
            case CHILD:
            case CHILDREN:
                if (target.isLedger() || target.isMirage()) {
                    throw new TestFuckup("scope " + source.getName() + "cannot contain non-scope (" + relation.getName() + ")");
                }
            case LINK:
            case LIST:
                if (target.isMirage()) {
                    throw new TestFuckup("scope " + source.getName() + "cannot refer mirage (" + relation.getName() + ")");
                }
        }
    }
}
