package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ApplicationProperties extends FileGenerator {

    public ApplicationProperties(TuqanExecutionParameters params) {
        super(params);
    }

        @Override public void createAt(File outputDir) throws IOException {
            List<String> sl = new ArrayList<>();
            sl.add("server.servlet.context-path=/" + params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name()).replace("/",  ""));
            sl.add("server.port=" + params.getCustomParams().get(SpringCustomParams.SERVER_PORT.name()));
            sl.add("#database");
            sl.add("spring.datasource.url=" + params.getCustomParams().get(SpringCustomParams.DB_URL.name()));
            sl.add("spring.datasource.username=" + params.getCustomParams().get(SpringCustomParams.DB_USERNAME.name()));
            sl.add("spring.datasource.password=" + params.getCustomParams().get(SpringCustomParams.DB_PASSWORD.name()));
            sl.add("spring.jpa.hibernate.ddl-auto=" + params.getCustomParams().get(SpringCustomParams.HIBERNATE_DDL_AUTO.name()));
            sl.add("hibernate.hbm2ddl.auto=" + params.getCustomParams().get(SpringCustomParams.HIBERNATE_DDL_AUTO.name()));
            sl.add("springdoc.swagger-ui.path=" + params.getCustomParams().get(SpringCustomParams.ENDPOINT_POSTMAN.name()));

            //batch
            sl.add("hibernate.jdbc.batch_size=50");
            sl.add("hibernate.order_inserts=true");
            sl.add("hibernate.order_updates=true");
            sl.add("hibernate.batch_versioned_data=true");
            
            sl.add("spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect");
            

        Files.write(new File(outputDir, "application.properties").toPath(), sl);
    }
    

   
}
