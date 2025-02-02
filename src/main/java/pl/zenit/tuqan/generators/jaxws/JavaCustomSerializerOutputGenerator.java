package pl.zenit.tuqan.generators.jaxws;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.util.Upcaster;

@Deprecated class JavaCustomSerializerOutputGenerator extends JavaOutputGenerator {

      public JavaCustomSerializerOutputGenerator(TuqanExecutionParameters params) {
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
            
            text.add("public class " + scope.serializerName() + " {");
            text.pushIndent();
            text.add("");
            
            addFunctionSerialize(text, scope);
            addFunctionUnserialize(text, scope);
            
            text.popIndent();
            text.add("} // end of class");

            File filename = getFileFor(scope.serializerName() + ".java");
            FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
      }
      
      private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
            text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
            text.add("");
      }
      
      @SuppressWarnings("deprecated")
      private void addImports(IndentedStringList text, JavaScope scope) {
            new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
            text.add("");
      }
      
      private void addFunctionSerialize(IndentedStringList text, JavaScope scope) {
            String argumentName = "item";
            text.add("public static String "+ scope.serialization().methodSerialize() + "(final " + scope.dtoName() + " " + argumentName + ") {");
            text.pushIndent();
            text.add("return new StringList()");
            text.pushIndent();
            List<String> appends = fieldlistJavaWriter(scope.fields(), field->
                  ".append(" + field.getLiterals().serializeToString(argumentName) + ")");
            appends.forEach(text::add);
            text.popIndent();
            text.add(".serialize();");            
            text.popIndent();
            text.add("}");
            text.add("");
      }
      
      private void addFunctionUnserialize(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
            text.add("public static "+ scope.dtoName() + " " + scope.serialization().methodUnserialize() +"(final String serialized) {");
            text.pushIndent();
            text.add("StringList sl = new StringList();");
            text.add("sl.unserialize(serialized);");

            for ( int i = 0 ; i < scope.fields().size() ; ++i ) {
                  JavaField f = scope.fields().get(i);
                  text.add(f.type() + " " + f.name() + " = " +
                  f.getLiterals().unserializeFromString("sl.get(" + i + ")") 
                  + ";");
            }
            List<String> fieldnames = fieldlistJavaWriter(scope.fields(), f-> f.name());
            text.add("return new "+ scope.dtoName() +"(" + fieldnames.stream().collect(Collectors.joining(", ")) + ");");
            text.popIndent();
            text.add("}");
            text.add("");
      }

} //end of class JavaOutput
