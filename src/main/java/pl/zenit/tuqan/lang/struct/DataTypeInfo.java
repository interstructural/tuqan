package pl.zenit.tuqan.lang.struct;

public class DataTypeInfo {

      public static DataTypeInfo primitive() {
            return new DataTypeInfo("");
      }
      
      public static DataTypeInfo forTarget(String target) {
            return new DataTypeInfo(target);
      }
      
      private final String targetName;

      private DataTypeInfo(String targetName) {
            this.targetName = targetName;
      }
      
      public String getTargetName() {
            return targetName;

      }
} 
