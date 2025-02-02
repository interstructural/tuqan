package pl.zenit.tuqan.generators.db;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.spring.FileGenerator;
import pl.zenit.tuqan.generators.spring.group.ApplicationNameResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DockerfileGenerator extends FileGenerator {

    public DockerfileGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createAt(File outputDir) throws IOException {
        String jarName = new ApplicationNameResolver(params).getServerName() + ".jar";
        IndentedStringList text = new IndentedStringList( params.getBasic().getIndentSize() );
        text.clear();
        text.add("FROM openjdk:17-jre-slim");
        text.add("WORKDIR /app");
        text.add("COPY " + jarName + " /app/" + jarName);
        text.add("ENTRYPOINT [\"java\", \"-jar\", \"/" + jarName + "\"]");
        File file = new File(outputDir, "dockerfile");
        Files.write(file.toPath(), text.getAsStringList());
    }

}
