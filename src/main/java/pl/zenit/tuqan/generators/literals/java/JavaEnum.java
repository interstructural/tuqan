package pl.zenit.tuqan.generators.literals.java;

import pl.zenit.tuqan.generators.literals.DefaultEnum;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;

public class JavaEnum extends DefaultEnum {

      public JavaEnum(TuqanEnumclass entity) {
            super(entity);
      }

      public String serializeMethodName() {
            return "serialize";
      }
      
      public String unserializeMethodName() {
            return "unserialize";
      }
      
} //end of class
