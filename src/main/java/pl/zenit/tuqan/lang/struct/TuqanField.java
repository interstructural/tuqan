package pl.zenit.tuqan.lang.struct;

import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;

public class TuqanField extends TuqanEntangledEntity implements TuqanFieldBroker {

      public static final TuqanField FIELD_ID = new TuqanField("id");
      
      private final String name;
      private final FieldType type;

      public TuqanField(String name, FieldType type, TuqanContext context) throws ParsingFuckup {
            super(context);
            this.name = name;
            this.type = type;
      }
      
      private TuqanField(String privateConstructorForFieldId__passPrimaryKeyNameHere) {
            super(null);
            this.name = privateConstructorForFieldId__passPrimaryKeyNameHere;
            this.type = new FieldType(DataType.INT, DataTypeInfo.primitive());
      }

      @Override public TuqanField asTuqanField() {
            return this;
      }

      @Override public String getName() {
            return name;            
      }
      public FieldType getType() {
            return type;
      }

}
