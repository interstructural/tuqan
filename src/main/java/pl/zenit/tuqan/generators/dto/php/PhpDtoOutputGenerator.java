package pl.zenit.tuqan.generators.dto.php;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.php.PhpField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class PhpDtoOutputGenerator extends OutputGenerator {

    public PhpDtoOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanScopeclass)
            outputScope(new DefaultScope(new Upcaster(data).asInferred()));
        else
            throw new GenerationFuckup("unknown operation");
    }

    private void outputScope(DefaultScope scope) throws GenerationFuckup {
        text.clear();
        addPhpTag(text);
        text.add("");
        text.add("class " + scope.dtoName() + " {");
        text.pushIndent();
        addFields(text, scope);
        text.add("");
        addGettersAndSetters(text, scope);
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".php");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPhpTag(IndentedStringList text) {
        text.add("<?php");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(PhpField::new)
            .stream()
            .map(PhpField::new)
            .map(f -> {
                return "private $" + f.name() + ";";
            })
            .forEachOrdered(text::add);
        text.add("");
    }

    private void addGettersAndSetters(IndentedStringList text, DefaultScope scope) {
        scope.fields(PhpField::new)
            .forEach(f -> {
                String capitalized = capitalize(f.name());
                text.add("public function get" + capitalized + "() {");
                text.pushIndent();
                text.add("return $this->" + f.name() + ";");
                text.popIndent();
                text.add("}");
                text.add("");
                text.add("public function set" + capitalized + "($" + f.name() + ") {");
                text.pushIndent();
                text.add("$this->" + f.name() + " = $" + f.name() + ";");
                text.popIndent();
                text.add("}");
                text.add("");
            });
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File phpDir = new File(dtoDir, DtoDirNames.php);
        return new File(phpDir, filename);
    }
}
