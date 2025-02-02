package pl.zenit.tuqan.generators.db;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.spring.FileGenerator;
import pl.zenit.tuqan.generators.spring.group.ApplicationNameResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RunMariadbFileGenerator extends FileGenerator {

    public RunMariadbFileGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createAt(File outputDir) throws IOException {
        String jarName = new ApplicationNameResolver(params).getServerName() + ".jar";
        IndentedStringList text = new IndentedStringList( params.getBasic().getIndentSize() );
        text.clear();
        text.add("docker-compose -f sql/docker-compose-mariadb-db.yml up");
        text.add("pause");
        File file = new File(outputDir, "mariadb_start.bat");
        Files.write(file.toPath(), text.getAsStringList());
    }

}
