package pl.zenit.tuqan.generators.literals.simple;

import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.generators.literals.DefaultField;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public class SwiftField extends DefaultField {

    public SwiftField(TuqanFieldBroker f) {
        super(f);
    }

    public String type() throws GenerationFuckup {
        switch (f.getType().getDataType()) {
            default:
            case NONE:
                return "WRONGTYPE";
            case BOOL:
                return "Bool";
            case INT:
                return "Int";
            case LONG:
                return "Int64";
            case FLOAT:
                return "Double";
            case STRING:
                return "String";
            case BINARY:
                return "[UInt8]";
            case ENUM:
                TuqanEnumclass enumObject = f.getContext().findEnum(f.getType().getInfo().getTargetName());
                if (enumObject == null) throw new GenerationFuckup("empty enum type info");
                return new DefaultEnum(enumObject).name();
            case LINK:
            case CHILD:
                TuqanScopeclass target1 = f.getContext().findScope(f.getType().getInfo().getTargetName());
                if (target1 == null) throw new GenerationFuckup("empty link type info");
                return new DefaultScope(target1).dtoName();
            case LIST:
            case CHILDREN:
                TuqanScopeclass target2 = f.getContext().findScope(f.getType().getInfo().getTargetName());
                if (target2 == null) throw new GenerationFuckup("empty list type info");
                return "[" + new DefaultScope(target2).dtoName() + "]";
        }
    }
}
