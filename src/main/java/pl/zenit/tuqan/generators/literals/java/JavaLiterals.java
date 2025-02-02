package pl.zenit.tuqan.generators.literals.java;

import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.generators.literals.sql.SqlField;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public class JavaLiterals {
    
    public static String mainServerClassName(String applicationName) {
        return CodeFormatting.firstWordLargeLetter(applicationName) + "Application";
    }
    public static String mainClientClassName(String applicationName) {
        return "Main";
    }

      private final TuqanField field;

      JavaLiterals(TuqanFieldBroker broker) {
            this.field = broker.asTuqanField();
      }

      String getType() throws GenerationFuckup {
            switch (field.getType().getDataType()) {
                  default: case NONE: return "WRONGTYPE";
                  case BOOL:        return "boolean";
                  case INT:         return "int";
                  case LONG:         return "long";
                  case FLOAT:       return "double";
                  case STRING:      return "String";
                  case BINARY:    return "byte[]";
                  case ENUM:
                        TuqanEnumclass enumObject = field.asTuqanField().getContext().findEnum(field.getType().getInfo().getTargetName());
                        if (enumObject == null)
                              throw new GenerationFuckup("empty enum type info");
                        return new JavaEnum(enumObject).name();
                  case LINK:
                  case CHILD: 
                        TuqanScopeclass scopeObj = field.asTuqanField().getContext().findScope(field.getType().getInfo().getTargetName());
                        if (scopeObj == null)
                              throw new GenerationFuckup("empty link type info");
                        return new JavaScope(scopeObj).dtoName();
                  case LIST: 
                  case CHILDREN:
                        TuqanScopeclass scopeObj2 = field.asTuqanField().getContext().findScope(field.getType().getInfo().getTargetName());
                        if (scopeObj2 == null)
                              throw new GenerationFuckup("empty list type info");
                        return "List<" + new JavaScope(scopeObj2).dtoName() + ">";
            }
      }

      String getGenericType() throws GenerationFuckup {
            switch (field.getType().getDataType()) {
                  case BOOL:        return "Boolean";
                  case INT:         return "Integer";
                  case LONG:         return "Long";
                  case BINARY:         return "byte[]";
                  case FLOAT:       return "Double";
                  case STRING:      return "String";
                  default: return getType();
            }
      }

      //---------------------------------------------------------------------------------------------------
      
      public String serializeToString(String argumentName) {
            String symbol = argumentName + "." + new JavaField(field).methodGetterName() + "()";
            switch (field.getType().getDataType()) {
                  case STRING: return symbol;
                  case INT: return "Convert.intToStr(" + symbol + ")";
                  case LONG: return "Convert.longToStr(" + symbol + ")";
                  case FLOAT: return "Convert.doubleToStr(" + symbol + ", -1)";
                  case BOOL: return symbol + " ? \"1\" : \"0\"";
                  case ENUM: return symbol + "." + new JavaEnum(null).serializeMethodName() + "()";
                  case LINK: throw new RuntimeException("LINK");
                  case CHILD: throw new RuntimeException("CHILD");
                  case LIST: throw new RuntimeException("LIST");
                  case CHILDREN: throw new RuntimeException("CHILDREN");
                  case BINARY: throw new RuntimeException("BINARY");
                  case NONE: default: return "";
            }
      }

      public String unserializeFromString(String symbol) throws GenerationFuckup {
            switch (field.getType().getDataType()) {
                  case NONE: default: return "";
                  case STRING: return symbol;
                  case INT: return "Convert.strToInt(" + symbol + ", 0)";
                  case LONG: return "Convert.strToLong(" + symbol + ")";
                  case FLOAT: return "Convert.strToDouble(" + symbol + ")";
                  case BOOL: return "\"1\".equals(" + symbol + ")";
                  case ENUM: return this.getType() + "." + new JavaEnum(null).unserializeMethodName() + "(" + symbol + ")";
                  case LINK: throw new RuntimeException("LINK");
                  case CHILD: throw new RuntimeException("CHILD");
                  case LIST: throw new RuntimeException("LIST");
                  case CHILDREN: throw new RuntimeException("CHILDREN");
                  case BINARY: throw new RuntimeException("BINARY");
            }
      }

      //---------------------------------------------------------------------------------------------------
      
      public String sqlInputValue(final String itemName) throws GenerationFuckup {
            JavaField javaField = new JavaField(field);
            String javaSymbol = itemName + "." + javaField.methodGetterName() + "()";
            String javaEnumSymbol = javaSymbol + "." + new JavaEnum(null).serializeMethodName() + "()";
            switch (field.asTuqanField().getType().getDataType()) {
                  case INT:
                  case LONG:
                  case FLOAT:
                        return "String.valueOf(" + javaSymbol + ").replace(\",\", \".\")";
                  case STRING:
                        return "\"\\'\" + Sanitizer.sanitizeForSql(" + javaSymbol + ") + \"\\'\"";
                  case BOOL:
                        return "(" + javaSymbol + " ? \"1\" : \"0\")";
                  case ENUM:
                        return "\"\\'\" + Sanitizer.sanitizeForSql(" + javaEnumSymbol + ") + \"\\'\"";
                  case LINK:
                        throw new RuntimeException("LINK");
                  case CHILD:
                        throw new RuntimeException("CHILD");
                  case LIST:
                        throw new RuntimeException("LIST");
                  case CHILDREN:
                        throw new RuntimeException("CHILDREN");
                  case BINARY:
                  case NONE:
                  default:  
                        throw new GenerationFuckup("unimplemented sql field type");
            }
      }
      
      public String getSqlRetriveMethod(String argumentName) throws GenerationFuckup {
            JavaField j = new JavaField(field);
            SqlField s = new SqlField(field);
            switch (field.getType().getDataType()) {
                  case ENUM:
                        return j.type() + "." + new JavaEnum(null).unserializeMethodName() + "("
                              + argumentName + ".getString"
                              + "(\"" + s.name() + "\")"
                        + ")";
                  case LINK:
                        throw new RuntimeException("LINK");
                  case CHILD:
                        throw new RuntimeException("CHILD");
                  case LIST:
                        throw new RuntimeException("LIST");
                  case CHILDREN:
                        throw new RuntimeException("CHILDREN");
                  default:
                        return argumentName + ".get" + CodeFormatting.firstWordLargeLetter(j.type()) 
                              + "(\"" + s.name() + "\")";
            }
      }
      
      //---------------------------------------------------------------------------------------------------
      
      public String restEncode(String stringTypedSymbol) {
            return "HttpOutputEncoder.encode(" + stringTypedSymbol + ")";
      }

      public String restDecode(String stringTypedSymbol) {
            return "HttpInputDecoder.decode(" + stringTypedSymbol + ")";
      }
      
} //end of class JavaFieldSymbolics
