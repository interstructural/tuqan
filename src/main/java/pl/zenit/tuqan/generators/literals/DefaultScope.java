package pl.zenit.tuqan.generators.literals;

import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultScope {

      private final TuqanScopeclass scope;

      public DefaultScope(TuqanScopeclass scope) {
            this.scope = scope;
      }
      
      public TuqanScopeclass asTuqanObject() {
            return scope;
      }      

      public String dtoName() {
            return CodeFormatting.firstWordLargeLetter(scope.getName());
      }

      public String dtoBuilderName() {
            return CodeFormatting.firstWordLargeLetter(scope.getName()) + "Builder";
      }
      
      public <FieldType> List<FieldType> fields(Function<TuqanFieldBroker, FieldType> fieldMapper) {
            return scope.getFields().stream().map(fieldMapper).collect(Collectors.toList());
      }

} 
