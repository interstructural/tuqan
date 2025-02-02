package pl.zenit.tuqan.execution.relationshiprules;

import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class MirageRestrictions extends RelationshipConsistencyTester {

    public MirageRestrictions(TuqanContext context) {
        super(context);
    }

    @Override
    protected void testRelation(TuqanScopeclass source, TuqanField relation, TuqanScopeclass target) throws TestFuckup {
        if (!source.isMirage())
            return;

        DataType relationType = relation.getType().getDataType();
        switch (relationType) {
            case CHILD:
            case CHILDREN:
                throw new TestFuckup("mirage " + source.getName() + "cannot contain (" + relation.getName() + ")");
            case LIST:
            case LINK:
                if (target.isMirage())
                    throw new TestFuckup("mirage " + source.getName() + "cannot refer to mirage (" + relation.getName() + ")");
        }
    }

}
