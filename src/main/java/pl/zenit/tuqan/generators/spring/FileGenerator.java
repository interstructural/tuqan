package pl.zenit.tuqan.generators.spring;

import java.io.File;
import java.io.IOException;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;

public abstract class FileGenerator {

    protected final TuqanExecutionParameters params;

    public FileGenerator(TuqanExecutionParameters params) {
        this.params = params;
    }

    public abstract void createAt(File outputDir) throws IOException;

}
