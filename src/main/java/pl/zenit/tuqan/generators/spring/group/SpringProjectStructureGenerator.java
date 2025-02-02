package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.phpclient.PhpAllInclude;
import pl.zenit.tuqan.generators.spring.phpclient.PhpEndpoints;

public class SpringProjectStructureGenerator {

    private final TuqanExecutionParameters params;
    private final PackageLocationResolver plr;

    public SpringProjectStructureGenerator(TuqanExecutionParameters params) {
        this.params = params;
        plr = new PackageLocationResolver(params);
    }

    public void generate() {
        try {
            createDirStructure();
            generateFiles();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createDirStructure() {
        File projectRootDir = params.getEnviourment().getOutputRootDir();
        
        File serverProjectDir = plr.getSpringServerProjectRoot(projectRootDir);
        createDir(serverProjectDir); 
        createDir(fileFor(serverProjectDir, "src"));
        createDir(fileFor(serverProjectDir, "src", "main"));
        createDir(fileFor(serverProjectDir, "src", "main", "java"));
        createDir(fileFor(serverProjectDir, "src", "main", "resources"));
        File serverJavaCodeDir = plr.getSourceCodeAppRootDir(serverProjectDir);
        createAllNecessaryDirs(serverJavaCodeDir);
        createDir(fileFor(serverJavaCodeDir, "accesscontrol"));
        createDir(fileFor(serverJavaCodeDir, "model"));
        createDir(fileFor(serverJavaCodeDir, "repository"));
        createDir(fileFor(serverJavaCodeDir, "controller"));
        createDir(fileFor(serverJavaCodeDir, "service")); 

        File clientProjectDir = plr.getSpringDesktopClientProjectRoot(projectRootDir);
        createDir(clientProjectDir);    
        createDir(fileFor(clientProjectDir, "src"));
        createDir(fileFor(clientProjectDir, "src", "main"));
        createDir(fileFor(clientProjectDir, "src", "main", "java"));
        createDir(fileFor(clientProjectDir, "src", "main", "resources"));
        File desktopClientJavaCodeDir = plr.getSourceCodeAppRootDir(clientProjectDir);
        createAllNecessaryDirs(desktopClientJavaCodeDir);
        createDir(fileFor(desktopClientJavaCodeDir, "dao"));

        File phpProjectDir = plr.getSpringPhpClientProjectRoot(projectRootDir);
        createDir(phpProjectDir);
    }

    private void generateFiles() throws IOException {
            File groupDir = params.getEnviourment().getOutputRootDir();
            File serverDir = plr.getSpringServerProjectRoot(groupDir);
            File desktopClientDir = plr.getSpringDesktopClientProjectRoot(groupDir);
            File phpClientDir = plr.getSpringPhpClientProjectRoot(groupDir);

            new PomClient(params).createAt(desktopClientDir);
            new GitIgnore(params).createAt(desktopClientDir);
            
            File clientJavaCodeDir = plr.getSourceCodeAppRootDir(desktopClientDir);
            new ClientMainClass(params).createAt(clientJavaCodeDir);
            new ClientEndpoints(params).createAt(fileFor(clientJavaCodeDir, "dao"));

            new ApplicationScriptLauncher(params).forServer().withFileExtention("bat").createAt(serverDir);
            new ApplicationScriptLauncher(params).forServer().withFileExtention("sh").createAt(serverDir);
            
            new ApplicationScriptLauncher(params).forClient().withFileExtention("bat").createAt(desktopClientDir);
            new ApplicationScriptLauncher(params).forClient().withFileExtention("sh").createAt(desktopClientDir);
            new PomServer(params).createAt(serverDir);
            new GitIgnore(params).createAt(serverDir);
            new ApplicationProperties(params).createAt(fileFor(serverDir, "src", "main", "resources"));
            File serverJavaCodeDir = plr.getSourceCodeAppRootDir(serverDir);
            new ServerMainClass(params).createAt(serverJavaCodeDir);
            new ServerServletInitializer(params).createAt(serverJavaCodeDir);
            
            new ServerDead404Controller(params).createAt(fileFor(serverJavaCodeDir, "controller"));
            new ServerPlainController(params).createAt(fileFor(serverJavaCodeDir, "controller"));
            
            if (params.getEnviourment().isUseCustomAccessControl()) {                
                new ClientSessionCredentials(params).createAt(fileFor(clientJavaCodeDir, "dao"));
                
                new ServerWebConfig(params).createAt(serverJavaCodeDir);
                new ServerAccessInterceptor(params).createAt(fileFor(serverJavaCodeDir, "accesscontrol"));
                new ServerAccessAdmin(params).createAt(fileFor(serverJavaCodeDir, "accesscontrol"));
                new ServerAccessUser(params).createAt(fileFor(serverJavaCodeDir, "accesscontrol"));
                new ServerAccessPublic(params).createAt(fileFor(serverJavaCodeDir, "accesscontrol"));
            }

            new PomGlobal(params).createAt(groupDir);
            new ProjectCompilerBat(params).createAt(groupDir);
            new ProjectCompilerSh(params).createAt(groupDir);
            
            new PhpEndpoints(params).createAt(phpClientDir);
            new PhpAllInclude(params).createAt(phpClientDir);
    }

    private void createDir(File dir) {
        if ( !dir.isDirectory() )
            try {
            Files.createDirectory(dir.toPath());
        }
        catch (IOException ex) {
            throw new RuntimeException("error while creating " + dir.getAbsolutePath());
        }
    }
    private File fileFor(File root, String... children) {
        File dir = root;
        for ( int i = 0; i < children.length; ++i ) {
            dir = new File(dir, children[i]);
        }
        return dir;
    }
    private void createAllNecessaryDirs(File dir) {
        if (dir.isDirectory()) 
            return;
        
        if (!dir.getParentFile().isDirectory()) 
            createAllNecessaryDirs(dir.getParentFile());
            
        createDir(dir);
    }
    private void save(File dir, String filename, List<String> text) throws IOException {
        File target = new File(dir, filename);
        Files.deleteIfExists(target.toPath());
        Files.write(target.toPath(), text, StandardOpenOption.CREATE);
    }

}