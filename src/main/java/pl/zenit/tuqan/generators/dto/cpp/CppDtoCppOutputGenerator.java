package pl.zenit.tuqan.generators.dto.cpp;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.CppField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;

import java.io.File;

public class CppDtoCppOutputGenerator extends OutputGenerator {

    public CppDtoCppOutputGenerator(TuqanExecutionParameters params) {
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
        addCppIncludeGuard(text, scope);
        text.add("#include \"" + scope.dtoName() + ".hpp\"");
        text.add("");
        addGetterDefinitions(text, scope);
        addSetterDefinitions(text, scope);
        closeCppIncludeGuard(text, scope);

        File filename = getFileFor(scope.dtoName() + ".cpp");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addCppIncludeGuard(IndentedStringList text, DefaultScope scope) {
        text.add("#ifndef " + scope.dtoName().toUpperCase() + "_CPP");
        text.add("#define " + scope.dtoName().toUpperCase() + "_CPP");
        text.add("");
    }

    private void closeCppIncludeGuard(IndentedStringList text, DefaultScope scope) {
        text.add("#endif // " + scope.dtoName().toUpperCase() + "_CPP");
    }

    private void addGetterDefinitions(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (CppField f : scope.fields(CppField::new)) {
            text.add(f.type() + " " + scope.dtoName() + "::get" + capitalize(f.name()) + "() const {");
            text.pushIndent();
            text.add("return " + f.name() + ";");
            text.popIndent();
            text.add("}");
            text.add("");
        }
    }

    private void addSetterDefinitions(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (CppField f : scope.fields(CppField::new)) {
            text.add("void " + scope.dtoName() + "::set" + capitalize(f.name()) + "(" + f.type() + " " + f.name() + ") {");
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

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File cppDir = new File(dtoDir, DtoDirNames.cpp);
        return new File(cppDir, filename);
    }
}
