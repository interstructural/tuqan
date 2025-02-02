package pl.zenit.tuqan.generators.dto.scala;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
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
import java.util.stream.Collectors;

public class ScalaEnumOutputGenerator extends OutputGenerator {

    public ScalaEnumOutputGenerator(TuqanExecutionParameters params) {
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

        File scalaFile = getFileFor(enumName + ".scala");
        FilesaveWrapper.saveToFile(fileText.getAsStringList(), scalaFile.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, DefaultEnum enumData) {
        text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
        text.add("");
    }

    private void addEnumDefinition(IndentedStringList text, DefaultEnum enumData) throws GenerationFuckup {
        text.add("object " + enumData.name() + " extends Enumeration {");
        text.pushIndent();
        text.add("type " + enumData.name() + " = Value");
        text.add("val " + enumData.fields().stream().collect(Collectors.joining(", ")) + " = Value");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File targetDir = new File(dtoDir, DtoDirNames.scala);
        return new File(targetDir, filename);
    }

}
