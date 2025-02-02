package pl.zenit.tuqan.generators.literals.sql;

import java.util.ArrayList;
import java.util.List;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class SqlScope {

      private final TuqanScopeclass scope;

      public SqlScope(TuqanScopeclass scope) {
            this.scope = scope;
      }

      public String scopeName() {
            return scope.getName().toLowerCase();
      }
      
      public List<SqlField> fields() {
            List<SqlField> fs = new ArrayList<>();
            for (int i = 0; i < scope.getFields().size() ; ++i) {
                  SqlField f = new SqlField(scope.getFields().get(i));
                  fs.add(f);
            }
            return fs;
      }
      
      public TuqanScopeclass asTuqanObject() {
            return scope;
      }
            
}
