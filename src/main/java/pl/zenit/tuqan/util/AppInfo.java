package pl.zenit.tuqan.util;

import java.io.File;

public class AppInfo {

    private final String name;
    private final String version;
    private final File entryPoint;

    public AppInfo(String name, String version, File entryPoint) {
        this.name = name;
        this.version = version;
        this.entryPoint = entryPoint;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public File getEntryPoint() {
        return entryPoint;
    }

}
