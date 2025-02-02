package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.spring.FileGenerator;

abstract class Pom extends FileGenerator {

public Pom(TuqanExecutionParameters params) {
        super(params);
    }

    protected abstract void addDependencies(IndentedStringList sl);
    
    protected abstract String getAppName();
    
    protected abstract String getMainClassFullName();
    
    @Override public void createAt(File outputDir) {
        String springBootVersion = "3.1.7";
        String appName = getAppName(); 
        String appDesc = "tuqan generated application " + appName;
        String appGroupId = params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name());
        String version = "1"; 
        String packaging = "jar";

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());

        text.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        text.add("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        text.add("xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">");
        text.pushIndent();
        text.add("<modelVersion>4.0.0</modelVersion>");
        text.add("");
        text.add("<parent>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-starter-parent</artifactId>");
        text.add("<version>" + springBootVersion + "</version>");
        text.add("<relativePath/> <!-- lookup parent from repository -->");
        text.popIndent();
        text.add("</parent>");
        text.add("");
        text.add("<groupId>"+ appGroupId +"</groupId>");
        text.add("<name>" + appName + "</name>");
        text.add("<artifactId>" + appName + "</artifactId>");
        text.add("<version>"+ version +"</version>");
        text.add("<packaging>"+ packaging +"</packaging>");
        text.add("<description>" + appDesc + "</description>");
        text.add("<properties>");
        text.pushIndent();
        text.add("<java.version>" + params.getBasic().getTargetJavaVersion() + "</java.version>");
        text.add("<maven.compiler.source>" + params.getBasic().getTargetJavaVersion() + "</maven.compiler.source>");
        text.add("<maven.compiler.target>" + params.getBasic().getTargetJavaVersion() + "</maven.compiler.target>");
        text.add("<start-class>" + getMainClassFullName() + "</start-class>");
        text.popIndent();
        text.add("</properties>");
        
        text.add("<dependencies>");
        text.pushIndent();
        text.add("");
        addDependencies(text);        
        text.popIndent();
        text.add("</dependencies>");

        text.add("<build>");
        text.pushIndent();
        text.add("");
        text.add("<finalName>${project.artifactId}</finalName>");
        text.add("");        
        text.add("<plugins>");
        text.pushIndent();
        
        text.add("<plugin>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-maven-plugin</artifactId>");
        text.popIndent();
        text.add("</plugin>");
        
        text.popIndent();
        text.add("</plugins>");
        text.popIndent();
        text.add("</build>");
        text.popIndent();
        text.add("</project>");
        
        FilesaveWrapper.saveToFile(text.getAsStringList(), new File(outputDir, "pom.xml").getAbsolutePath());
    }
    
}
