package pl.zenit.tuqan.generators.dto.delphi;

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

public class DelphiEnumOutputGenerator extends OutputGenerator {

    public DelphiEnumOutputGenerator(TuqanExecutionParameters params) {
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
        IndentedStringList unitText = new IndentedStringList(this.params.getBasic().getIndentSize());

        String enumName = enumData.name();
        addUnitHeader(unitText, enumName);
        addEnumDefinition(unitText, enumData);
        addUnitFooter(unitText);

        File unitFile = getFileFor(enumName + ".pas");
        FilesaveWrapper.saveToFile(unitText.getAsStringList(), unitFile.getAbsolutePath());
    }

    private void addUnitHeader(IndentedStringList text, String enumName) {
        text.add("unit " + enumName + ";");
        text.add("");
        text.add("interface");
        text.add("");
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("type");
        text.pushIndent();
        text.add(enumData.name() + " = (");
        text.pushIndent();

        List<String> enumValues = enumData.fields();
        for (int i = 0; i < enumValues.size(); i++) {
            text.add(enumValues.get(i) + (i < enumValues.size() - 1 ? "," : ""));
        }

        text.popIndent();
        text.add(");");
        text.popIndent();
        text.add("");
    }

    private void addUnitFooter(IndentedStringList text) {
        text.add("implementation");
        text.add("");
        text.add("end.");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.delphi);
        return new File(targetDir, filename);
    }
}
