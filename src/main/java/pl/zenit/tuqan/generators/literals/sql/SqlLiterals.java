package pl.zenit.tuqan.generators.literals.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.fuckup.ContextFuckup;

public class SqlLiterals {

      private final TuqanField field;

      SqlLiterals(TuqanField field) {
            this.field = field;
      }
      
      public String getType() {
            switch (field.getType().getDataType()) {
                  default: case NONE: return "WRONGTYPE";
                  case BOOL:        return "TINYINT";
                  case INT:         return "INT";
                  case LONG:         return "BIGINT";
                  case FLOAT:       return "DOUBLE";
                  case STRING:      return "TEXT";
                  case BINARY:    return "LONGBLOB";
                  case ENUM:        
                        String enumName = field.getType().getInfo().getTargetName();
                        TuqanEnumclass enumObject = field.getContext().findEnum(enumName);
                        List<String> list = new ArrayList<>();
                        if (enumObject == null) 
                        throw new ContextFuckup("unknown enum \"" + enumName + "\"");
                        
                        return "ENUM(" 
                               + enumObject.getValues().stream()
                                    .map(m-> "\'" + m + "\'")
                                    .collect(Collectors.joining(", ")) 
                               + ")";
                  case LINK:   return "INT";
                  case CHILD:   return "INT";
                  case LIST:   throw new RuntimeException("LIST is not supported by sql literals");
                  case CHILDREN:   throw new RuntimeException("CHILDREN is not supported by sql literals");
            }
      }
      
} //end of class SqlLiterals