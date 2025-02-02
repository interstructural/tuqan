package pl.zenit.tuqan.generators.literals.java;

import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public class RestField implements TuqanFieldBroker {

      private final TuqanField field;
      
      public RestField(TuqanFieldBroker field) {
            this.field = field.asTuqanField();
      }

      @Override public TuqanField asTuqanField() {
            return field;
      }
      
      public String getFormParamName() {
            return field.getName().toLowerCase();
      }
      
      public String getFunctionParamName() {
            return new JavaField(field).name();
      }
      
      public String getDecodedVarName() {
            return new JavaField(field).name() + "Decoded";
      }
      
      public String getEncodedVarName() {
            return new JavaField(field).name() + "Encoded";
      }

      public String getDecodingDeclarationLiteral() throws GenerationFuckup {
            JavaField jField = new JavaField(field);
            String decodeLiteral = jField.getLiterals().restDecode(getFunctionParamName()) + ".first()";
            return jField.type() + " " + getDecodedVarName() 
                   + " = " + jField.getLiterals().unserializeFromString(decodeLiteral);
      }

} //end of class RestField
