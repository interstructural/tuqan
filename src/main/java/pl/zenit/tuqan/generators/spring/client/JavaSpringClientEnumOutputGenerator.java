package pl.zenit.tuqan.generators.spring.client;

import java.io.File;
import pl.zenit.tuqan.generators.spring.group.JavaSpringEnumOutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;

public class JavaSpringClientEnumOutputGenerator extends JavaSpringEnumOutputGenerator {

    public JavaSpringClientEnumOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    protected String getDeclaredPackage() {
        return new PackageLocationResolver(params).getRootPacakge() + ".dao";
    }

    @Override
    protected File getFileFor(String filename) {
        String targetFolder = "dao";
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File serverDir = plr.getSpringDesktopClientProjectRoot(overallRoot);
        File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
        File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }

} //end of class
