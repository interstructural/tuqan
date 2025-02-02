package pl.zenit.tuqan.generators.literals;

import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultEnum {

      private final TuqanEnumclass enumObject;

      public DefaultEnum(TuqanEnumclass entity) {
            this.enumObject = entity;
      }
      
      public TuqanEnumclass asTuqanObject() {
            return enumObject;
      }

      public String name() {
            return CodeFormatting.firstWordLargeLetter(enumObject.getName());
      }
      
      public List<String> fields() {
            return enumObject.getValues().stream()
                  .map(m-> m.toUpperCase())
                  .collect(Collectors.toList());
      }

} //end of class
