package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class JavaSpringRepositoryOutputGenerator extends JavaOutputGenerator {

    public JavaSpringRepositoryOutputGenerator(TuqanExecutionParameters params) {
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
        String primaryKeyType = new JavaField(TuqanField.FIELD_ID).genericArgumentType();
        if (scope.asTuqanObject().isMirage()) {
            return;
        }

        text.clear();
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        text.add("@Repository");
        text.add("public interface " + scope.spring().repositoryName() + " extends JpaRepository<" + scope.spring().modelName()
                 + ", " + primaryKeyType + "> {");
        text.add("");
        addMethods(text, scope);
        text.add("} // end of class");

        File filename = getFileFor(scope.spring().repositoryName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        text.add("package " + new PackageLocationResolver(params).getRootPacakge() + ".repository;");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope scope) {
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
        new ImportOrganizer().getSpringServerImports().forEach(i -> text.add("import " + i + ";"));
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".model." + scope.spring()
              .modelName() + ";");
        text.add("");
    }
    
    private void addMethods(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        text.add("void deleteByIdIn(List<"+ new JavaField(TuqanField.FIELD_ID).genericArgumentType() +"> ids);");
        text.add("long count();");
        text.add("List<" + scope.spring().modelName() + "> findByOrderByIdAsc(Pageable pageable);");
        text.add("List<" + scope.spring().modelName() + "> findByOrderByIdDesc(Pageable pageable);");
        text.add("");
    }

    @Override protected File getFileFor(String filename) {
        String targetFolder = "repository";

        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File serverDir = plr.getSpringServerProjectRoot(overallRoot);
        File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
        File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }

} //end of class 