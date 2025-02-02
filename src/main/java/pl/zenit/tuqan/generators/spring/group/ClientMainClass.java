package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ClientMainClass extends FileGenerator {

    public ClientMainClass(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String pkg = new PackageLocationResolver(params).getRootPacakge();
        
        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");
        text.add("public class Main {");
        text.add("");
        text.pushIndent();
        text.add("public static void main(String[] args) {");
        text.pushIndent();
        text.add("");
        text.add("");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        
        Files.write(new File(outputDir, "Main.java").toPath(), text.getAsStringList());
    }

}
