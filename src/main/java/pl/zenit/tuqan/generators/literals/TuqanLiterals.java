package pl.zenit.tuqan.generators.literals;

import pl.zenit.tuqan.lang.struct.DataType;
import static pl.zenit.tuqan.lang.struct.DataType.BINARY;
import static pl.zenit.tuqan.lang.struct.DataType.BOOL;
import static pl.zenit.tuqan.lang.struct.DataType.CHILDREN;
import static pl.zenit.tuqan.lang.struct.DataType.ENUM;
import static pl.zenit.tuqan.lang.struct.DataType.FLOAT;
import static pl.zenit.tuqan.lang.struct.DataType.INT;
import static pl.zenit.tuqan.lang.struct.DataType.LINK;
import static pl.zenit.tuqan.lang.struct.DataType.CHILD;
import static pl.zenit.tuqan.lang.struct.DataType.LIST;
import static pl.zenit.tuqan.lang.struct.DataType.LONG;
import static pl.zenit.tuqan.lang.struct.DataType.STRING;
import pl.zenit.tuqan.lang.struct.TuqanField;

public class TuqanLiterals {

      private final TuqanField field;

      public TuqanLiterals(TuqanField field) {
            this.field = field;
      }
      
    public static DataType fromTuqanCode(String type) {
        switch (type.toUpperCase()) {
            case "BOOL":      return BOOL;
            case "INT":       return INT;
            case "LONG":     return LONG;
            case "FLOAT":     return FLOAT;
            case "STRING":    return STRING;
            case "BINARY":  return BINARY;
            case "LINK OF":  return LINK;
            case "LIST OF":  return LIST;
            case "CHILD":  return CHILD;
            case "CHILDREN":  return CHILDREN;
            default: return ENUM;
        }
    }
    public static String toTuqanCode(DataType dataType) {
        switch (dataType) {
            case BOOL:      return "BOOL";
            case INT:       return "INT";
            case LONG:      return "LONG";
            case FLOAT:     return "FLOAT";
            case STRING:    return "STRING";
            case BINARY:    return "BINARY";
            case LINK:      return "LINK OF";
            case LIST:      return "LIST OF";
            case CHILD:      return "CHILD";
            case CHILDREN:  return "CHILDREN";
            default: return "";
        }
    }
      
} //end of class TuqanLiterals
