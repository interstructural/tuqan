package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.RestAddressUtils;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ClientEndpoints extends FileGenerator {

    public ClientEndpoints(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        
        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");
        text.add("public class Endpoints {");
        text.add("");
        text.pushIndent();
        text.add("public static final String main = \"" + new RestAddressUtils(params).getEndpointRootAddress() + "\";");
        text.pushIndent();
        text.add("");
        text.popIndent();
        text.popIndent();
        text.add("}");
        
        Files.write(new File(outputDir, "Endpoints.java").toPath(), text.getAsStringList());
    }

}
