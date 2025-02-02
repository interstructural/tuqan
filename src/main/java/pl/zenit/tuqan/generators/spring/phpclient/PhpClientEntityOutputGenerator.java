package pl.zenit.tuqan.generators.spring.phpclient;

import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.generators.literals.php.PhpField;
import pl.zenit.tuqan.generators.literals.php.PhpScope;

public class PhpClientEntityOutputGenerator extends OutputGenerator {

    public PhpClientEntityOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("class " + scope.dtoName() + " {");
            text.pushIndent();
            text.add("");
            addConstructor(text, scope);
            addPhpFields(text, scope);
            addPhpGettersAndSetters(text, scope);
            text.popIndent();
        text.add("} // end of class");
        text.add("");
        text.add("?>");

        File filename = getFileFor(scope.dtoName() + ".php");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPhpFields(IndentedStringList text, PhpScope phpScope) throws GenerationFuckup {
        for (PhpField field : phpScope.fields()) {
            text.add("public $" + field.name() + ";");
        }
        text.add("");
    }

    private void addPhpGettersAndSetters(IndentedStringList text, PhpScope phpScope) throws GenerationFuckup {
        for (PhpField field : phpScope.fields()) {
            String fieldName = field.name();
            text.add("public function " + field.methodGetterName() + "() { return $this->" + fieldName + "; }");
            text.add("public function " + field.methodSetterName() + "($" + fieldName + ") { $this->" + fieldName + " = $" + fieldName + "; }");
        }
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
            File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File phpDir = plr.getSpringPhpClientProjectRoot(overallRoot);
        return new File(phpDir, filename);
    }

    private void addConstructor(IndentedStringList text, PhpScope scope) {
        text.add("public function __construct($data) {");
        text.pushIndent();
        for (PhpField field : scope.fields()) {
            text.add("$this -> " + field.name()  + " = $data['" + field.name() + "'] ?? null;");
        }
        text.popIndent();
        text.add("}");
        text.add("");
    }
}
