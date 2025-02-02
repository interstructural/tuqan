package pl.zenit.tuqan.generators.dto.swift;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.SwiftField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class SwiftDtoOutputGenerator extends OutputGenerator {

    public SwiftDtoOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("import Foundation");
        text.add("");
        text.add("struct " + scope.dtoName() + " {");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".swift");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(SwiftField::new)
            .stream()
            .map(SwiftField::new)
            .map(f -> {
                try {
                    return "var " + f.name() + ": " + f.type();
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
        File swiftDir = new File(dtoDir, "swift");
        return new File(swiftDir, filename);
    }
}
