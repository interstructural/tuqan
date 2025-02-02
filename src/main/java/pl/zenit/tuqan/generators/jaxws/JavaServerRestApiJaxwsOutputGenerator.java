package pl.zenit.tuqan.generators.jaxws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.RestField;
import pl.zenit.tuqan.lang.struct.TuqanObject;

@Deprecated class JavaServerRestApiJaxwsOutputGenerator extends JavaOutputGenerator {

      public JavaServerRestApiJaxwsOutputGenerator(TuqanExecutionParameters params) {
            super(params);
      }

      private static final String q = "\"";

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
            
            
            text.add("@Path(" + q +  params.getCustomParams().get(SpringCustomParams.SERVER_ROOT_PATH.name())  + q + ")");
            text.add("public class " + scope.serverRestDaoName() + " {");
            text.pushIndent();
            text.add("");
            
            addFields(text, scope);
            
            addGetAllMethod(text, scope);
            addGetMethod(text, scope);
            addDeleteMethod(text, scope);
            addCreateMethod(text, scope);
            addUpdateMethod(text, scope);
            text.popIndent();
            
            text.add("} // end of class");

            File filename = getFileFor(scope.serverRestDaoName() + ".java");
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
            new ImportOrganizer().getJaxWsImports().forEach(i-> text.add("import " + i + ";"));
            text.add("");
      }
      private void addFields(IndentedStringList text, JavaScope scope) {
            text.add("private final "+ scope.storageName() +" storage = new " + scope.storageName() + "();");
            text.add("");
      }

      private void addGetAllMethod(IndentedStringList text, JavaScope scope) {
            text.add("@GET");
            text.add("@Produces(MediaType.TEXT_PLAIN)");
            text.add("@Path(" + q + scope.rest().endpointGetAll() + q + ")");
            text.add("public Response "+ scope.rest().methodGetAll() +"() {");
            text.pushIndent();
            text.add("List<" + scope.dtoName() + "> list = storage." + scope.crud().methodGetAll() + "();");
            text.add("StringList sl = new StringList();");
            text.add("");
            text.add("for (" + scope.dtoName() + " item : list) ");
            text.add("sl.add(" + scope.serializerName() + ".serialize(item));");
            text.add("");            
            text.add("return Response");
            text.pushIndent();
            text.add(".status(200)");
            text.add(".entity(HttpOutputEncoder.encode(sl))");
            text.add(".build();");
            text.popIndent();
            text.popIndent();
            text.add("}");
            text.add("");
      }
      private void addGetMethod(IndentedStringList text, JavaScope scope) {
            text.add("@POST");
            text.add("@Produces(MediaType.TEXT_PLAIN)");
            text.add("@Path(" + q + scope.rest().endpointGet() + q + ")");
            text.add("public Response "+ scope.rest().methodGet() +"(@FormParam(" + q  + scope.rest().paramId().getFormParamName() + q + ") String id) {");
            text.pushIndent();
            text.add("String pID = HttpInputDecoder.decode(id == null ? \"\" : id).first();");
            text.add("if (pID == null) pID = " + q + "0" + q + ";");
            text.add("int intId = Convert.strToInt(pID);");
            text.add("String content = " + q + q + ";");
            text.add( scope.dtoName() + " item = storage." + scope.crud().methodGet() + "(intId);");
            text.add("if (item != null) {");
            text.pushIndent();
            text.add("content = " + scope.serializerName() + ".serialize(item);");
            text.popIndent();
            text.add("}");
            text.add("");
            text.add("StringList sl = new StringList();");
            text.add("sl.append(HttpOutputEncoder.encode(new StringList().append(content)));");
            text.add("return Response");
            text.pushIndent();
            text.add(".status(200)");
            text.add(".entity(HttpOutputEncoder.encode(sl))");
            text.add(".build();");
            text.popIndent();
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addDeleteMethod(IndentedStringList text, JavaScope scope) {
            text.add("@POST");
            text.add("@Produces(MediaType.TEXT_PLAIN)");
            text.add("@Path(" + q + scope.rest().endpointDelete()+ q + ")");
            text.add("public Response "+ scope.rest().methodDelete()+"(@FormParam(" + q  + scope.rest().paramId().getFormParamName() + q + ") String ids) {");
            text.pushIndent();
            text.add("StringList pIDs = HttpInputDecoder.decode(ids == null ? \"\" : ids);");
            text.add("List<Integer> list = new ArrayList<>();");
            text.add("pIDs.forEach(pid-> list.add(Convert.strToInt(pid, 0)));");
                                    
            text.add("list.removeIf(p-> p == 0);");
            text.add("storage." + scope.crud().methodRemove() + "(list);");
            text.add("");
            text.add("return Response");
            text.pushIndent();
            text.add(".status(200)");
            text.add(".entity(list.isEmpty() ? " + q + "error" + q + " : "+ q + "success" + q +")");
            text.add(".build();");
            text.popIndent();
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addCreateMethod(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            text.add("@POST");
            text.add("@Produces(MediaType.TEXT_PLAIN)");
            text.add("@Path(" + q + scope.rest().endpointAdd()+ q + ")");
            text.add("public Response "+  scope.rest().methodAdd() + "(");
            text.pushIndent();
            text.pushIndent();
            text.pushIndent();
            text.pushIndent();
            
            List<RestField> restFieldsNoId = new ArrayList<>();
            restFieldsNoId.addAll(scope.rest().fields());
            restFieldsNoId.removeIf(r-> r.getFormParamName().equals(scope.rest().paramId().getFormParamName()));
            
            List<String> formParamNames = new ArrayList();
            restFieldsNoId.stream().forEachOrdered(field-> 
                  formParamNames.add("@FormParam(" + q + field.getFormParamName() + q + ") String " + field.getFunctionParamName() ));
            
            for ( int i = 0 ; i < formParamNames.size() ; ++i ) {
                  String comma = i < formParamNames.size()-1 ? ", " : ") {";
                  text.add(formParamNames.get(i) + comma);
            }

            text.popIndent();
            text.popIndent();
            text.popIndent();
            text.add("");
            text.add("try {");
            text.pushIndent();
            
            
            for ( int i = 0 ; i < restFieldsNoId.size() ; ++i ) {
                  RestField field = restFieldsNoId.get(i);
                  JavaField jField = new JavaField(field);
                  String decodeLiteral = jField.getLiterals().restDecode(field.getFunctionParamName()) + ".first()";
                  text.add(jField.type() 
                           + " " 
                           + field.getDecodedVarName() 
                           + " = " 
                           + jField.getLiterals().unserializeFromString(decodeLiteral)
                           + ";");
            }

            text.add(scope.dtoName() + " item = new " + scope.dtoName() + "(0, ");
            text.pushIndent();
            
            List<String> decodedVarNames = new ArrayList();
            restFieldsNoId.stream().forEachOrdered(field-> decodedVarNames.add(field.getDecodedVarName()));

            for ( int i = 0 ; i < decodedVarNames.size() ; ++i ) {
                  String comma = i == decodedVarNames.size()-1 ? "" : ", ";
                  text.add(decodedVarNames.get(i) + comma);
            }
            text.popIndent();
            text.add(");");
            text.add("");
            text.add("storage." + scope.crud().methodCreate() + "(item);");
            text.add("return Response.status(200).entity(\"success\").build();");
            text.popIndent();
            text.add("}");
            text.add("catch (Exception e) {");
            text.pushIndent();
            text.add("return Response.status(200).entity(\"error\").build();");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addUpdateMethod(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            
            text.add("@POST");
            text.add("@Produces(MediaType.TEXT_PLAIN)");
            text.add("@Path(" + q + scope.rest().endpointUpdate() + q + ")");

            text.add("public Response "+  scope.rest().methodUpdate()+ "(");
            text.pushIndent();
            text.pushIndent();
            text.pushIndent();
            text.pushIndent();
            
            List<String> formParamNames = new ArrayList();
            scope.rest().fields().stream().forEachOrdered(field->
                  formParamNames.add("@FormParam(" + q + field.getFormParamName() + q + ") String " + field.getFunctionParamName() ));
            
            for ( int i = 0 ; i < formParamNames.size() ; ++i ) {
                  String comma = i < formParamNames.size()-1 ? ", " : ") {";
                  text.add(formParamNames.get(i) + comma);
            }

            text.popIndent();
            text.popIndent();
            text.popIndent();
            text.add("");
            text.add("try {");
            text.pushIndent();
            
            for ( int i = 0 ; i < scope.rest().fields().size() ; ++i ) {
                  RestField field = scope.rest().fields().get(i);
                  JavaField jField = new JavaField(field);
                  String decodeLiteral = jField.getLiterals().restDecode(field.getFunctionParamName()) + ".first()";
                  text.add(jField.type() 
                           + " " 
                           + field.getDecodedVarName() 
                           + " = " 
                           + jField.getLiterals().unserializeFromString(decodeLiteral)
                           + ";");
            }

            text.add(scope.dtoName() + " item = new " + scope.dtoName() + "(");
            text.pushIndent();
            List<String> decodedVarNames = new ArrayList();
            scope.rest().fields().stream().forEachOrdered(field-> decodedVarNames.add(field.getDecodedVarName()));

            for ( int i = 0 ; i < decodedVarNames.size() ; ++i ) {
                  String comma = i == decodedVarNames.size()-1 ? "" : ", ";
                  text.add(decodedVarNames.get(i) + comma);
            }
            text.popIndent();
            text.add(");");
            text.add("");
            text.add("storage." + scope.crud().methodUpdate()+ "(item);");
            text.add("return Response.status(200).entity(\"success\").build();");
            text.popIndent();
            text.add("}");
            text.add("catch (Exception e) {");
            text.pushIndent();
            text.add("return Response.status(200).entity(\"error\").build();");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("}");            
            text.add("");

      }
      
} //end of class JavaCle
