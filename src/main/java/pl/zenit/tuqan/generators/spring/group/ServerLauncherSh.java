package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ServerLauncherSh extends FileGenerator {

    public ServerLauncherSh(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String artifactName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        List<String> sl = Arrays.asList("java -jar target/"+ artifactName + ".jar -Xmx1G");
        Files.write(new File(outputDir.getAbsolutePath(), "serverlauncher.sh").toPath(), sl);
    }

}
