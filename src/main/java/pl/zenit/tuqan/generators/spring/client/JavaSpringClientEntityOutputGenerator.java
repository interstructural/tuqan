package pl.zenit.tuqan.generators.spring.client;

import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import pl.zenit.tuqan.util.Upcaster;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class JavaSpringClientEntityOutputGenerator extends JavaOutputGenerator {

    public JavaSpringClientEntityOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanScopeclass ) {
            output(new JavaScope(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }

    }

    private void output(JavaScope scope) throws GenerationFuckup {

        text.clear();
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        text.add("public class " + scope.dtoName() + " {");
        text.pushIndent();
        text.add("");
        addFields(text, scope);
        addGettersAndSetters(text, scope);
        text.popIndent();
        text.add("} // end of class");

        File filename = getFileFor(scope.dtoName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        text.add("package " + pkg + ";");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope scope) {
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));        
        text.add("");
    }

    private String findTypeName(JavaField field) throws GenerationFuckup {
        if ( field.asTuqanField().getType().getDataType().equals(DataType.LINK) 
            || field.asTuqanField().getType().getDataType().equals(DataType.CHILD)) {
            String targetType = field.asTuqanField().getType().getInfo().getTargetName();
            TuqanScopeclass targetClass = field.asTuqanField().getContext().findScope(targetType);
            return new JavaScope(targetClass).dtoName();
        }
        else {
            return field.type();
        }
    }

    private void addFields(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        for ( JavaField field : scope.fields() ) {
            text.add("private " + findTypeName(field) + " " + field.name() + ";");
        }
        text.add("");
    }

    private void addGettersAndSetters(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        for ( JavaField field : scope.fields() ) {
            String typeName = findTypeName(field);
            text.add("public " + typeName + " " + field.methodGetterName() + "() { return " + field.name() + "; }");
            text.add("public void " + field.methodSetterName() + "(" + typeName + " " + field.name() + ") { this."
                           + field.name() + " = " + field.name() + "; }");            
        }
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File clientDir = plr.getSpringDesktopClientProjectRoot(overallRoot);
        clientDir = plr.getSourceCodeAppRootDir(clientDir);
        clientDir = new File(clientDir, "dao");
        return new File(clientDir, filename);
    }
} //end of class 