package pl.zenit.tuqan.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.spring.group.ApplicationNameResolver;

public class PackageLocationResolver {

    private final TuqanExecutionParameters params;

    public PackageLocationResolver(TuqanExecutionParameters params) {
        this.params = params;
    }
    
    public String getRootPacakge() {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String appGroupId = params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name());
        return appGroupId + "." + appName.toLowerCase();
    }

    public File getSourceCodeAppRootDir(File projectRoot) {
        
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String appGroupId = params.getCustomParams().get(SpringCustomParams.APPLICATION_GROUP_ID.name());
        List<String> groupId = new ArrayList<>();
        groupId.addAll(Arrays.asList("src", "main", "java"));
        groupId.addAll(Arrays.asList(appGroupId.split("\\.")));
        groupId.add(appName.toLowerCase());
        
        File srcCodeDir = projectRoot;
        for ( int i = 0 ; i < groupId.size() ; ++i ) {            
            srcCodeDir = new File(srcCodeDir, groupId.get(i));
        }
        
        return srcCodeDir;
    }
    
    public File getSpringServerProjectRoot(File projectGroupRoot) {
        return new File(projectGroupRoot, new ApplicationNameResolver(params).getServerName());
        
    }
    
    public File getSpringDesktopClientProjectRoot(File projectGroupRoot) {
        return new File(projectGroupRoot, new ApplicationNameResolver(params).getDesktopClientName());
    }
    
    public File getSpringPhpClientProjectRoot(File projectGroupRoot) {
        return new File(projectGroupRoot, new ApplicationNameResolver(params).getPhpClientName());
    }
    
    public File getDatabaseProjectRoot(File projectGroupRoot) {
        return new File(projectGroupRoot, new ApplicationNameResolver(params).getDbDomainName());
    }
    
    public File getPlainDtoProjectRoot(File projectGroupRoot) {
        return new File(projectGroupRoot, new ApplicationNameResolver(params).getPlainDtoName());
    }

} //end of class
