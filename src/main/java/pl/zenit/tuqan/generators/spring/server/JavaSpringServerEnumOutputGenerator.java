package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.generators.spring.group.JavaSpringEnumOutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;

import java.io.File;

public class JavaSpringServerEnumOutputGenerator extends JavaSpringEnumOutputGenerator {

    public JavaSpringServerEnumOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override protected String getDeclaredPackage() {
        return new PackageLocationResolver(params).getRootPacakge() + ".model";
    }

    @Override protected File getFileFor(String filename) {
            String targetFolder = "model";
            File overallRoot = params.getEnviourment().getOutputRootDir();
            PackageLocationResolver plr = new PackageLocationResolver(params);
            File serverDir = plr.getSpringServerProjectRoot(overallRoot);
            File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
            File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }
      
} //end of class
