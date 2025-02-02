package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class PomGlobal extends FileGenerator {

public PomGlobal(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) {
        String springBootVersion = "3.1.7";
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String appDesc = "tuqan generated application " + appName;
        String appGroupId = params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name());
        String version = "1"; 
        String packaging = "jar";

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());

        text.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        text.add("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        text.add("xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">");
        text.add("");
        text.pushIndent();
        text.add("<modelVersion>4.0.0</modelVersion>");
        text.add("<groupId>" + appGroupId + "</groupId>");
        text.add("<artifactId>" + appName + "</artifactId>");
        text.add("<version>1</version>");
        text.add("<packaging>pom</packaging>");
        text.add("");
        text.add("<modules>");
        text.pushIndent();
        text.add("<module>" + new ApplicationNameResolver(params).getServerName() + "</module>");
        text.add("<module>" + new ApplicationNameResolver(params).getDesktopClientName() + "</module>");
        text.popIndent();
        text.add("</modules>");
        
        text.popIndent();
        text.add("</project>");
    
        FilesaveWrapper.saveToFile(text.getAsStringList(), new File(outputDir, "pom.xml").getAbsolutePath());
    }
    
}
