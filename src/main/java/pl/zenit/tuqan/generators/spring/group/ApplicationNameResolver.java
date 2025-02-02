package pl.zenit.tuqan.generators.spring.group;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;

public class ApplicationNameResolver {

    private final TuqanExecutionParameters params;

    public ApplicationNameResolver(TuqanExecutionParameters params) {
        this.params = params;
    }
    
    public String getDesktopClientName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "client";
    }
    
    public String getPhpClientName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "clientphp";
    }

    public String getServerName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "server";
    }
    public String getDbDomainName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "db";
    }
    public String getPlainDtoName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "plaindto";
    }

    public String getPythonClientName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "clientpython";
    }

    public String getDockerClientName() {
        return params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name()) + "docker";
    }

} //end of class
