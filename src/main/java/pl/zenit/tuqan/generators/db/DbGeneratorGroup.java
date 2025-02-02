package pl.zenit.tuqan.generators.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.*;
import pl.zenit.tuqan.generators.dto.java.JavaEnumOutputGenerator;
import pl.zenit.tuqan.generators.spring.group.ApplicationNameResolver;

public class DbGeneratorGroup extends GeneratorGroup {

    public DbGeneratorGroup() {
        super("sql", Arrays.asList(
              SqlCreateOutputGenerator::new
        ));
    }

    @Override
    public Runnable getInitializer(TuqanExecutionParameters params) {
        return () -> {
            File projectRootDir = params.getEnviourment().getOutputRootDir();            
            File grouproot = new File(projectRootDir, new ApplicationNameResolver(params).getDbDomainName());
            File sqlroot = new File(grouproot, DbDirNames.sql);
            File postgresroot = new File(grouproot, DbDirNames.postgres);
            createDir(grouproot);
            createDir(sqlroot);
            createDir(postgresroot);
            createDockerFiles(grouproot, params);
        };
    }
    
    private void createDir(File dir) {
        if ( !dir.isDirectory() )
            try {
            Files.createDirectory(dir.toPath());
        }
        catch (IOException ex) {
            throw new RuntimeException("error while creating " + dir.getAbsolutePath());
        }
    }

    private void createDockerFiles(File groupRoot, TuqanExecutionParameters params) {
        try {
            File sqlroot = new File(groupRoot, DbDirNames.sql);
            File postgresroot = new File(groupRoot, DbDirNames.postgres);

            new MariadbDockerComposeGenerator(params).createAt(sqlroot);
            new DockerfileGenerator(params).createAt(sqlroot);

            new PostgresDockerComposeGenerator(params).createAt(postgresroot);
            new DockerfileGenerator(params).createAt(postgresroot);

            new RunMariadbFileGenerator(params).createAt(groupRoot);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override public List<String> getCustomParameters() {
        return Arrays.asList();
    }

    @Override public List<OutputGenerator> getEnumGenerators(TuqanExecutionParameters params) {
        List<Function<TuqanExecutionParameters, OutputGenerator>> suppliers = Arrays.asList(
              JavaEnumOutputGenerator::new);
        return suppliers
            .stream()
            .map(supplier-> supplier.apply(params))
            .collect(Collectors.toList());
    }
    
} //end of class
