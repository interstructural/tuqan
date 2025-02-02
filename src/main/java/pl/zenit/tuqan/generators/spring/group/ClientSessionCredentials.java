package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ClientSessionCredentials extends FileGenerator {

    public ClientSessionCredentials(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        
        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");
        text.add("import org.springframework.http.HttpEntity;");
        text.add("import org.springframework.http.HttpHeaders;");
        text.add("");
        text.add("public class SessionCredentials {");
        text.add("");
        text.pushIndent();
        text.add("public static final HttpEntity<String> getHeaderEntity() {");
        text.pushIndent();
        text.add("HttpHeaders headers = new HttpHeaders();");
        text.add("headers.set(\"Bearer\", \"0123\");");
        text.add("HttpEntity<String> entity = new HttpEntity<>(headers);");
        text.add("return entity;");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        
        Files.write(new File(outputDir, "SessionCredentials.java").toPath(), text.getAsStringList());
    }

}
