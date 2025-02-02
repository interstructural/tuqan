package pl.zenit.tuqan.generators;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;

public abstract class GeneratorGroup {

      private final String name;
      private final List<Function<TuqanExecutionParameters, OutputGenerator>> generatorSuppliers;

      public GeneratorGroup(String name, List<Function<TuqanExecutionParameters, OutputGenerator>> generatorSuppliers) {
            this.name = name;
            this.generatorSuppliers = generatorSuppliers;
      }

      public String getName() {
            return name;
      }

      public List<OutputGenerator> getGenerators(TuqanExecutionParameters params) {
            return generatorSuppliers.stream()
                  .map(supplier-> supplier.apply(params))
                  .collect(Collectors.toList());
      }
      
      public abstract List<OutputGenerator> getEnumGenerators(TuqanExecutionParameters params);

      public abstract Runnable getInitializer(TuqanExecutionParameters params);

      public abstract List<String> getCustomParameters();
      
      
      
} //end of class
