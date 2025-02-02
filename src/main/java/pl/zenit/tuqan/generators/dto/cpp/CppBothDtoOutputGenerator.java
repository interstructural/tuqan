package pl.zenit.tuqan.generators.dto.cpp;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.C99Field;
import pl.zenit.tuqan.generators.literals.simple.CppField;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.util.Coalescer;
import pl.zenit.tuqan.util.Upcaster;

import java.io.File;
import java.util.Optional;

public class CppBothDtoOutputGenerator extends OutputGenerator {

    public CppBothDtoOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("class " + scope.dtoName() + " {");
        text.add("public:");
        text.pushIndent();
        text.add("");
        addFields(text, scope);
        addGetters(text, scope);
        addSetters(text, scope);
        text.popIndent();
        text.add("}; // end of class");
        text.add("");
        closeHeaderGuard(text, scope);

        File filename = getFileFor(scope.dtoName() + ".hpp");
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
        text.add("#include <string>");
        text.add("#include <vector>");
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
                .map(f -> "#include \"" + f + ".hpp\"")
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
        scope.fields(CppField::new)
                .stream()
                .map(FailsafeCppFieldWrapper::new)
                .map(f -> f.type() + " " + f.name() + ";")
                .forEachOrdered(text::add);
        text.add("");
    }

    private void addGetters(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (CppField f : scope.fields(CppField::new)) {
            text.add(f.type() + " get" + capitalize(f.name()) + "() const {");
            text.pushIndent();
            text.add("return " + f.name() + ";");
            text.popIndent();
            text.add("}");
            text.add("");
        }
    }

    private void addSetters(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (CppField f : scope.fields(CppField::new)) {
            text.add("void set" + capitalize(f.name()) + "(" + f.type() + " " + f.name() + ") {");
            text.pushIndent();
            text.add("this->" + f.name() + " = " + f.name() + ";");
            text.popIndent();
            text.add("}");
            text.add("");
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private class FailsafeCppFieldWrapper {

        private final CppField field;

        public FailsafeCppFieldWrapper(CppField field) {
            this.field = field;
        }

        public String name() {
            return field.name();
        }

        public String type() {
            return Coalescer.coalesce(() -> field.type(), "TYPE-ERROR");
        }
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File cppDir = new File(dtoDir, DtoDirNames.cpp);
        return new File(cppDir, filename);
    }
}
