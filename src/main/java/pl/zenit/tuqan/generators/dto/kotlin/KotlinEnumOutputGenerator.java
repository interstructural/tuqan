package pl.zenit.tuqan.generators.dto.kotlin;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.util.Upcaster;

import java.io.File;
import java.util.List;

public class KotlinEnumOutputGenerator extends OutputGenerator {

    public KotlinEnumOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanEnumclass) {
            output(new DefaultEnum(new Upcaster(data).asInferred()));
        } else {
            throw new GenerationFuckup("unknown operation");
        }
    }

    private void output(DefaultEnum enumData) throws GenerationFuckup {
        IndentedStringList fileText = new IndentedStringList(this.params.getBasic().getIndentSize());

        addPackageDeclaration(fileText, enumData);
        String enumName = enumData.name();
        addEnumDefinition(fileText, enumData);

        File kotlinFile = getFileFor(enumName + ".kt");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), kotlinFile.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, DefaultEnum enumData) {
        text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
        text.add("");
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("enum class " + enumData.name() + " {");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (int i = 0; i < enumValues.size(); i++) {
            text.add(enumValues.get(i) + (i < enumValues.size() - 1 ? "," : ""));
        }

        text.popIndent();
        text.add("}");
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.kotlin);
        return new File(targetDir, filename);
    }
}
