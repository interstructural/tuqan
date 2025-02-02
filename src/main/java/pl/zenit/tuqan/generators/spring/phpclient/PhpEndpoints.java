package pl.zenit.tuqan.generators.spring.phpclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.RestAddressUtils;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

public class PhpEndpoints extends FileGenerator {

    public PhpEndpoints(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        
       IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("<?php");
        text.add("");
        text.add("class Endpoints {");
        text.add("");
        text.pushIndent();
        text.add("const main = \"" + new RestAddressUtils(params).getEndpointRootAddress() + "\";");
        text.popIndent();
        text.add("}");
        text.add("");
        text.add("?>");
        
        Files.write(new File(outputDir, "Endpoints.php").toPath(), text.getAsStringList());
    }

}