package pl.zenit.tuqan.generators.dto.c99;

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

public class C99EnumOutputGenerator extends OutputGenerator {

    public C99EnumOutputGenerator(TuqanExecutionParameters params) {
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
        IndentedStringList headerText = new IndentedStringList(this.params.getBasic().getIndentSize());

        addHeaderGuard(headerText, enumData);
        headerText.add("");
        addEnumDefinition(headerText, enumData);
        headerText.add("");
        closeHeaderGuard(headerText, enumData);

        File headerFile = getFileFor(enumData.name() + ".h");
        FilesaveWrapper.saveToFile(headerText.getAsStringList(), headerFile.getAbsolutePath());
    }

    private void addHeaderGuard(IndentedStringList text, DefaultEnum enumData) {
        text.add("#ifndef " + enumData.name().toUpperCase() + "_H");
        text.add("#define " + enumData.name().toUpperCase() + "_H");
        text.add("");
    }

    private void closeHeaderGuard(IndentedStringList text, DefaultEnum enumData) {
        text.add("#endif // " + enumData.name().toUpperCase() + "_H");
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("typedef enum {");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (int i = 0; i < enumValues.size(); i++) {
            text.add(enumValues.get(i) + (i < enumValues.size() - 1 ? "," : ""));
        }

        text.popIndent();
        text.add("} " + enumData.name() + ";");
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.c99);
        return new File(targetDir, filename);
    }
}
