package pl.zenit.tuqan.generators.dto.delphi;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.dto.DtoDirNames;
import pl.zenit.tuqan.generators.literals.DefaultScope;
import pl.zenit.tuqan.generators.literals.simple.DelphiField;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.util.Upcaster;
import java.io.File;

public class DelphiDtoOutputGenerator extends OutputGenerator {

    public DelphiDtoOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if (data instanceof TuqanScopeclass)
            outputScope(new DefaultScope(new Upcaster(data).asInferred()));
        else
            throw new GenerationFuckup("unknown operation");
    }

    private void outputScope(DefaultScope scope) throws GenerationFuckup {
        text.clear();
        addUnitDeclaration(text, scope);
        text.add("");
        text.add("type");
        text.add("  " + scope.dtoName() + " = class");
        text.add("  private");
        text.pushIndent();
        addFields(text, scope);
        text.popIndent();
        text.add("  public");
        text.pushIndent();
        addGettersAndSetters(text, scope);
        text.popIndent();
        text.add("  end;");
        text.add("");
        text.add("implementation");
        text.add("");
        addImplementation(text, scope);
        text.add("end.");

        File filename = getFileFor(scope.dtoName() + ".pas");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addUnitDeclaration(IndentedStringList text, DefaultScope scope) {
        text.add("unit " + scope.dtoName() + ";");
        text.add("");
        text.add("interface");
        text.add("");
        text.add("uses");
        text.add("  System.SysUtils;");
        text.add("");
    }

    private void addFields(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (DelphiField field : scope.fields(DelphiField::new)) {
            String fieldName = "F" + capitalize(field.name());
            String fieldType = field.type();
            text.add(fieldName + ": " + fieldType + ";");
        }
        text.add("");
    }

    private void addGettersAndSetters(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (DelphiField field : scope.fields(DelphiField::new)) {
            String capitalizedName = capitalize(field.name());
            String fieldType = field.type();
            text.add("function Get" + capitalizedName + ": " + fieldType + ";");

            text.add("procedure Set" + capitalizedName + "(const Value: " + fieldType + ");");
            text.add("property " + capitalizedName + ": " + fieldType + " read Get" + capitalizedName + " write Set" + capitalizedName + ";");
            text.add("");
        }
    }

    private void addImplementation(IndentedStringList text, DefaultScope scope) throws GenerationFuckup {
        for (DelphiField field : scope.fields(DelphiField::new)) {
            String capitalizedName = capitalize(field.name());
            String fieldType = field.type();
            text.add("function " + scope.dtoName() + ".Get" + capitalizedName + ": " + fieldType + ";");
            text.add("begin");
            text.pushIndent();
            text.add("Result := F" + capitalizedName + ";");
            text.popIndent();
            text.add("end;");
            text.add("");

            text.add("procedure " + scope.dtoName() + ".Set" + capitalizedName + "(const Value: " + fieldType + ");");
            text.add("begin");
            text.pushIndent();
            text.add("F" + capitalizedName + " := Value;");
            text.popIndent();
            text.add("end;");
            text.add("");
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getPlainDtoProjectRoot(overallRoot);
        File delphiDir = new File(dtoDir, DtoDirNames.delphi);
        return new File(delphiDir, filename);
    }
}
