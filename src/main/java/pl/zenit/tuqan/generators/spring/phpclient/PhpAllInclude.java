package pl.zenit.tuqan.generators.spring.phpclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;
import pl.zenit.tuqan.lang.TuqanContext;

public class PhpAllInclude extends FileGenerator {

    public PhpAllInclude(TuqanExecutionParameters params) {
        super(params);
    }
    
    @Override public void createAt(File outputDir) throws IOException {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        
       IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("<?php");
        text.add("");
        text.add("$dir = \"dao/\";");
        text.add("require_once $dir . \"Endpoints.php\";");
        text.add("");        
        TuqanContext.getCurrent().getAllScopes().forEach(s-> {
             text.add("require_once $dir . \"" + s.getName() + ".php\";");
             text.add("require_once $dir . \"" + s.getName() + "Dao.php\";");
        });

        TuqanContext.getCurrent().getEnums().forEach(s-> 
             text.add("require_once $dir . \"" + s.getName() + ".php\";"));

        text.add("");
        text.add("?>");
        
        Files.write(new File(outputDir, "_include_all.php").toPath(), text.getAsStringList());
    }

}