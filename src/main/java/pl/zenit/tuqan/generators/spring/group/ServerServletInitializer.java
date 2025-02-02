package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.literals.java.JavaLiterals;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ServerServletInitializer extends FileGenerator {

    public ServerServletInitializer(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge();

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());        
        text.add("package " + pkg + ";");
        text.add("");
        text.add("import org.springframework.boot.builder.SpringApplicationBuilder;");
        text.add("import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;");
        text.add("");
        text.add("public class ServletInitializer extends SpringBootServletInitializer {");
        text.add("");
        text.pushIndent();
        text.add("@Override");
        text.add("protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {");
        text.pushIndent();
		text.add("return application.sources("+ JavaLiterals.mainServerClassName(appName) + ".class);");
        text.popIndent();
        text.add("}");
        text.popIndent();        
        text.add("}");

        Files.write(new File(outputDir, "ServletInitializer.java").toPath(), text.getAsStringList());
    }

}