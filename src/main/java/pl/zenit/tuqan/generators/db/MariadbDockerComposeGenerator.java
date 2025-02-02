package pl.zenit.tuqan.generators.db;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.spring.FileGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MariadbDockerComposeGenerator extends FileGenerator {

    public MariadbDockerComposeGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createAt(File outputDir) throws IOException {
            File fileFull = new File(outputDir, "docker-compose-mariadb-full.yml");
            Files.write(fileFull.toPath(), getText( true));
            File fileDb = new File(outputDir, "docker-compose-mariadb-db.yml");
            Files.write(fileDb.toPath(), getText(false));
        }

    private List<String> getText(boolean withAppSection) {
        int appPort = Integer.parseInt(params.getCustomParams().get(SpringCustomParams.SERVER_PORT.name()));
        IndentedStringList text = new IndentedStringList( params.getBasic().getIndentSize() );
        text.clear();
        text.add("version: '3.8'");
        text.add("services:");
        text.pushIndent();

        if (withAppSection) {
            text.add("app:");
            text.pushIndent();
            text.add("build: .");
            text.add("ports:");
            text.pushIndent();
            text.add("- \"" + appPort + ":" + appPort + "\"");
            text.popIndent();
            text.add("depends_on:");
            text.pushIndent();
            text.add("- db");
            text.popIndent();
            text.popIndent();
            text.add("");
        }
        text.add("db:");
        text.pushIndent();
        text.add("image: mariadb:10.5");
        text.add("environment:");
        text.pushIndent();
        text.add("MYSQL_ROOT_PASSWORD: " + params.getCustomParams().get(SpringCustomParams.DB_PASSWORD.name()));
        text.add("MYSQL_USER: " + params.getCustomParams().get(SpringCustomParams.DB_USERNAME.name()));
        text.add("MYSQL_PASSWORD: " + params.getCustomParams().get(SpringCustomParams.DB_PASSWORD.name()));
        text.add("MYSQL_DATABASE: tuqanpublic");
        text.popIndent();
        text.add("ports:");
        text.pushIndent();
        text.add("- \"3306:3306\"");
        return text.getAsStringList();
    }

}
