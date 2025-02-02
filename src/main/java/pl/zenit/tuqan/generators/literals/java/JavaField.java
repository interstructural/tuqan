package pl.zenit.tuqan.generators.literals.java;

import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public class JavaField implements TuqanFieldBroker {

    private final TuqanField f;

    public JavaField(TuqanFieldBroker f) {
        this.f = f.asTuqanField();
    }

    @Override public TuqanField asTuqanField() {
        return f;
    }

    public String type() throws GenerationFuckup {
        return getLiterals().getType();
    }

    public String name() {
        return CodeFormatting.firstWordSmallLetter(f.getName());
    }

    public JavaLiterals getLiterals() {
        return new JavaLiterals(f);
    }

    public String methodGetterName() {
        String prefix = "get";
        if (f.getType().getDataType() == DataType.BOOL) {
            prefix = "is";
        }

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

    public String genericArgumentType() throws GenerationFuckup {
        return getLiterals().getGenericType();
    }

    public String hibernateForeignKey() {
        return name().toLowerCase() + "_id";
    }

} //end of class JavaField
