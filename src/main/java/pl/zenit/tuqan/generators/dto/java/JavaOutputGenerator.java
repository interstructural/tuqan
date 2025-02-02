package pl.zenit.tuqan.generators.dto.java;

import java.util.ArrayList;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.util.List;
import java.util.function.Function;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.OutputGenerator;

public abstract class JavaOutputGenerator extends OutputGenerator {
      
      public JavaOutputGenerator(TuqanExecutionParameters params) {
            super(params);
      }

      protected List<String> fieldlistJavaWriter(List<JavaField> fields, Function<JavaField, String> writer) {
            List<String> sl = new ArrayList<>();
            for ( int i = 0 ; i < fields.size() ; ++i )
            sl.add( writer.apply(fields.get(i)));            
            return sl;
      }

} //end of class JavaOutput
