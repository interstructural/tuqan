package pl.zenit.tuqan.generators.dto.go;

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

public class GoEnumOutputGenerator extends OutputGenerator {

    public GoEnumOutputGenerator(TuqanExecutionParameters params) {
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

        File goFile = getFileFor(enumName + ".go");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), goFile.getAbsolutePath());
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("package " + enumData.name().toLowerCase());
        text.add("");
        text.add("type " + enumData.name() + " int");
        text.add("");
        text.add("const (");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (int i = 0; i < enumValues.size(); i++) {
            text.add(enumValues.get(i) + " " + enumData.name() + " = " + String.valueOf(i));
        }

        text.popIndent();
        text.add(")");
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.go);
        return new File(targetDir, filename);
    }
}
