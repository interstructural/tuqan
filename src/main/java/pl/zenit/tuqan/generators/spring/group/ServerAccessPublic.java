package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ServerAccessPublic extends FileGenerator {

    public ServerAccessPublic(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".accesscontrol";

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");        
        text.add("import java.lang.annotation.ElementType;");
        text.add("import java.lang.annotation.Retention;");
        text.add("import java.lang.annotation.RetentionPolicy;");
        text.add("import java.lang.annotation.Target;");

        text.add("@Retention(RetentionPolicy.RUNTIME)");
        text.add("@Target({ElementType.METHOD, ElementType.TYPE})");
        text.add("public @interface PublicAccess {");
        text.add("}");

        Files.write(new File(outputDir, "PublicAccess.java").toPath(), text.getAsStringList());
    }
}

        