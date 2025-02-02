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

class ServerMainClass extends FileGenerator {

    public ServerMainClass(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge();
        String mainClassName = JavaLiterals.mainServerClassName(appName);
        
        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");
        text.add("import org.springframework.boot.SpringApplication;");
        text.add("import org.springframework.boot.autoconfigure.SpringBootApplication;");
        text.add("");
        text.add("@SpringBootApplication");
        text.add("public class " + mainClassName + " {");
        text.add("");
        text.pushIndent();        
        text.add("public static void main(String[] args) {");
        text.pushIndent();
		text.add("SpringApplication.run(" + mainClassName + ".class, args);");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        
        Files.write(new File(outputDir, mainClassName + ".java").toPath(), text.getAsStringList());
    }

}
