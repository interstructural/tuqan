package pl.zenit.tuqan.generators.spring.server;

import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.generators.literals.java.JavaScope;
import pl.zenit.tuqan.generators.literals.java.JavaField;
import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.generators.literals.sql.SqlScope;

public class JavaSpringModelOutputGenerator extends JavaOutputGenerator {

    public JavaSpringModelOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanScopeclass ) {
            output(new JavaScope(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }

    }

    private void output(JavaScope scope) throws GenerationFuckup {
        text.clear();
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        if (!scope.asTuqanObject().isMirage()) {
            text.add("@Entity");
            text.add("@Table(name = \"" + new SqlScope(scope.asTuqanObject()).scopeName() + "\")");
        }
        text.add("public class " + scope.spring().modelName() + " {");
        text.pushIndent();
        text.add("");
        addFields(text, scope);
        addGettersAndSetters(text, scope);
        text.popIndent();
        text.add("} // end of class");

        File filename = getFileFor(scope.spring().modelName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        text.add("package " + new PackageLocationResolver(params).getRootPacakge() + ".model;");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope scope) {
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
        if (!scope.asTuqanObject().isMirage()) {
            new ImportOrganizer().getSpringServerImports().forEach(i -> text.add("import " + i + ";"));
        }
        text.add("");
    }

    private String findModelTypeName(JavaField field) throws GenerationFuckup {
        DataType datatype = field.asTuqanField().getType().getDataType();
        switch (datatype) {
            default:
                return field.type();
            case LINK:
            case CHILD:
            case LIST:
            case CHILDREN:
        }
        String targetType = field.asTuqanField().getType().getInfo().getTargetName();
        TuqanScopeclass targetClass = field.asTuqanField().getContext().findScope(targetType);
        
        switch (datatype) {
            case LIST: 
            case CHILDREN: 
                return new JavaScope(targetClass).spring().modelListName();
            case LINK: 
            case CHILD: 
                return new JavaScope(targetClass).spring().modelName();
            default: 
                throw new GenerationFuckup("type " + datatype.name() + " not implemented");
        }
    }

    private void addFields(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        boolean mirage = scope.asTuqanObject().isMirage();
        if (mirage) {
            for ( JavaField field : scope.fields() ) {
                text.add("private " + findModelTypeName(field) + " " + field.name() + ";");
            }
            text.add("");
            return;
        }

        for ( JavaField field : scope.fields() ) {
            if ( field.name().equalsIgnoreCase("id") ) {
                text.add("@Id");
                text.add("@GeneratedValue(strategy = GenerationType.IDENTITY)");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.ENUM) {
                text.add("@Enumerated(EnumType.STRING)");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.BINARY) {
                text.add("@Lob");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.CHILDREN) {
                String colWithForeign = field.hibernateForeignKey();
                text.add("@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)");
                text.add("@JoinColumn(name = \"" + colWithForeign + "\")");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.LIST) {
                String childrenType = field.asTuqanField().getType().getInfo().getTargetName();
//                String parentColName = "parent_" + field.hibernateForeignKey();
//                String childrenColName = "children_" + childrenType + "_id";
                String parentColName = "parent_id";
                String childrenColName = "child_id";
                String associationTableName = scope.dtoName() + "_" + field.name() + "_list";

                text.add("@ManyToMany");
                text.add("@JoinTable(");
                text.pushIndent();
                text.add("name = \"" + associationTableName + "\",");
                text.add("joinColumns = @JoinColumn(name = \""+ parentColName +"\"),");
                text.add("inverseJoinColumns = @JoinColumn(name = \"" + childrenColName + "\")");
                text.popIndent();
                text.add(")");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.LINK) {
                String colWithForeign = field.hibernateForeignKey();
                String foreignColWithPrimary = new JavaField(TuqanField.FIELD_ID).name();
                text.add("@OneToOne(fetch = FetchType.EAGER)");
                text.add("@JoinColumn(name = \"" + colWithForeign + "\", referencedColumnName = \""+ foreignColWithPrimary +"\")");
            }
            else if (field.asTuqanField().getType().getDataType() == DataType.CHILD) {
                String colWithForeign = field.hibernateForeignKey();
                String foreignColWithPrimary = new JavaField(TuqanField.FIELD_ID).name();
                text.add("@ManyToOne(fetch = FetchType.EAGER)");
                text.add("@JoinColumn(name = \"" + colWithForeign + "\", referencedColumnName = \""+ foreignColWithPrimary +"\")");
            }
            text.add("private " + findModelTypeName(field) + " " + field.name() + ";");
        }

        text.add("");
    }

    private void addGettersAndSetters(IndentedStringList text, JavaScope scope) throws GenerationFuckup {
        for ( JavaField field : scope.fields() ) {
            String typeName = findModelTypeName(field);
            text.add("public " + typeName + " " + field.methodGetterName() + "() { return " + field.name() + "; }");
            text.add("public void " + field.methodSetterName() + "(final " + typeName + " " + field.name() + ") { this."
                           + field.name() + " = " + field.name() + "; }");
        }
        text.add("");
    }

    @Override protected File getFileFor(String filename) {
        String targetFolder = "model";
        
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File serverDir = plr.getSpringServerProjectRoot(overallRoot);
        File srcCodeDir = plr.getSourceCodeAppRootDir(serverDir);
        File targetDir = new File(srcCodeDir, targetFolder);
        return new File(targetDir, filename);
    }


} //end of class JavaOutput
