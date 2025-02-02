package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ServerWebConfig extends FileGenerator {

    public ServerWebConfig(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge();

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());        
        text.add("package " + pkg + ";");
        text.add("");        
        text.add("import org.springframework.beans.factory.annotation.Autowired;");
        text.add("import org.springframework.context.annotation.Configuration;");
        text.add("import org.springframework.web.servlet.config.annotation.InterceptorRegistry;");
        text.add("import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;");
        text.add("import " + pkg + ".accesscontrol.AccessInterceptor;");
        text.add("");
        text.add("@Configuration");
        text.add("public class WebConfig implements WebMvcConfigurer {");
        text.pushIndent();
        text.add("");
        text.add("@Autowired");
        text.add("private AccessInterceptor accessInterceptor;");
        text.add("");
        text.add("@Override");
        text.add("public void addInterceptors(InterceptorRegistry registry) {");
        text.pushIndent();
        text.add("registry.addInterceptor(accessInterceptor);");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");

        Files.write(new File(outputDir, "WebConfig.java").toPath(), text.getAsStringList());
    }

}