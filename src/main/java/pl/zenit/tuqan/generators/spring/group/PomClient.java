package pl.zenit.tuqan.generators.spring.group;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.literals.java.JavaLiterals;

class PomClient extends Pom {
        
    public PomClient(TuqanExecutionParameters params) {
        super(params);
    }

    @Override protected String getAppName() {
        return new ApplicationNameResolver(params).getDesktopClientName();
    }
    
    @Override protected String getMainClassFullName() {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge();
        return pkg + "." + JavaLiterals.mainClientClassName(appName);
    }

    @Override protected void addDependencies(IndentedStringList text) {
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-starter-web</artifactId>");
        text.popIndent();
        text.add("</dependency>");
        text.add("");
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>com.fasterxml.jackson.core</groupId>");
        text.add("<artifactId>jackson-databind</artifactId>");
        text.add("<version>2.13.0</version>");
        text.popIndent();
        text.add("</dependency>");
    }
    
}
