package pl.zenit.tuqan.generators.literals.sql;

import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;

public class SqlField implements TuqanFieldBroker {

      private final TuqanField f;
      public SqlField(TuqanFieldBroker field) {
            this.f = field.asTuqanField();
      }
      @Override public TuqanField asTuqanField() {
            return f;
      }

      public String type() {
            return getLiterals().getType();
      }
      public String name() {
            return toSnakeCase(f.getName());
      }

      public SqlLiterals getLiterals() {
            return new SqlLiterals(f);
      }
      
       public static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

} 
