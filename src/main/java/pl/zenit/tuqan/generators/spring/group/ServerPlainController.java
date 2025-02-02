/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

/**
 *
 * @author user
 */
public class ServerPlainController extends FileGenerator {

    public ServerPlainController(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
//        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".controller";

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.pushIndent();
        text.add("");
        text.add("import jakarta.servlet.http.HttpServletRequest;");
        text.add("import org.springframework.http.ResponseEntity;");
        text.add("import org.springframework.web.bind.annotation.RequestMapping;");
        text.add("import org.springframework.web.bind.annotation.RestController;");
        text.add("import java.util.Date;");
        text.add("");
        text.add("@RestController");
        text.add("public class PlainController {");
        text.pushIndent();
            text.add("");        
            text.add("@RequestMapping(\"\")");
            text.add("public ResponseEntity<String> plain() {");
            text.pushIndent();        
                text.add("return ResponseEntity.ok(\"\" + new Date().getTime());");
            text.popIndent();
            text.add("}");
            text.add("");
        text.popIndent();
        text.add("}");
        
        Files.write(new File(outputDir, "PlainController.java").toPath(), text.getAsStringList());
    }
}