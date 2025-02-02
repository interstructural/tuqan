
package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaScope;

import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.generators.literals.java.JavaField;

public class JavaSpringServiceImplOutputGenerator extends JavaOutputGenerator {

    public JavaSpringServiceImplOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("@Service");
        text.add("public class " + scope.spring().service().implementationName()
                + " implements " +  scope.spring().service().interfaceName() + " {");
        text.pushIndent();        
        text.add("");
        text.add("private static final int BATCH_SIZE = 50;");
        text.add("");
        text.add("@Autowired");
        text.add("private " + scope.spring().repositoryName() + " repository;");
        text.add("");
        text.add("@PersistenceContext");
        text.add("private EntityManager entityManager;");        
        addMethodCreate(text, scope);
        
        if (!scope.asTuqanObject().isLedger()) {
            addMethodDelete(text, scope);
            addMethodUpdate(text, scope);
        }
        text.add("");
        text.popIndent();
        text.add("} // end of class");

        File filename = getFileFor(scope.spring().service().implementationName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        text.add("package " + new PackageLocationResolver(params).getRootPacakge() + ".service;");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope scope) {
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".model." + scope.spring().modelName() + ";");
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".repository." + scope.spring().repositoryName() + ";");
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
        new ImportOrganizer().getSpringServerImports().forEach(i -> text.add("import " + i + ";"));
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

    private void addMethodCreate(IndentedStringList text, JavaScope scope) {
        text.add("@Transactional");
        text.add("@Override public void " + scope.spring().service().methodBulkCreateName() + "(final " + scope.spring().modelListName() + " entities) {");
        text.pushIndent();
        text.add("");
        text.add("int batchSize = BATCH_SIZE;");
        text.add("for (int i = 0; i < entities.size(); i++) {");
        text.pushIndent();
        text.add("entityManager.merge(entities.get(i));");
        text.add("if (i % batchSize == 0 && i > 0) {");
        text.pushIndent();
        text.add("entityManager.flush();");
        text.add("entityManager.clear();");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        text.add("entityManager.flush();");
        text.add("entityManager.clear();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addMethodDelete(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        String idListType = "List<" + new JavaField(TuqanField.FIELD_ID).genericArgumentType() + ">";
        text.add("@Transactional");
        text.add("@Override public void " + scope.spring().service().methodBulkRemoveName() + "(final " + idListType + " ids) {");
        text.pushIndent();
        text.add("repository.deleteByIdIn(ids);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addMethodUpdate(IndentedStringList text, JavaScope scope) {
        text.add("@Transactional");
        text.add("@Override public void " + scope.spring().service().methodBulkUpdateName() + "(final "+ scope.spring().modelListName() +" entities) {");
        text.pushIndent();
        text.add("");
        text.add("int batchSize = BATCH_SIZE;");
        text.add("for (int i = 0; i < entities.size(); i++) {");
        text.pushIndent();
        text.add("entityManager.merge(entities.get(i));");
        text.add("if (i % batchSize == 0 && i > 0) {");
        text.pushIndent();
        text.add("entityManager.flush();");
        text.add("entityManager.clear();");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        text.add("entityManager.flush();");
        text.add("entityManager.clear();");
        text.popIndent();
        text.add("}");
        text.add("");
    }
    

} //end of class
