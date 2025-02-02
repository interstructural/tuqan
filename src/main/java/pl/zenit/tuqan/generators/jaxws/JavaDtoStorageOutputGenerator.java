package pl.zenit.tuqan.generators.jaxws;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.generators.literals.sql.SqlField;
import pl.zenit.tuqan.generators.literals.sql.SqlScope;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.util.Upcaster;

@Deprecated class JavaDtoStorageOutputGenerator extends JavaOutputGenerator {

      private final String itemFromResultSetFunction = "itemFromResultSet";

      public JavaDtoStorageOutputGenerator(TuqanExecutionParameters params) {
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
            
            text.add("public class " + scope.storageName()+ " {");
            text.pushIndent();
            text.add("");
                        
            addFields(text, scope);
            addConstructor(text, scope);
            addDeleteMethod(text, scope);
            addCreateMethod(text, scope);
            addUpdateMethod(text, scope);
            addGetAllMethod(text, scope);
            addGetMethod(text, scope);
            addItemFromResultFunction(text, scope);
            
            text.popIndent();
            text.add("} // end of class");

            File filename = getFileFor(scope.storageName() + ".java");
            FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
      }

      private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
            text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
            text.add("");
      }

      private void addImports(IndentedStringList text, JavaScope scope) {
            new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
            Arrays.asList(
                  "java.sql.ResultSet",
                  "java.sql.SQLException",
                  "sql.Sanitizer",
                  "sql.SqlConnector")
                  .forEach(i-> text.add("import " + i + ";"));
            text.add("");
      }
      private void addFields(IndentedStringList text, JavaScope scope) {
            SqlScope sqlScope = new SqlScope(scope.asTuqanObject());
            text.add("private static final String scope = \""+ sqlScope.scopeName() + "\";");
            text.add("");
      }
      private void addConstructor(IndentedStringList text, JavaScope scope) {
            text.add("public " + scope.storageName()+ "() {");
            text.pushIndent();
            
            text.add("try {");
            text.pushIndent();
            text.add("Class.forName(\"com.mysql.jdbc.Driver\").newInstance();");
            text.popIndent();
            text.add("}");
            text.add("catch (Exception e) {");
            text.pushIndent();
            text.add("System.out.println(e.toString());");
            text.popIndent();
            text.add("}");

            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addDeleteMethod(IndentedStringList text, JavaScope scope) {
            SqlField fieldId = new SqlField(TuqanField.FIELD_ID);
            text.add("public void " + scope.crud().methodRemove() + "(final List<Integer> list) {");
            text.pushIndent();
            text.add("SqlConnector sql = " + params.getCustomParams().get("SQL CONNECTOR LITERAL") + ";");
            text.add("Coalescer.suppress(() -> sql.open());");
            
            
            text.add("for (int i = 0 ; i < list.size() ; ++i) {");
            text.pushIndent();
            text.add("int finali = i;");
            String query = "\"DELETE FROM \" + scope + \" WHERE "+ fieldId.name() +" = \" + list.get(finali)";
            text.add("String query = " + query + ";");
            text.add("System.out.println(query);");
            text.add("Coalescer.suppress(() -> sql.command(query));");

            text.popIndent();
            text.add("}");
            text.add("Coalescer.suppress(() -> sql.close());");
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addCreateMethod(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            SqlScope sqlScope = new SqlScope(scope.asTuqanObject());
            String itemName = "item";
            
            text.add("public void " + scope.crud().methodCreate() + "(final "+ scope.dtoName() +" " + itemName + ") {");
            text.pushIndent();

            text.add("SqlConnector sql = " + params.getCustomParams().get("SQL CONNECTOR LITERAL") + ";");
            text.add("Coalescer.suppress(() -> sql.open());");
            
            List<SqlField> fieldsWithoutId = sqlScope.fields();
            fieldsWithoutId.removeIf(field-> field.name().equals(new SqlField(TuqanField.FIELD_ID).name()));
            
            List<String> sqlfields = fieldsWithoutId.stream().map(m-> m.name()).collect(Collectors.toList());

            text.add("String query = \"INSERT INTO \" + scope + \" (" + sqlfields.stream().collect(Collectors.joining(", ")) +") VALUES (\"");
            text.pushIndent();
            text.add("+ new StringList()");
            text.pushIndent();

            List<JavaField> javaFields = fieldsWithoutId.stream()
                  .map(f-> new JavaField(f))
                  .collect(Collectors.toList());
            for ( int i = 0 ; i < javaFields.size() ; ++i ) {
                  text.add(".append("+ javaFields.get(i).getLiterals().sqlInputValue(itemName) +")");
            }
            
            text.popIndent();
            text.add(".implode(\", \")");
            text.add("+ \")\";");
            text.popIndent();
            text.add("");
            text.add("System.out.println(query);");
            text.add("Coalescer.suppress(() -> sql.command(query));");
            text.add("Coalescer.suppress(() -> sql.close());");
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addUpdateMethod(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            SqlScope sqlScope = new SqlScope(scope.asTuqanObject());
            String itemName = "item";
            
            text.add("public void " + scope.crud().methodUpdate()+ "(final "+ scope.dtoName() +" " + itemName + ") {");
            text.pushIndent();

            text.add("SqlConnector sql = " + params.getCustomParams().get("SQL CONNECTOR LITERAL") + ";");            
            text.add("Coalescer.suppress(() -> sql.open());");
            
            text.add("String query = \"UPDATE \" + scope + \" SET \"");
            text.pushIndent();
            List<SqlField> fieldsWithoutId = sqlScope.fields();
            fieldsWithoutId.removeIf(field-> field.name().equals(new SqlField(TuqanField.FIELD_ID).name()));
                        
            for ( int i = 0 ; i < fieldsWithoutId.size() ; ++i ) {
                  SqlField sqlField = fieldsWithoutId.get(i);
                  JavaField javaField = new JavaField(sqlField);
                  String updateLine = "+ \" " + sqlField.name() + " = \" + " + javaField.getLiterals().sqlInputValue(itemName);
                  if (i > 0) text.add("+ \", \"");
                  text.add(updateLine);
            }

            text.popIndent();
            text.add("+ \" WHERE id = \" + " + itemName + "." + new JavaField(TuqanField.FIELD_ID).methodGetterName()+ "();");
            
            text.add("");
            text.add("System.out.println(query);");
            text.add("Coalescer.suppress(() -> sql.command(query));");
            text.add("Coalescer.suppress(() -> sql.close());");
            text.popIndent();
            text.add("}");
            text.add("");
      }
      private void addGetAllMethod(IndentedStringList text, JavaScope scope) {
            text.add("public List<" + scope.dtoName() + "> " + scope.crud().methodGetAll() + "() {");
            text.pushIndent();
            
            text.add("List<" + scope.dtoName() + "> list = new ArrayList<>();");
            text.add("SqlConnector sql = " + params.getCustomParams().get("SQL CONNECTOR LITERAL") + ";");
            text.add("String query = \"SELECT * FROM \" + scope;");
            text.add("Coalescer.suppress(() -> sql.open());");
            
            text.add("System.out.println(query);");
            text.add("Coalescer.suppress(() -> sql.select(query).with(result -> {");
            text.pushIndent();
            text.add("while ( result.next() ) {");
            text.pushIndent();
            text.add(scope.dtoName() + " item = "+ itemFromResultSetFunction + "(result);");
            text.add("list.add(item);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("}));");

            text.add("Coalescer.suppress(() -> sql.close());");
            text.add("return list;");
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addGetMethod(IndentedStringList text, JavaScope scope) {
            SqlField fieldId = new SqlField(TuqanField.FIELD_ID);

            text.add("public " + scope.dtoName() + " " + scope.crud().methodGet() + "(final int id) {");
            text.pushIndent();
            
            text.add("List<" + scope.dtoName() + "> list = new ArrayList<>();");
            text.add("SqlConnector sql = " + params.getCustomParams().get("SQL CONNECTOR LITERAL") + ";");
            text.add("String query = \"SELECT * FROM \" + scope + \" WHERE " + fieldId.name() + " = \" + id;");
            text.add("Coalescer.suppress(() -> sql.open());");
            
            text.add("System.out.println(query);");
            text.add("Coalescer.suppress(() -> sql.select(query).with(result -> {");
            text.pushIndent();
            text.add("if ( result.next() ) {");
            text.pushIndent();
            text.add(scope.dtoName() + " item = " + itemFromResultSetFunction + "(result);");
            text.add("list.add(item);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("}));");

            text.add("Coalescer.suppress(() -> sql.close());");
            text.add("return list.isEmpty() ? null : list.get(0);");
            text.popIndent();
            text.add("}");            
            text.add("");
      }
      private void addItemFromResultFunction(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            String argumentName = "result";
            text.add("private " + scope.dtoName() + " " + itemFromResultSetFunction + "(final ResultSet "+ argumentName +") throws SQLException {");
            text.pushIndent();
            
            for ( int i = 0 ; i < scope.fields().size() ; ++i ) {
                  JavaField field = scope.fields().get(i);
                  text.add(field.type() + " " + field.name() + " = " + field.getLiterals().getSqlRetriveMethod(argumentName) + ";");
            }
            
            List<String> list = scope.fields().stream()
                  .map(f-> f.name()).collect(Collectors.toList());
            String fields = String.join(", ", list);
            text.add("return new " + scope.dtoName() + "(" + fields + ");");

            text.popIndent();
            text.add("}");            
            text.add("");
      }

} //end of class JavaOutput
