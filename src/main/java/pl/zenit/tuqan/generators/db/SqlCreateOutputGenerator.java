package pl.zenit.tuqan.generators.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pl.zenit.tuqan.generators.literals.sql.SqlField;
import pl.zenit.tuqan.generators.literals.sql.SqlScope;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.DataType;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class SqlCreateOutputGenerator extends OutputGenerator {

    public SqlCreateOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanScopeclass ) {
            output(new SqlScope(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }
    }

    private void output(SqlScope scope) {
        List<String> fields = new ArrayList<>();
        for ( int i = 0; i < scope.fields().size(); ++i ) {
            SqlField f = scope.fields().get(i);

            if ( f.asTuqanField().getType().getDataType() == DataType.LIST ) {
                continue;
            }
            
            if ( f.asTuqanField().getType().getDataType() == DataType.CHILDREN ) {
                continue;
            }

            if ( f.name().equals(new SqlField(TuqanField.FIELD_ID).name()) ) {
                fields.add(f.name() + " " + f.type() + " UNSIGNED NOT NULL AUTO_INCREMENT");
                fields.add("PRIMARY KEY (" + f.name() + ")");
            }
            else {
                fields.add(f.name() + " " + f.type());
            }
        }
        String query = "CREATE TABLE " + scope.scopeName() + " (" + fields.stream().collect(Collectors.joining(", ")) + ")";

        File filename = getFileFor(scope.scopeName() + ".sql");
        FilesaveWrapper.saveToFile(Arrays.asList(query), filename.getAbsolutePath());
    }

    @Override
    public File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File dtoDir = plr.getDatabaseProjectRoot(overallRoot);
        File sqlDir = new File(dtoDir, DbDirNames.sql);
        return new File(sqlDir, filename);
    }

} //end of class SqlCreateOutput
