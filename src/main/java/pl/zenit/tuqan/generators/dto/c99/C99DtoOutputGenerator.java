package pl.zenit.tuqan.generators.dto.c99;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.C99Field;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;

import java.io.File;
import java.util.Optional;

public class C99DtoOutputGenerator extends OutputGenerator {

    public C99DtoOutputGenerator(TuqanExecutionParameters params) {
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
        addHeaderGuard(text, scope);
        text.add("");

        addIncludes(text, scope);
        text.add("typedef struct {");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add("} " + scope.dtoName() + ";");
        text.add("");
        closeHeaderGuard(text, scope);

        File filename = getFileFor(scope.dtoName() + ".h");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addHeaderGuard(IndentedStringList text, DefaultScope scope) {
        text.add("#ifndef " + scope.dtoName().toUpperCase() + "_H");
        text.add("#define " + scope.dtoName().toUpperCase() + "_H");
        text.add("");
    }

    private void closeHeaderGuard(IndentedStringList text, DefaultScope scope) {
        text.add("#endif // " + scope.dtoName().toUpperCase() + "_H");
    }

    private void addIncludes(IndentedStringList text, DefaultScope scope) {
        text.add("#include <stdio.h>");
        text.add("");
        scope.fields(C99Field::new)
                .stream()
                .filter(p-> p.asTuqanField().getType().getDataType() == DataType.ENUM)
                .map(p-> Optional.ofNullable(p.asTuqanField().getContext().findEnum(p.asTuqanField().getType().getInfo().getTargetName())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DefaultEnum::new)
                .map(DefaultEnum::name)
                .distinct()
                .sorted(String::compareTo)
                .map(f -> "#include \"" + f + ".h\"")
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
                .map(f -> "#include \"" + f + ".hpp\"")
                .forEachOrdered(text::add);
        text.add("");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(C99Field::new)
            .stream()
            .map(C99Field::new)
            .map(f -> {
                try {
                    return f.type() + " " + f.name() + ";";
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
        File c99Dir = new File(dtoDir, DtoDirNames.c99);
        return new File(c99Dir, filename);
    }
}
