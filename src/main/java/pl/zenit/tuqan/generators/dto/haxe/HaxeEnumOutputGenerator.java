package pl.zenit.tuqan.generators.dto.haxe;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.util.Upcaster;

import java.io.File;
import java.util.List;

public class HaxeEnumOutputGenerator extends OutputGenerator {

    public HaxeEnumOutputGenerator(TuqanExecutionParameters params) {
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

        String enumName = enumData.name();
        addPackageDeclaration(fileText);
        text.add("");
        addEnumDefinition(fileText, enumData);

        File haxeFile = getFileFor(enumName + ".hx");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), haxeFile.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text) {
        text.add("package "
                + params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name())
                + "."
                + params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name())
                + ";");
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("enum " + enumData.name() + " {");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (String value : enumValues) {
            text.add(value + ";");
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
        File targetDir = new File(dtoDir, DtoDirNames.haxe);
        return new File(targetDir, filename);
    }
}
