package pl.zenit.tuqan.generators.spring.group;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.literals.java.JavaLiterals;

class PomServer extends Pom {

    public PomServer(TuqanExecutionParameters params) {
        super(params);
    }

    @Override protected String getAppName() {
        return new ApplicationNameResolver(params).getServerName();
    }

    @Override protected String getMainClassFullName() {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge();
        return pkg + "." + JavaLiterals.mainServerClassName(appName);
    }

    @Override protected void addDependencies(IndentedStringList text) {
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-starter-data-jpa</artifactId>");
        text.popIndent();
        text.add("</dependency>");
        text.add("");
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-starter-web</artifactId>");
        text.popIndent();
        text.add("</dependency>");
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>com.mysql</groupId>");
        text.add("<artifactId>mysql-connector-j</artifactId>");
        text.add("<version>8.3.0</version>");
        text.popIndent();
        text.add("</dependency>");
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>org.springframework.boot</groupId>");
        text.add("<artifactId>spring-boot-starter-test</artifactId>");
        text.add("<scope>test</scope>");
        text.popIndent();
        text.add("</dependency>");
        text.add("<dependency>");
        text.pushIndent();
        text.add("<groupId>org.springdoc</groupId>");
        text.add("<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>");
        text.add("<version>2.1.0</version>");
        text.popIndent();
        text.add("</dependency>");
    }

}
