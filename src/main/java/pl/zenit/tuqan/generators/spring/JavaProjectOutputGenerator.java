package pl.zenit.tuqan.generators.spring;

import java.io.File;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;

public abstract class JavaProjectOutputGenerator extends JavaOutputGenerator {

      private final File projectDir;
      
      public JavaProjectOutputGenerator(TuqanExecutionParameters params, File projectDir) {
            super(params);
            this.projectDir = projectDir;
      }

      @Override protected File getFileFor(String filename) {
            return new File(projectDir, filename);
      }

} //end of class
