package pl.zenit.tuqan.generators.dto.kotlin;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.KotlinField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class KotlinDtoOutputGenerator extends OutputGenerator {

    public KotlinDtoOutputGenerator(TuqanExecutionParameters params) {
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
        addPackageDeclaration(text, scope);
        addImports(text);
        text.add("");
        text.add("data class " + scope.dtoName() + " (");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add(")");

        File filename = getFileFor(scope.dtoName() + ".kt");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, DefaultScope scope) {
        text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
        text.add("");
    }

    private void addImports(IndentedStringList text) {
        text.add("import java.util.*");
        text.add("");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(KotlinField::new)
            .stream()
            .map(KotlinField::new)
            .map(f -> {
                try {
                    return "val " + f.name() + ": " + f.type() + ",";
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
        File kotlinDir = new File(dtoDir, DtoDirNames.kotlin);
        return new File(kotlinDir, filename);
    }
}
