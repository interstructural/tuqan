package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ApplicationScriptLauncher extends FileGenerator {

    private String scriptFileExtention = "UNDEFINED_EXTENTION";
    private boolean forServer = true;
    
    public ApplicationScriptLauncher(TuqanExecutionParameters params) {
        super(params);
    }

    public ApplicationScriptLauncher forServer() {
        forServer = true;
        return this;
    }
    
    public ApplicationScriptLauncher forClient() {
        forServer = false;
        return this;
    }
    
    
    
    public ApplicationScriptLauncher withFileExtention(String fileExtention) {
        this.scriptFileExtention = fileExtention;
        return this;
    }

    @Override public void createAt(File outputDir) throws IOException {
        ApplicationNameResolver anr = new ApplicationNameResolver(params);
        String artifactName = forServer ? anr.getServerName() : anr.getDesktopClientName();
        
        List<String> sl = Arrays.asList("java -jar target/"+ artifactName + ".jar -Xmx1G");     
        Files.write(new File(outputDir.getAbsolutePath(), "applauncher." + scriptFileExtention).toPath(), sl);
    }

}
