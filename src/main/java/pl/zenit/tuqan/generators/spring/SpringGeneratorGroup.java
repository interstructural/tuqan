package pl.zenit.tuqan.generators.spring;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.*;
import pl.zenit.tuqan.generators.spring.client.JavaSpringClientDaoOutputGenerator;
import pl.zenit.tuqan.generators.spring.client.JavaSpringClientEntityOutputGenerator;
import pl.zenit.tuqan.generators.spring.client.JavaSpringClientEnumOutputGenerator;
import pl.zenit.tuqan.generators.spring.group.SpringProjectStructureGenerator;
import pl.zenit.tuqan.generators.spring.phpclient.PhpClientDaoOutputGenerator;
import pl.zenit.tuqan.generators.spring.phpclient.PhpClientEntityOutputGenerator;
import pl.zenit.tuqan.generators.spring.phpclient.PhpClientEnumOutputGenerator;
import pl.zenit.tuqan.generators.spring.server.*;

public class SpringGeneratorGroup extends GeneratorGroup {

      public SpringGeneratorGroup() {            
            super("spring", Arrays.asList(JavaSpringModelOutputGenerator::new,
                  JavaSpringRepositoryOutputGenerator::new,
                  JavaSpringControllerOutputGenerator::new,
                  JavaSpringServiceInterfaceOutputGenerator::new,
                  JavaSpringServiceImplOutputGenerator::new,
                  
                  JavaSpringClientDaoOutputGenerator::new,
                  JavaSpringClientEntityOutputGenerator::new,

                  PhpClientEntityOutputGenerator::new,
                  PhpClientDaoOutputGenerator::new
            ));
      }

    @Override public Runnable getInitializer(TuqanExecutionParameters params) {
        return ()-> new SpringProjectStructureGenerator(params).generate();
    }

    @Override public List<String> getCustomParameters() {
        return Arrays.asList(SpringCustomParams.values()).stream().map(m-> m.name()).collect(Collectors.toList());
    }

    @Override public List<OutputGenerator> getEnumGenerators(TuqanExecutionParameters params) {
        List<Function<TuqanExecutionParameters, OutputGenerator>> suppliers = Arrays.asList(JavaSpringServerEnumOutputGenerator::new,
              JavaSpringClientEnumOutputGenerator::new,
              PhpClientEnumOutputGenerator::new
        );
        return suppliers
                .stream()
                .map(supplier-> supplier.apply(params))
                .collect(Collectors.toList());
    }
    
} //end of class
