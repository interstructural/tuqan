package pl.zenit.tuqan.generators.dto.sol;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.C99Field;
import pl.zenit.tuqan.generators.literals.simple.SolidityField;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;
import java.util.Optional;

public class SolidityDtoOutputGenerator extends OutputGenerator {

    public SolidityDtoOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("pragma solidity ^0.8.0;");
        text.add("");
        addIncludes(text, scope);
        text.add("contract " + scope.dtoName() + " {");
        text.pushIndent();
        addFields(text, scope);
        addGetters(text, scope);
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".sol");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addIncludes(IndentedStringList text, DefaultScope scope) {
        scope.fields(SolidityField::new)
                .stream()
                .filter(p-> p.asTuqanField().getType().getDataType() == DataType.ENUM)
                .map(p-> Optional.ofNullable(p.asTuqanField().getContext().findEnum(p.asTuqanField().getType().getInfo().getTargetName())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DefaultEnum::new)
                .map(DefaultEnum::name)
                .distinct()
                .sorted(String::compareTo)
                .map(f -> "import \"./" + f + ".sol\";")
                .forEachOrdered(text::add);
        text.add("");
        scope.fields(C99Field::new)
                .stream()
                .filter(p-> p.asTuqanField().getType().isRelation())
                .map(p-> Optional.ofNullable(p.asTuqanField().getContext().findScope(p.asTuqanField().getType().getInfo().getTargetName())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DefaultScope::new)
                .map(DefaultScope::dtoName)
                .distinct()
                .sorted(String::compareTo)
                .map(f -> "import \"./" + f + ".sol\";")
                .forEachOrdered(text::add);
        text.add("");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(SolidityField::new)
            .stream()
            .map(SolidityField::new)
            .map(f -> {
                try {
                    return f.type() + " private " + f.name() + ";";
                } catch (GenerationFuckup e) {
                    throw new RuntimeException(e);
                }
            })
            .forEachOrdered(text::add);
        text.add("");
    }

    private void addGetters(IndentedStringList text, DefaultScope scope) {
        scope.fields(SolidityField::new)
            .forEach(f -> {
                String capitalized = capitalize(f.name());
                try {
                    text.add("function get" + capitalized + "() public view returns (" + f.type() + ") {");
                } catch (GenerationFuckup e) {
                    throw new RuntimeException(e);
                }
                text.pushIndent();
                text.add("return " + f.name() + ";");
                text.popIndent();
                text.add("}");
                text.add("");
            });
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File solidityDir = new File(dtoDir, DtoDirNames.solidity);
        return new File(solidityDir, filename);
    }
}
