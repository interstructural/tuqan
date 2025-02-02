package pl.zenit.tuqan.generators.dto.java;

import pl.zenit.tuqan.execution.parameters.DeprecatedCustomParams;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.zenit.tuqan.util.Coalescer;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class JavaImmutableDtoOutputGenerator extends JavaOutputGenerator {

      public JavaImmutableDtoOutputGenerator(TuqanExecutionParameters params) {
            super(params);
      }
      
      @Override public void createOutput(TuqanObject data) throws GenerationFuckup {
            if (data instanceof TuqanScopeclass)
                  outputScope(new JavaScope(new Upcaster(data).asInferred()));
            else 
            throw new GenerationFuckup("unknown operation");
            
      }
      
      private void outputScope(JavaScope scope) throws GenerationFuckup {
            text.clear();                        
            addPackageDeclaration(text, scope);
            addImports(text, scope);
            text.add("");
            text.add("public class " + scope.dtoName() + " {");
            text.pushIndent();
            text.add("");
            addFields(text, scope);
            addConstructor(text, scope);
            addGetters(text, scope);
            addSetters(text, scope);
            addBuilder(text, scope);
            text.popIndent();
            text.add("} // end of class");
             
            File filename = getFileFor(scope.dtoName() + "Im.java");
            FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
      }
      
      private void addPackageDeclaration(IndentedStringList text, JavaScope javaScope) {
            text.add("package " + params.getCustomParams().get(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name()) + ";");
            text.add("");
      }
      
      private void addImports(IndentedStringList text, JavaScope javaScope) {
            new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
            text.add("");
      }

      private void addFields(IndentedStringList text, JavaScope javaScope) {
            javaScope.fields()
                  .stream()
                  .map(FailsafeJavaFieldWrapper::new)
                  .map(f-> f.type() + " " + f.name())
                  .map(l-> "private final " + l + ";")
                  .forEachOrdered(text::add);
            text.add("");
      }

      private void addConstructor(IndentedStringList text, JavaScope javaScope) {
            List<String> declarations = new ArrayList<>();
            javaScope.fields()
                  .stream()
                  .map(FailsafeJavaFieldWrapper::new)
                  .map(f-> f.type() + " " + f.name())
                  .forEachOrdered(declarations::add);
            String declist = declarations.stream().collect(Collectors.joining(" "));
            text.add("public " + javaScope.dtoName() + "(" + declist + ") {");
            text.pushIndent();
            
            javaScope.fields()
                  .stream()
                  .map(f-> "this." + f.name() + " = " + f.name() + ";")
                  .forEachOrdered(text::add);

            text.popIndent();
            text.add("}");            
            text.add("");
      }

      private void addGetters(IndentedStringList text, JavaScope javaScope) throws GenerationFuckup {
            for ( int i = 0 ; i < javaScope.fields().size() ; ++i ) {
                  JavaField f = javaScope.fields().get(i);
                  text.add("public " + f.type() + " " + f.methodGetterName() + "() {");
                  text.pushIndent();
                  text.add("return " + f.name() + ";");
                  text.popIndent();
                  text.add("}");
                  text.add("");
            }
      }

      private void addSetters(IndentedStringList text, JavaScope javaScope) throws GenerationFuckup {
            for ( int i = 0 ; i < javaScope.fields().size() ; ++i ) {
                  JavaField f = javaScope.fields().get(i);
                  text.add("public " + javaScope.dtoName() + " "
                        + f.methodSetterWithName() + "(" 
                        + f.type() + " " + f.name() 
                        + ") {");
                  text.pushIndent();
                  List<String> names = fieldlistJavaWriter(javaScope.fields(), xx-> xx.name());
                  text.add("return new " + javaScope.dtoName()
                         + " (" + names.stream().collect(Collectors.joining(", ")) + ");");
                  text.popIndent();
                  text.add("}");
                  text.add("");
            }
      }

      private void addBuilder(IndentedStringList text, JavaScope javaScope) throws GenerationFuckup {
            text.add("public static " + javaScope.dtoBuilderName() + " builder() {");
            text.pushIndent();
            text.add("return new " + javaScope.dtoBuilderName() + "();");
            text.popIndent();
            text.add("}");
            
            text.add("public static class " + javaScope.dtoBuilderName() + " {");
            text.pushIndent();

            text.add("");
            text.add("private " + javaScope.dtoBuilderName() + "() {}");
            text.add("");
            
            javaScope.fields()
                  .stream()
                  .map(FailsafeJavaFieldWrapper::new)
                  .map(f-> f.type() + " " + f.name())
                  .map(l-> "private " + l + ";")
                  .forEachOrdered(text::add);
            text.add("");
            
            for ( int i = 0 ; i < javaScope.fields().size() ; ++i ) {
                  JavaField f = javaScope.fields().get(i);
                  text.add("public " + javaScope.dtoBuilderName() + " "
                        + f.builderMethodSetterName() + "(" 
                        + f.type() + " " + f.name() 
                        + ") {");
                  text.pushIndent();
                  text.add( "this." + f.name() + " = " + f.name() + ";");
                  text.add("return this;");
                  text.popIndent();
                  text.add("}");
                  text.add("");
            }

            text.add("public " + javaScope.dtoName() + " build() {");
            text.pushIndent();
            
            List<String> names = fieldlistJavaWriter(javaScope.fields(), xx-> xx.name());
                  text.add("return new " + javaScope.dtoName()
                  + " (" + names.stream().collect(Collectors.joining(", ")) + ");");
            text.popIndent();
            text.add("}");
            text.add("");

            text.popIndent();
            text.add("}");
            text.add("");
      }
      
      private class FailsafeJavaFieldWrapper {
            
            private final JavaField field;

            public FailsafeJavaFieldWrapper(JavaField field) {
                  this.field = field;
            }
            
            public String name() {
                  return field.name();
            }
            public String type() {
                  return Coalescer.coalesce(()-> field.type(), "TYPE-ERROR");
            }
      }
      
       @Override
        protected File getFileFor(String filename) {
            File overallRoot = params.getEnviourment().getOutputRootDir();
            PackageLocationResolver plr = new PackageLocationResolver(params);
            File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
            File javaDir = new File(dtoDir, DtoDirNames.java);
            return new File(javaDir, filename);            
        }
      
} //end of class
