package pl.zenit.tuqan.generators.dto.java;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;

import pl.zenit.tuqan.util.Coalescer;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class JavaPojoOutputGenerator extends JavaOutputGenerator {

    public JavaPojoOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanScopeclass)
            outputScope(new JavaScope(new Upcaster(data).asInferred()));
        else
            throw new GenerationFuckup("unknown operation");
    }

    private void outputScope(JavaScope scope) throws GenerationFuckup {
        text.clear();
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        text.add("public class " + scope.dtoName() + " {");
        text.pushIndent();
        text.add("");
        addFields(text, scope);
        addDefaultConstructor(text, scope);
        addGetters(text, scope);
        addSetters(text, scope);
        text.popIndent();
        text.add("} // end of class");

        File filename = getFileFor(scope.dtoName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope javaScope) {
        text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope javaScope) {
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
        text.add("");
    }

    private void addFields(IndentedStringList text, JavaScope javaScope) {
        javaScope.fields()
            .stream()
            .map(FailsafeJavaFieldWrapper::new)
            .map(f -> f.type() + " " + f.name())
            .map(l -> "private " + l + ";")
            .forEachOrdered(text::add);
        text.add("");
    }

    private void addDefaultConstructor(IndentedStringList text, JavaScope javaScope) {
        text.add("public " + javaScope.dtoName() + "() {");
        text.add("}");
        text.add("");
    }

    private void addGetters(IndentedStringList text, JavaScope javaScope) throws GenerationFuckup {
        for (int i = 0; i < javaScope.fields().size(); ++i) {
            JavaField f = javaScope.fields().get(i);
            text.add("public " + f.type() + " " + f.methodGetterName() + "() {");
            text.pushIndent();
            text.add("return " + f.name() + ";");
            text.popIndent();
            text.add("}");
            text.add("");
        }
    }

    private void addSetters(IndentedStringList text, JavaScope javaScope) throws GenerationFuckup {
        for (int i = 0; i < javaScope.fields().size(); ++i) {
            JavaField f = javaScope.fields().get(i);
            text.add("public void " + f.methodSetterWithName() + "(" + f.type() + " " + f.name() + ") {");
            text.pushIndent();
            text.add("this." + f.name() + " = " + f.name() + ";");
            text.popIndent();
            text.add("}");
            text.add("");
        }
    }

    private class FailsafeJavaFieldWrapper {

        private final JavaField field;

        public FailsafeJavaFieldWrapper(JavaField field) {
            this.field = field;
        }

        public String name() {
            return field.name();
        }

        public String type() {
            return Coalescer.coalesce(() -> field.type(), "TYPE-ERROR");
        }
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File javaDir = new File(dtoDir, DtoDirNames.java);
        return new File(javaDir, filename);
    }
}
