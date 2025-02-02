package pl.zenit.tuqan.generators.dto.haxe;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.HaxeField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class HaxeDtoOutputGenerator extends OutputGenerator {

    public HaxeDtoOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("class " + scope.dtoName() + " {");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".hx");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text) {
        text.add("package "
                + params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name())
                + "."
                + params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name())
                + ";");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(HaxeField::new)
            .stream()
            .map(HaxeField::new)
            .map(f -> {
                try {
                    return "public var " + f.name() + ":" + f.type() + ";";
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
        File haxeDir = new File(dtoDir, DtoDirNames.haxe);
        return new File(haxeDir, filename);
    }
}
