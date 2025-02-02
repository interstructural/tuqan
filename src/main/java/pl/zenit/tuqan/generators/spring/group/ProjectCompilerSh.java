package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ProjectCompilerSh extends FileGenerator {

    public ProjectCompilerSh(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        
        List<String> sl = new ArrayList<>();
        sl.add("cd " + new ApplicationNameResolver(params).getServerName());
        sl.add("mvn clean install");
        sl.add("cd ..");
        sl.add("cd " + new ApplicationNameResolver(params).getDesktopClientName());
        sl.add("mvn clean install");
        sl.add("cd ..");
        Files.write(new File(outputDir.getAbsolutePath(), "projectCompiler.sh").toPath(), sl);
    }

}
