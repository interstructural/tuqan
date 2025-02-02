package pl.zenit.tuqan.generators.dto.go;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.GoField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class GoDtoOutputGenerator extends OutputGenerator {

    public GoDtoOutputGenerator(TuqanExecutionParameters params) {
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
        addPackageDeclaration(text);
        text.add("");
        addImports(text);
        text.add("");
        text.add("type " + scope.dtoName() + " struct {");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".go");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text) {
        text.add("package main");
    }

    private void addImports(IndentedStringList text) {
        text.add("import (");
        text.pushIndent();
        text.add("\"fmt\"");
        text.popIndent();
        text.add(")");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(GoField::new)
            .stream()
            .map(GoField::new)
            .map(f -> {
                try {
                    return f.name() + " " + f.type();
                } catch (GenerationFuckup e) {
                    throw new RuntimeException(e);
                }
            })
            .forEachOrdered(text::add);
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File goDir = new File(dtoDir, DtoDirNames.go);
        return new File(goDir, filename);
    }
}
