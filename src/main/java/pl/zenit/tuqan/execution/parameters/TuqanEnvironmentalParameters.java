package pl.zenit.tuqan.execution.parameters;

import java.io.File;
import pl.zenit.tuqan.execution.GeneratorSetup;

public class TuqanEnvironmentalParameters {

    private final String code;
    private final File outputRootDir;
    private final int threadCount; 
    private final GeneratorSetup generatorSetup;
    private final boolean useCustomAccessControl;

    public TuqanEnvironmentalParameters(String code, File outputRootDir, int threadCount, GeneratorSetup generatorSetup, boolean useCustomAccessControl) {
        this.code = code;
        this.outputRootDir = outputRootDir;
        this.threadCount = threadCount;
        this.generatorSetup = generatorSetup;
        this.useCustomAccessControl = useCustomAccessControl;
    }

    public String getCode() {
        return code;
    }

    public File getOutputRootDir() {
        return outputRootDir;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public GeneratorSetup getGeneratorSetup() {
        return generatorSetup;
    }

    public boolean isUseCustomAccessControl() {
        return useCustomAccessControl;
    }

    public static TuqanEnvironmentalParametersBuilder builder() {
        return new TuqanEnvironmentalParametersBuilder();
    }

    public static class TuqanEnvironmentalParametersBuilder {

        private String code;
        private File outputRootDir;
        private int threadCount = 1;
        private GeneratorSetup generatorSetup;
        private boolean useCustomAccessControl;

        private TuqanEnvironmentalParametersBuilder() {
        }

        public TuqanEnvironmentalParametersBuilder code(String code) {
            this.code = code;
            return this;
        }

        public TuqanEnvironmentalParametersBuilder outputRootDir(File outputRootDir) {
            this.outputRootDir = outputRootDir;
            return this;
        }

        public TuqanEnvironmentalParametersBuilder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }
        
        public TuqanEnvironmentalParametersBuilder generatorSetup(GeneratorSetup generatorSetup) {
            this.generatorSetup = generatorSetup;
            return this;
        }

        public TuqanEnvironmentalParametersBuilder useCustomAccessControl(boolean useCustomAccessControl) {
            this.useCustomAccessControl = useCustomAccessControl;
            return this;
        }

        public TuqanEnvironmentalParameters build() {
            return new TuqanEnvironmentalParameters(code, outputRootDir, threadCount, generatorSetup, useCustomAccessControl);
        }

    }

} // end of class
