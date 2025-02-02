package pl.zenit.tuqan.generators.dto.cs;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.CSharpField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class CsDtoOutputGenerator extends OutputGenerator {

    public CsDtoOutputGenerator(TuqanExecutionParameters params) {
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
        addUsings(text);
        text.add("namespace " + params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + " {");
        text.pushIndent();
        text.add("");
        text.add("internal class " + scope.dtoName());
        text.add("{");
        text.pushIndent();
        text.add("");
        addFields(text, scope);
        addProperties(text, scope);
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");

        File filename = getFileFor(scope.dtoName() + ".cs");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addUsings(IndentedStringList text) {
        text.add("using System;");
        text.add("using System.Collections.Generic;");
        text.add("");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(CSharpField::new)
            .stream()
            .map(CSharpField::new)
            .map(f -> {
                try {
                    return "private " + f.type() + " " + f.name() + ";";
                } catch (GenerationFuckup e) {
                    throw new RuntimeException(e);
                }
            })
            .forEachOrdered(text::add);
        text.add("");
    }

    private void addProperties(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (CSharpField f : scope.fields(CSharpField::new)) {
            text.add("public " + f.type() + " " + capitalize(f.name()));
            text.add("{");
            text.pushIndent();
            text.add("get { return " + f.name() + "; }");
            text.add("set { " + f.name() + " = value; }");
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
        File csharpDir = new File(dtoDir, DtoDirNames.cs);
        return new File(csharpDir, filename);
    }
}
