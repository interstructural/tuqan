package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class GitIgnore extends FileGenerator {

    public GitIgnore(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        List<String> sl = new ArrayList<>();
        sl.add("HELP.md");
        sl.add("target/");
        sl.add("!.mvn/wrapper/maven-wrapper.jar");
        sl.add("!**/src/main/**/target/");
        sl.add("!**/src/test/**/target/");
        sl.add("");
        sl.add("### STS ###");
        sl.add(".apt_generated");
        sl.add(".classpath");
        sl.add(".factorypath");
        sl.add(".project");
        sl.add(".settings");
        sl.add(".springBeans");
        sl.add(".sts4-cache");
        sl.add("");
        sl.add("### IntelliJ IDEA ###");
        sl.add(".idea");
        sl.add("*.iws");
        sl.add("*.iml");
        sl.add("*.ipr");
        sl.add("");
        sl.add("### NetBeans ###");
        sl.add("/nbproject/private/");
        sl.add("/nbbuild/");
        sl.add("/dist/");
        sl.add("/nbdist/");
        sl.add("/.nb-gradle/");
        sl.add("build/");
        sl.add("!**/src/main/**/build/");
        sl.add("!**/src/test/**/build/");
        sl.add("");
        sl.add("### VS Code ###");
        sl.add(".vscode/");
        Files.write(new File(outputDir, ".gitignore").toPath(), sl);
    }

}
