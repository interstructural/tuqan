package pl.zenit.tuqan.generators.spring.phpclient;

import java.io.File;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;

public class PhpClientEnumOutputGenerator extends OutputGenerator {

    public PhpClientEnumOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanEnumclass ) {
            output(new DefaultEnum(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }

    }

    private void output(DefaultEnum enumObject) {
        text.clear();
        text.add("<?php");
        text.add("");
        text.add("class " + enumObject.name() + " {");
        text.pushIndent();
        text.add("");
        addConstants(text, enumObject);
        text.popIndent();
        text.add("} // end of enum");
        text.add("");
        text.add("?>");

        File filename = getFileFor(enumObject.name() + ".php");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addConstants(IndentedStringList text, DefaultEnum enumObject) {
        for ( String constant : enumObject.fields() ) {
            text.add("const " + constant.toUpperCase() + " = '" + constant.toLowerCase() + "';");
        }
        text.add("");
    }
    
    @Override
    protected File getFileFor(String filename) {
            File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File phpDir = plr.getSpringPhpClientProjectRoot(overallRoot);
        return new File(phpDir, filename);
    }
    
} //end of class
