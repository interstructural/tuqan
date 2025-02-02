package pl.zenit.tuqan.execution;

import java.util.Arrays;
import java.util.List;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.util.Convert;

public class RestAddressUtils implements TestSupplier {

    private final TuqanExecutionParameters params;

    public RestAddressUtils(TuqanExecutionParameters params) {
        this.params = params;
    }
    
    public String getEndpointRootAddress() {
        return params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name())
            + ":" + params.getCustomParams().get(SpringCustomParams.SERVER_PORT.name())
            + "/" + params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name());
    }

    @Override public List<RunnableAsTest> getTests() {
        return Arrays.asList(this::testAddress, this::testPort, this::testResourceRootPath);
    }
    
    private void testAddress() throws TestFuckup {
        if (!params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name()).contains(":"))
            throw new TestFuckup("address has no protocol specified");

        if (!params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name()).contains("://"))
            throw new TestFuckup("no valid connector (not a //resource)");

        if (params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name()).indexOf(":")
        != params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name()).lastIndexOf(":"))
            throw new TestFuckup("address should not contain port");
    
        if (params.getCustomParams().get(SpringCustomParams.SERVER_PROTOCOLHOST.name()).endsWith("/"))
            throw new TestFuckup("address should specify no endpoint (/ terminated)");
    }
    
    private void testPort() throws TestFuckup {
        if (Convert.strToInt(params.getCustomParams().get(SpringCustomParams.SERVER_PORT.name()), 0)  == 0)
            throw new TestFuckup("port cannot be 0");
    }
    
    private void testResourceRootPath() throws TestFuckup {
        if (params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name()).startsWith("/"))
            throw new TestFuckup("root resource path cant start with /");

        if (params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name()).endsWith("/"))
            throw new TestFuckup("root resource path cant end with /");

        if (params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name()).contains("/"))
            throw new TestFuckup("root resource path does not express a flat hierarchy");
        
    }
        
}
