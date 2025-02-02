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
public class ServerDead404Controller extends FileGenerator {

    public ServerDead404Controller(TuqanExecutionParameters params) {
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
        text.add("import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;");
        text.add("import org.springframework.boot.web.servlet.error.ErrorAttributes;");
        text.add("import org.springframework.http.HttpStatus;");
        text.add("import org.springframework.http.ResponseEntity;");
        text.add("import org.springframework.stereotype.Controller;");
        text.add("import org.springframework.web.bind.annotation.RequestMapping;");
        text.add("");
        text.add("@Controller");
        text.add("public class Dead404Controller extends AbstractErrorController {");
        text.pushIndent();
        text.add("");
        text.add("public Dead404Controller(ErrorAttributes errorAttributes) {");
        text.pushIndent();
        text.add("super(errorAttributes);");
        text.popIndent();
        text.add("}");
        text.add("");
        text.add("@RequestMapping(\"/error\")");
        text.add("public ResponseEntity<?> handleError(HttpServletRequest request) {");
        text.pushIndent();
        text.add("HttpStatus status = getStatus(request);");
        text.add("if (status == HttpStatus.NOT_FOUND) {");
        text.pushIndent();
        text.add("try {");
        text.pushIndent();
        text.add("Thread.sleep(2000);");
        text.popIndent();
        text.add("}");
        text.add("finally {");
        text.pushIndent();
        text.add("return null;");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        text.add("return new ResponseEntity<>(status);");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");
        text.popIndent();


        Files.write(new File(outputDir, "Dead404Controller.java").toPath(), text.getAsStringList());
    }
}