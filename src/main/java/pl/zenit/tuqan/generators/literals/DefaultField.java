package pl.zenit.tuqan.generators.literals;

import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public abstract class DefaultField implements TuqanFieldBroker {

    protected final TuqanField f;

    public DefaultField(TuqanFieldBroker f) {
        this.f = f.asTuqanField();
    }

    public abstract String type() throws GenerationFuckup;

    @Override public TuqanField asTuqanField() {
        return f;
    }

    public String name() {
        return CodeFormatting.firstWordSmallLetter(f.getName());
    }

    public String methodGetterName() {
        String prefix = "get";
        if (f.getType().getDataType() == DataType.BOOL)
            prefix = "is";
        return prefix + CodeFormatting.firstWordLargeLetter(f.getName());
    }

    public String methodSetterName() {
        return "set" + CodeFormatting.firstWordLargeLetter(f.getName());
    }

    public String methodSetterWithName() {
        return "with" + CodeFormatting.firstWordLargeLetter(f.getName());
    }

    public String builderMethodSetterName() {
        return name();
    }

}
