package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.Questionable;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import pl.zenit.tuqan.generators.literals.java.JavaScope;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.DataType;

public class JavaSpringControllerOutputGenerator extends JavaOutputGenerator {

    public JavaSpringControllerOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }
    
    @Override public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanScopeclass)
            output(new JavaScope(new Upcaster(data).asInferred()));
        else
            throw new GenerationFuckup("unknown operation");

    }
    private void output(JavaScope scope) throws GenerationFuckup {
        Map<JavaField, JavaScope> listScopes = getListFieldTargetScopes(scope);

        addPackageDeclaration(text, scope);
        addImports(text, scope, new ArrayList<>(listScopes.values()));

        if (params.getEnviourment().isUseCustomAccessControl()) 
            text.add("@AdminAccess");
        
        text.add("@RestController");
        text.add("@RequestMapping(\""+ scope.spring().baseRequestUrlName() +"\")");
        text.add("public class " + scope.spring().controllerName() + " {");
        text.add("");
        text.pushIndent(); 
        
        addAutowiredStuff(text, scope, new ArrayList<>(listScopes.values()));

        //these are assumed to be always wanted... unless they aint
        addCount(text, scope);
        addDisplay(text, scope);

        boolean ledger = !scope.asTuqanObject().isLedger();
        boolean mirage = !scope.asTuqanObject().isMirage();

        if (!mirage) {
            addSingleCreate(text, scope);
            addBulkCreate(text, scope);
        }
        if (!ledger && !mirage) {
            addSingleUpdate(text, scope);
            addBulkUpdate(text, scope);
            addSingleRemove(text, scope);
            addBulkRemove(text, scope);
        }

        for (var entry : listScopes.entrySet()) {
            //addChildsFunctions(text, scope, entry.getKey(), entry.getValue());
        }

        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.spring().controllerName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private Map<JavaField, JavaScope> getListFieldTargetScopes(JavaScope scope) {
        Map<JavaField, JavaScope> listMap = new HashMap<>();

        TuqanContext context = scope.asTuqanObject().getContext();
        for (JavaField f : scope.fields())
        if (f.asTuqanField().getType().getDataType() == DataType.LIST) {
            TuqanScopeclass target = context.findScope(f.asTuqanField().getType().getInfo().getTargetName());
            JavaScope childScope = new JavaScope(target);
            listMap.put(f, childScope);
        }
        return listMap;
    }
    
    private void addPackageDeclaration(IndentedStringList text, JavaScope javaScope) {
        text.add("package " + new PackageLocationResolver(params).getRootPacakge() + ".controller;");
        text.add("");
    }
    private void addImports(IndentedStringList text, JavaScope javaScope, List<JavaScope> listTargetScopes) {
        new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
        new ImportOrganizer().getSpringServerImports().forEach(i-> text.add("import " + i + ";"));
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".model." + javaScope.spring().modelName() + ";");
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".repository." + javaScope.spring().repositoryName() + ";");
        text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".service." + javaScope.spring().service().interfaceName() + ";");

        for (JavaScope childScope : listTargetScopes) {
            text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".model." + childScope.spring().modelName() + ";");
            text.add("import " + new PackageLocationResolver(params).getRootPacakge() + ".repository." + childScope.spring().repositoryName() + ";");
        }

        if (params.getEnviourment().isUseCustomAccessControl()) {
            String rootpkg = new PackageLocationResolver(params).getRootPacakge();
            new ImportOrganizer().getSpringClientCustomAccessImports(rootpkg).forEach(i-> text.add("import " + i + ";"));
        }            
        
        text.add("");
    }
    private void addAutowiredStuff(IndentedStringList text, JavaScope scope, List<JavaScope> listScopes) {
        if (!scope.asTuqanObject().isMirage()) {
            text.add("@Autowired");
            text.add("private " + scope.spring().repositoryName() + " " + scope.spring().repositoryVarName() + ";");
            listScopes.forEach(c -> text.add("private " + c.spring().repositoryName() + " " + c.spring().repositoryVarName() + ";"));
            text.add("");
            text.add("@Autowired");
            text.add("private " + scope.spring().service().interfaceName() + " " + scope.spring().service().entityName() + ";");
            text.add("");
        }
    }

    private void addCount(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        text.add("@GetMapping(\"count\")");
        text.add("public long getCount() {");
        text.pushIndent();
        text.add("System.out.println(\"" + scope.spring().modelName() + " count\");");
        text.add("return "+ scope.spring().repositoryVarName() +".count();");
        text.popIndent();
        text.add("}");
        text.add("");
    }
    private void addDisplay(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        //GET
        String entityName = scope.spring().modelName();
        String primaryKeyType = new JavaField(TuqanField.FIELD_ID).type();
        
        // GET ALL
        text.add("@GetMapping");
        text.add("public " + scope.spring().modelListName() + " getAll() {");
        text.pushIndent();
        text.add("System.out.println(\""+ entityName + " getAll\");");
        text.add("return " + scope.spring().repositoryVarName() + ".findAll();");
        text.popIndent();
        text.add("}");
        text.add("");

        // GET by ID
        text.add("@GetMapping(\"/{id}\")");
        text.add("public ResponseEntity<" + entityName + "> getById(@PathVariable " + primaryKeyType + " id) {");
        text.pushIndent();
        text.add("System.out.println(\""+ entityName + " get\");");
        text.add("Optional<" + entityName + "> entity = "+ scope.spring().repositoryVarName() +".findById(id);");
        text.add("if (entity.isPresent()) {");
        text.pushIndent();
        text.add("return ResponseEntity.ok(entity.get());");
        text.popIndent();
        text.add("} else {");
        text.pushIndent();
        text.add("return ResponseEntity.notFound().build();");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        text.add("");

        //get latest
        text.add("@GetMapping(\"/latest\")");
        text.add("public ResponseEntity<"+ scope.spring().modelName() +"> getLastAddedObject() {");
            text.add("Pageable pageable = PageRequest.of(0, 1);");
            text.add("List<"+ scope.spring().modelName() +"> result = " + scope.spring().repositoryVarName() + ".findByOrderByIdDesc(pageable);");
            text.add("return result.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(result.get(0));");
        text.add("}");
        text.add("");
    }
    private void addBulkCreate(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        //POST
        String entityName = scope.spring().modelName();
        text.add("@PostMapping(\"/all\")");
        text.add("public ResponseEntity<Void> createAll(@RequestBody " + scope.spring().modelListName() + " entities) {");
        text.pushIndent();
        text.add(scope.spring().service().entityName() + "." + scope.spring().service().methodBulkCreateName() + "(entities);");
        text.add("return ResponseEntity.ok().build();");
        text.popIndent();
        text.add("}");
    }
    private void addSingleCreate(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        //POST
        String entityName = scope.spring().modelName();
        text.add("@PostMapping");
        text.add("public ResponseEntity<" + entityName + "> create(@RequestBody " + entityName + " entity) {");
        text.pushIndent();
        text.add("System.out.println(\""+ entityName + " create\");");
        text.add(entityName + " savedEntity = "+ scope.spring().repositoryVarName() +".save(entity);");
        text.add("return ResponseEntity.ok(savedEntity);");
        text.popIndent();
        text.add("}");
    }
    private void addBulkRemove(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        String primaryKeyGenericType = new JavaField(TuqanField.FIELD_ID).genericArgumentType();
        text.add("@DeleteMapping(\"/all\")");
        text.add("public ResponseEntity<Void> delete(@RequestParam List<" + primaryKeyGenericType + "> ids) {");
        text.pushIndent();        
        text.add(scope.spring().service().entityName() + "." + scope.spring().service().methodBulkRemoveName() + "(ids);");
        text.add("return ResponseEntity.ok().build();");
        text.popIndent();
        text.add("}");
        text.add("");
    }
    private void addSingleRemove(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        String entityName = scope.spring().modelName();
        text.add("@DeleteMapping(\"/{id}\")");
        text.add("public ResponseEntity<Void> delete(@PathVariable " + new JavaField(TuqanField.FIELD_ID).type() + " id) {");
        text.pushIndent();
        text.add("System.out.println(\""+ entityName + " delete\");");
        text.add("if ("+ scope.spring().repositoryVarName() +".existsById(id)) {");
        text.pushIndent();
        text.add(scope.spring().repositoryVarName() + ".deleteById(id);");
        text.add("return ResponseEntity.ok().build();");
        text.popIndent();
        text.add("} else {");
        text.pushIndent();
        text.add("return ResponseEntity.notFound().build();");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
    }        
    private void addSingleUpdate(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        //PUT
        String entityName = scope.spring().modelName();
        String primaryKeyType = new JavaField(TuqanField.FIELD_ID).type();

        text.add("@PutMapping(\"/{id}\")");
        text.add("public ResponseEntity<Void> update(@PathVariable " + primaryKeyType + " id, @RequestBody " + entityName + " entityDetails) {");
        text.pushIndent();
        text.add("System.out.println(\"" + entityName + " update\");");
        text.add("entityDetails.setId(id);");
        text.add(scope.spring().service().entityName() + "." + scope.spring().service().methodBulkUpdateName() + "(Arrays.asList(entityDetails));");
        text.add("return ResponseEntity.ok().build();");
        text.popIndent();
        text.add("}");
    }
    private void addBulkUpdate(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        //PUT
        text.add("@PutMapping(\"/all\")");
        text.add("public ResponseEntity<Void> updateAll(@RequestBody " + scope.spring().modelListName() + " entities) {");
        text.pushIndent();
        text.add(scope.spring().service().entityName() + "." + scope.spring().service().methodBulkUpdateName() + "(entities);");
        text.add("return ResponseEntity.ok().build();");
        text.popIndent();
        text.add("}");        
    }
    
    @Override protected File getFileFor(String filename) {
        String targetFolder = "controller";
        
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File serverDir = plr.getSpringServerProjectRoot(overallRoot);
        File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
        File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }

    /** this should not be generated unless someone writes a proper hql for LIST children crud */
    @Questionable
    private void addChildsFunctions(IndentedStringList text, JavaScope scope, JavaField child, JavaScope childScope) throws GenerationFuckup {
        String path = childScope.spring().childRequestUrlName();
        String childType = childScope.spring().modelName();

        //GET ALL
        text.add("@GetMapping(\"/" + path + "/{parentid}\")");
        text.add("public " + childScope.spring().modelListName() + " " + childScope.rest().methodChildrenGetAll() +
            "(@PathVariable " + new JavaField(TuqanField.FIELD_ID).type() + " parentid) {");
        text.pushIndent();
        text.add("System.out.println(\""+ scope.spring().modelName() + ": " + childType + " getAll\");");
        text.add("return " + scope.spring().repositoryVarName() + ".findById(parentid).get()." + child.methodGetterName() + "();");
        text.popIndent();
        text.add("}");

        //POST 
        text.add("@PostMapping(\"/" + path + "/{parentid}\")");
        text.add("public ResponseEntity<" + childType + "> " + childScope.rest().methodChildrenCreate()+ "(@RequestBody " + childType + " entity) {");
        text.pushIndent();
        text.add("System.out.println(\""+ scope.spring().modelName() + ": " + childType + " create\");");
        text.add(childType + " savedEntity = " + childScope.spring().repositoryVarName() + ".save(entity);");
        //
        
        text.add("return ResponseEntity.ok(savedEntity);");        
        text.popIndent();
        text.add("}");
        text.add("");

        if (!scope.asTuqanObject().isLedger()) {
            //DELETE (
            text.add("@DeleteMapping(\"/" + path + "/{parentid}/{childid}\")");
            text.add("public ResponseEntity<Void> "+ childScope.rest().methodChildrenDelete() +
                    "(@PathVariable " + new JavaField(TuqanField.FIELD_ID).type() + " parentid, "
                    + "@PathVariable " + new JavaField(TuqanField.FIELD_ID).type() + " childid) {");
            text.pushIndent();

            text.add("System.out.println(\""+ childType + " delete\");");
            text.add("if ("+ childScope.spring().repositoryVarName() +".existsById(childid)) {");
            text.pushIndent();
            text.add("System.out.println(\""+ scope.spring().modelName() + ": " + childType + " delete\");");
            text.add("System.out.println(\"this doesnt work\");");
            text.add("return ResponseEntity.ok().build();");
            text.popIndent();
            text.add("} else {");
            text.pushIndent();
            text.add("return ResponseEntity.notFound().build();");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("}");
        }
    }

}
