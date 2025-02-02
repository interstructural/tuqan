package pl.zenit.tuqan.generators;

import java.io.File;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public abstract class OutputGenerator {

      protected final TuqanExecutionParameters params;      
      protected final IndentedStringList text;

      public OutputGenerator(TuqanExecutionParameters params) {
            this.params = params;
            text = new IndentedStringList(params.getBasic().getIndentSize());
      }
      
      public abstract void createOutput(TuqanObject data) throws GenerationFuckup;
      
      protected File getFileFor(String filename) {
            return new File(params.getEnviourment().getOutputRootDir().getAbsolutePath() + File.separator + filename);
      }

} //end of class OutputGenerator
