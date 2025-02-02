package pl.zenit.tuqan.generators.spring.phpclient;

import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.php.PhpScope;

public class PhpClientDaoOutputGenerator extends OutputGenerator {

    public PhpClientDaoOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanScopeclass) {
            output(new PhpScope(new Upcaster(data).asInferred()));
        } else {
            throw new GenerationFuckup("unknown operation");
        }
    }

    private void output(PhpScope scope) throws GenerationFuckup {
        text.clear();
        text.add("<?php");
        text.add("");
        addPhpClassDeclaration(text, scope);
        addPhpFields(text, scope);
        addPhpFunctions(text, scope);
        
        text.popIndent();
        text.add("} // end of class");
        text.add("");
        text.add("?>");

        File filename = getFileFor(scope.spring().daoName() + ".php");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPhpClassDeclaration(IndentedStringList text, PhpScope scope) {
        text.add("class " + scope.spring().daoName() + " {");
        text.pushIndent();
        text.add("");
    }

    private void addPhpFields(IndentedStringList text, PhpScope scope) {
        text.add("private $baseUrl = Endpoints::main . '/" + scope.spring().baseRequestUrlName() + "';");
        text.add("");
    }

    private void addPhpFunctions(IndentedStringList text, PhpScope scope) {
        boolean ledger = scope.asTuqanObject().isLedger();
        boolean mirage = scope.asTuqanObject().isMirage();

        addPhpFunctionCount(text, scope);
        addPhpFunctionGetAll(text, scope);
        addPhpFunctionGet(text, scope);

        if (!mirage) {
            addPhpFunctionAdd(text, scope);
            addPhpFunctionAddAll(text, scope);
        }
        if (!ledger && !mirage) {
            addPhpFunctionUpdate(text, scope);
            addPhpFunctionUpdateAll(text, scope);
            addPhpFunctionDelete(text, scope);
            addPhpFunctionDeleteAll(text, scope);
        }
    }

    private void addPhpFunctionCount(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodCount() + "() {");
        text.pushIndent();
        text.add("$response = file_get_contents($this->baseUrl . '/count');");
        text.add("$count = json_decode($response, true);");
        text.add("return $count;");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionGetAll(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodGetAll() + "() {");
        text.pushIndent();
        text.add("$response = file_get_contents($this->baseUrl);");
        text.add("$json = json_decode($response, true);");
		text.add("$output = array_map(fn($item) => new "+ scope.dtoName() + "($item), $json);");
		text.add("return $output;");
        text.add("");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionGet(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodGet() + "($id) {");
        text.pushIndent();
        text.add("$response = file_get_contents($this->baseUrl . '/' . $id);");
        text.add("$json = json_decode($response, true);");
        text.add("return new " + scope.dtoName() + "($json);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionAdd(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodAdd() + "($item) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'header'  => 'Content-type: application/json',");
        text.add("'method'  => 'POST',");
        text.add("'content' => json_encode($item),");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("$response = file_get_contents($this->baseUrl, false, $context);");
        text.add("return json_decode($response, true);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionDelete(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodDelete() + "($id) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'method'  => 'DELETE',");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("file_get_contents($this->baseUrl . '/' . $id, false, $context);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionAddAll(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodAddAll() + "($items) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'header'  => 'Content-type: application/json',");
        text.add("'method'  => 'POST',");
        text.add("'content' => json_encode($items),");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("$response = file_get_contents($this->baseUrl . '/all', false, $context);");
        text.add("return json_decode($response, true);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionDeleteAll(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodDeleteAll() + "($ids) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'header'  => 'Content-type: application/json',");
        text.add("'method'  => 'DELETE',");
        text.add("'content' => json_encode($ids),");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("file_get_contents($this->baseUrl . '/all', false, $context);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionUpdate(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodUpdate() + "($id, $item) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'header'  => 'Content-type: application/json',");
        text.add("'method'  => 'PUT',");
        text.add("'content' => json_encode($item),");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("$response = file_get_contents($this->baseUrl . '/' . $id, false, $context);");
        text.add("return json_decode($response, true);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addPhpFunctionUpdateAll(IndentedStringList text, PhpScope scope) {
        text.add("public function " + scope.rest().methodUpdateAll() + "($items) {");
        text.pushIndent();
        text.add("$options = [");
        text.pushIndent();
        text.add("'http' => [");
        text.pushIndent();
        text.add("'header'  => 'Content-type: application/json',");
        text.add("'method'  => 'PUT',");
        text.add("'content' => json_encode($items),");
        text.popIndent();
        text.add("],");
        text.popIndent();
        text.add("];");
        text.add("$context  = stream_context_create($options);");
        text.add("$response = file_get_contents($this->baseUrl . '/all', false, $context);");
        text.add("return json_decode($response, true);");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File phpDir = plr.getSpringPhpClientProjectRoot(overallRoot);
        return new File(phpDir, filename);
    }
}
