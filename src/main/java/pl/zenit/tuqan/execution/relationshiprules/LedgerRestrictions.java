package pl.zenit.tuqan.execution.relationshiprules;

import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class LedgerRestrictions extends RelationshipConsistencyTester {

    public LedgerRestrictions(TuqanContext context) {
        super(context);
    }

    @Override
    protected void testRelation(TuqanScopeclass source, TuqanField relation, TuqanScopeclass target) throws TestFuckup {
        if (!source.isLedger())
            return;

        DataType relationType = relation.getType().getDataType();

        switch (relationType) {
            case LINK:
            case LIST:
                throw new TestFuckup("ledger " + source.getName() + "cannot be a source of reference (" + relation.getName() + ")");
            case CHILD:
            case CHILDREN:
                if (target.isMirage())
                    throw new TestFuckup("ledger " + source.getName() + "cannot contain mirage (" + relation.getName() + ")");
        }

    }

}
