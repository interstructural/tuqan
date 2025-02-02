package pl.zenit.tuqan.generators.spring.group;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaEnum;
import java.io.File;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.util.Upcaster;

public abstract class JavaSpringEnumOutputGenerator extends JavaOutputGenerator {

    public JavaSpringEnumOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    protected abstract String getDeclaredPackage();

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanEnumclass ) {
            output(new JavaEnum(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }

    }

    private void output(JavaEnum enumObject) {
        text.clear();
        addPackageDeclaration(text, enumObject);
        text.add("");
        text.add("public enum " + enumObject.name() + " {");
        text.pushIndent();
        text.add("");
        addFields(text, enumObject);
        addMethodSerialize(text, enumObject);
        addMethodUnserialize(text, enumObject);
        text.popIndent();
        text.add("} // end of enum");

        File filename = getFileFor(enumObject.name() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaEnum javaEnum) {
        text.add("package " + getDeclaredPackage() + ";");
        text.add("");
    }

    private void addFields(IndentedStringList text, JavaEnum enumObject) {
        for ( int i = 0; i < enumObject.fields().size(); ++i ) {
            String sign = i == enumObject.fields().size() - 1 ? ";" : ",";
            text.add(enumObject.fields().get(i) + sign);
        }
        text.add("");
    }

    private void addMethodSerialize(IndentedStringList text, JavaEnum e) {
        text.add("public String " + e.serializeMethodName() + "() {");
        text.pushIndent();
        text.add("return this.name();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addMethodUnserialize(IndentedStringList text, JavaEnum e) {
        text.add("public static " + e.name() + " " + e.unserializeMethodName() + "(final String serialized) {");
        text.pushIndent();
        text.add("for (" + e.name() + " val : " + e.name() + ".values())");
        text.add("if (val.name().equalsIgnoreCase(serialized))");
        text.add("return val;");
        text.add("return null;");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    protected abstract File getFileFor(String filename);

} //end of class
