package pl.zenit.tuqan.generators.dto.python;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.PythonField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class PythonDtoOutputGenerator extends OutputGenerator {

    public PythonDtoOutputGenerator(TuqanExecutionParameters params) {
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
        text.add("class " + scope.dtoName() + ":");
        text.pushIndent();
        text.add("def __init__(self):");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.popIndent();

        File filename = getFileFor(scope.dtoName() + ".py");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addFields(IndentedStringList text, DefaultScope scope) {
        scope.fields(PythonField::new)
            .stream()
            .map(PythonField::new)
            .map(f -> {
                try {
                    return "self." + f.name() + " = None  # type: " + f.type();
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
        File pythonDir = new File(dtoDir, DtoDirNames.python);
        return new File(pythonDir, filename);
    }
}
