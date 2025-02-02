package pl.zenit.tuqan.generators.jaxws;

import java.util.Arrays;
import java.util.List;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.*;

@Deprecated class JaxwsGeneratorGroup extends GeneratorGroup {

      public JaxwsGeneratorGroup() {            
            super("jaxws", Arrays.asList(
                  JavaCustomSerializerOutputGenerator::new,
                  JavaDtoStorageOutputGenerator::new,
                  JavaClientRestDaoOutputGenerator::new,
                  JavaServerRestApiJaxwsOutputGenerator::new
            ));
      }

      @Override
      public Runnable getInitializer(TuqanExecutionParameters params) {
            return ()->{}; //jaxws i tak jest rozproszony po 50 miejscach i do wywalenia
      }

    @Override
    public List<String> getCustomParameters() {
        return Arrays.asList("SQL CONNECTOR LITERAL");
    }

    @Override public List<OutputGenerator> getEnumGenerators(TuqanExecutionParameters params) {
        return Arrays.asList();
    }

} //end of class
