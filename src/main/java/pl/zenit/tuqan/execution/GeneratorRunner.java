package pl.zenit.tuqan.execution;

import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class GeneratorRunner {

      private final OutputGenerator generator;
      private final TuqanObject object;

      public GeneratorRunner(OutputGenerator generator, TuqanObject object) {
            this.generator = generator;
            this.object = object;
      }

      public void run() throws GenerationFuckup {
            generator.createOutput(object);
      }
      
}
