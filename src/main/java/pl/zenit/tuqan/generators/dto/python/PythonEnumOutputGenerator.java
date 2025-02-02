package pl.zenit.tuqan.generators.dto.python;

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

public class PythonEnumOutputGenerator extends OutputGenerator {

    public PythonEnumOutputGenerator(TuqanExecutionParameters params) {
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

        File pythonFile = getFileFor(enumName + ".py");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), pythonFile.getAbsolutePath());
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("from enum import Enum");
        text.add("");
        text.add("class " + enumData.name() + "(Enum):");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (String value : enumValues) {
            text.add(value + " = '" + value + "'");
        }

        text.popIndent();
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.python);
        return new File(targetDir, filename);
    }
}
