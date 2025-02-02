package pl.zenit.tuqan.generators.dto.rust;

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

public class RustEnumOutputGenerator extends OutputGenerator {

    public RustEnumOutputGenerator(TuqanExecutionParameters params) {
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
        addEnumDefinition(fileText, enumData);

        File rustFile = getFileFor(enumName + ".rs");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), rustFile.getAbsolutePath());
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("enum " + enumData.name() + " {");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (String value : enumValues) {
            text.add(value + ",");
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
        File targetDir = new File(dtoDir, DtoDirNames.rust);
        return new File(targetDir, filename);
    }
}
