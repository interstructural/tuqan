package pl.zenit.tuqan.lang.struct;

public class FieldType {

      private final DataType dataType;
      private final DataTypeInfo info;

      public FieldType(DataType dataType, DataTypeInfo info) {
            this.dataType = dataType;
            this.info = info;
      }

      public DataType getDataType() {
            return dataType;
      }

      public DataTypeInfo getInfo() {
            return info;
      }

      public boolean isRelation() {
          //cóż za przebrzydły hack. ale bez niego to kwaśna mina słodki jezu gorzkie łzy i słono każą płacić
            switch (dataType) {
                  case LINK: return true;
                  case CHILD: return true;
                  case LIST: return true;
                  case CHILDREN: return true;
                  default: return false;
            }
      }

} //end of class
