package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaScope;

import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.generators.literals.java.JavaField;

public class JavaSpringServiceInterfaceOutputGenerator extends JavaOutputGenerator {

    public JavaSpringServiceInterfaceOutputGenerator(TuqanExecutionParameters params) {
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
        if (scope.asTuqanObject().isMirage()) {
            return;
        }

        text.clear();
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        text.add("public interface " + scope.spring().service().interfaceName() + " {");
        text.pushIndent();
        addMethods(text, scope);
        text.popIndent();
        text.add("} // end of interface");

        File filename = getFileFor(scope.spring().service().interfaceName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        text.add("package " + new PackageLocationResolver(params).getRootPacakge() + ".service;");
        text.add("");
    }
    
    private void addImports(IndentedStringList text, JavaScope scope) {
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".model." + scope.spring().modelName() + ";");
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
    }

    private void addMethods(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        String idListType = "List<" + new JavaField(TuqanField.FIELD_ID).genericArgumentType() + ">";        
        text.add("public abstract void " + scope.spring().service().methodBulkCreateName() + "(List<"+ scope.spring().modelName() +"> entities);");
        
        if (!scope.asTuqanObject().isLedger()) {
            text.add("public abstract void " + scope.spring().service().methodBulkRemoveName() + "(" + idListType + " ids);");
            text.add("public abstract void " + scope.spring().service().methodBulkUpdateName() + "(List<"+ scope.spring().modelName() +"> entities);");
        }
        text.add("");
    }

    @Override protected File getFileFor(String filename) {
        String targetFolder = "service";
        
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File serverDir = plr.getSpringServerProjectRoot(overallRoot);
        File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
        File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }

} //end of class JavaOutput
