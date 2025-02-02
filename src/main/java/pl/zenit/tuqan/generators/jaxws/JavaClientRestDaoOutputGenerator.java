package pl.zenit.tuqan.generators.jaxws;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import java.io.File;
import pl.zenit.tuqan.execution.RestAddressUtils;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import pl.zenit.tuqan.generators.literals.java.RestField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.util.Upcaster;

@Deprecated class JavaClientRestDaoOutputGenerator extends JavaOutputGenerator {

      public JavaClientRestDaoOutputGenerator(TuqanExecutionParameters params) {
            super(params);
      }

      @Override public void createOutput(TuqanObject data) throws GenerationFuckup {
            if (data instanceof TuqanScopeclass)
                  output(new JavaScope(new Upcaster(data).asInferred()));
            else 
            throw new GenerationFuckup("unknown operation");
      }
      
      private void output(JavaScope scope) throws GenerationFuckup {
            text.clear();
            addPackageDeclaration(text, scope);
            addImports(text, scope);
            
            text.add("public class " + scope.clientRestDaoName()+ " {");
            text.pushIndent();
            text.add("");
            
            addFields(text, scope);
            addGetAllMethod(text, scope);
            addGetMethod(text, scope);
            addDeleteMethod(text, scope);
            addDeleteMethod2(text, scope);
            addCreateMethod(text, scope);
            addUpdateMethod(text, scope);
            text.popIndent();
            
            text.add("} // end of class");

            File filename = getFileFor(scope.clientRestDaoName() + ".java");
            FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
      }

      private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
            text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
            text.add("");
      }
      @SuppressWarnings("deprecated")
      private void addImports(IndentedStringList text, JavaScope scope) {
            new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
            new ImportOrganizer().getRestImports().forEach(i-> text.add("import " + i + ";"));
            text.add("");
      }
      private void addFields(IndentedStringList text, JavaScope scope) {
            text.add("/** assuming rest root path ends with / and is  at least a single / */");
            text.add("private static final String SERVER = \""+ new RestAddressUtils(params).getEndpointRootAddress() + "\";");
            text.add("");
      }

      private void addGetAllMethod(IndentedStringList text, JavaScope scope) {
            text.add("public List<" + scope.dtoName() + "> " + scope.rest().methodGetAll() + "() {");
            text.pushIndent();
            
            text.add("String text = new HttpsRequester(s -> {}).get(SERVER + \"" + scope.rest().endpointGetAll() +"\");");
            text.add("StringList sl = HttpInputDecoder.decode(text);");
            text.add("List<"+ scope.dtoName() +"> list = new ArrayList<>();");
            text.add("sl.forEach(line-> list.add(new "+ scope.serializerName() +"().unserialize(line)));");
            text.add("return list;");
            
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addGetMethod(IndentedStringList text, JavaScope scope) {
            
            text.add("public " + scope.dtoName() + " " + scope.rest().methodGet() + "(final int id) {");
            text.pushIndent();
            text.add("String encodedId = HttpOutputEncoder.encode(new StringList().append(\"\" + id));");
            text.add("StringList params = new StringList().append(\""+ scope.rest().paramId().getFormParamName() +"=\" + encodedId);");
            text.add("String text = new HttpsRequester(s -> {}).get(SERVER + \"" + scope.rest().endpointGet() + "?\" + params.implode(\"&\"));");
            text.add("StringList sl = HttpInputDecoder.decode(text);");
            text.add("return new " + scope.serializerName() + "().unserialize(sl.first());");
            
            text.popIndent();
            text.add("}");            
            text.add("");
      }

      private void addDeleteMethod(IndentedStringList text, JavaScope scope) {
            text.add("public void " + scope.rest().methodDelete() + "(final List<Integer> ids) {");
            text.pushIndent();
            addDeleteMethodContent(text, scope);
            text.popIndent();
            text.add("}");
            text.add("");
      }
      private void addDeleteMethod2(IndentedStringList text, JavaScope scope) {
            text.add("public void " + scope.rest().methodDelete() + "(final int...ids) {");
            text.pushIndent();
            addDeleteMethodContent(text, scope);
            text.popIndent();
            text.add("}");       
            text.add("");
      }
      private void addDeleteMethodContent(IndentedStringList text, JavaScope scope) {
            text.add("StringList list = new StringList();");
            text.add("for (int id : ids) {");
            text.pushIndent();
            text.add("list.add(\"\" + id);");
            text.popIndent();
            text.add("}");
            text.add("String encodedId = HttpOutputEncoder.encode(list);");
            text.add("StringList params = new StringList().append(\"" + scope.rest().paramId().getFormParamName() + "=\" + encodedId);");
            text.add("String text = new HttpsRequester(s -> {}).post(SERVER + \"" + scope.rest().endpointDelete() + "\", params.implode(\"&\"));");
      }
      private void addCreateMethod(IndentedStringList text, JavaScope scope) {
            String argumentName = "item";
            text.add("public void " + scope.rest().methodAdd()+ "(final " + scope.dtoName() + " " + argumentName + ") {");
            text.pushIndent();
            
            scope.fields().stream()
                  .filter(field->  !field.name().equals( new JavaField(TuqanField.FIELD_ID).name()) )
                  .forEachOrdered(field-> {
                        String stringLiteral = field.getLiterals().serializeToString(argumentName);
                        text.add("String " + new RestField(field).getEncodedVarName() + " = " 
                           + field.getLiterals().restEncode("new StringList().append(" + stringLiteral + ")")
                           + ";");
            });
            
            text.add("StringList params = new StringList()");
            text.pushIndent();
            scope.fields().stream()
                  .filter(field->  !field.name().equals( new JavaField(TuqanField.FIELD_ID).name()) )
                  .forEachOrdered(field-> 
                        text.add(".append(\"" + new RestField(field).getFormParamName() + "=\" + " + new RestField(field).getEncodedVarName() + ")"));

            text.add(";");
            text.popIndent();
            text.add("new HttpsRequester(s-> {}).post(SERVER + \"" + scope.rest().endpointAdd() + "\", params.implode(\"&\"));");
            
            text.popIndent();
            text.add("}");
            text.add("");
      }
      private void addUpdateMethod(IndentedStringList text, JavaScope scope) {
            String argumentName = "item";
            text.add("public void " + scope.rest().methodUpdate() + "(final " + scope.dtoName() + " " + argumentName + ") {");
            text.pushIndent();
            
            scope.fields().stream().forEachOrdered(field-> {
                  String stringLiteral = field.getLiterals().serializeToString(argumentName);
                  text.add("String " + new RestField(field).getEncodedVarName() + " = " 
                           + field.getLiterals().restEncode("new StringList().append(" + stringLiteral + ")")
                           + ";");
            });

            text.add("StringList params = new StringList()");
            text.pushIndent();
            scope.fields().stream().forEachOrdered(field->
                  text.add(".append(\"" + new RestField(field).getFormParamName() + "=\" + " + new RestField(field).getEncodedVarName() + ")"));

            text.add(";");
            text.popIndent();
            
            text.add("new HttpsRequester(s -> {}).post(SERVER + \"" + scope.rest().endpointUpdate() + "\", params.implode(\"&\"));");

            text.popIndent();
            text.add("}");
            text.add("");
      }
      
} //end of class 
