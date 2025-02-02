package pl.zenit.tuqan.generators.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.*;
import pl.zenit.tuqan.generators.dto.c99.C99DtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.c99.C99EnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.cpp.CppDtoCppOutputGenerator;
import pl.zenit.tuqan.generators.dto.cpp.CppDtoHppOutputGenerator;
import pl.zenit.tuqan.generators.dto.cpp.CppEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.cs.CsDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.cs.CsEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.delphi.DelphiDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.delphi.DelphiEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.go.GoDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.go.GoEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.haxe.HaxeDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.haxe.HaxeEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.java.JavaImmutableDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.java.JavaEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.java.JavaPojoOutputGenerator;
import pl.zenit.tuqan.generators.dto.kotlin.KotlinDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.kotlin.KotlinEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.php.PhpDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.php.PhpEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.python.PythonDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.python.PythonEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.rust.RustDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.rust.RustEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.scala.ScalaDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.scala.ScalaEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.sol.SolidityDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.sol.SolidityEnumOutputGenerator;
import pl.zenit.tuqan.generators.dto.swift.SwiftDtoOutputGenerator;
import pl.zenit.tuqan.generators.dto.swift.SwiftEnumOutputGenerator;
import pl.zenit.tuqan.generators.spring.group.ApplicationNameResolver;

public class DtoGeneratorGroup extends GeneratorGroup {

    public DtoGeneratorGroup() {
        super("dto", Arrays.asList(
                C99DtoOutputGenerator::new,
                CppDtoHppOutputGenerator::new,
                CppDtoCppOutputGenerator::new,
                CsDtoOutputGenerator::new,
                DelphiDtoOutputGenerator::new,
                GoDtoOutputGenerator::new,
                HaxeDtoOutputGenerator::new,
                JavaPojoOutputGenerator::new,
                JavaImmutableDtoOutputGenerator::new,
                KotlinDtoOutputGenerator::new,
                PhpDtoOutputGenerator::new,
                PythonDtoOutputGenerator::new,
                RustDtoOutputGenerator::new,
                ScalaDtoOutputGenerator::new,
                SolidityDtoOutputGenerator::new,
                SwiftDtoOutputGenerator::new
        ));
    }

    @Override
    public Runnable getInitializer(TuqanExecutionParameters params) {
        return () -> {
            File projectRootDir = params.getEnviourment().getOutputRootDir();           
            File grouproot = new File(projectRootDir, new ApplicationNameResolver(params).getPlainDtoName());
            createDir(grouproot);

            createDir(new File(grouproot, "c99"));
            createDir(new File(grouproot,"cpp"));
            createDir(new File(grouproot,"cs"));
            createDir(new File(grouproot,"delphi"));
            createDir(new File(grouproot,"go"));
            createDir(new File(grouproot,"haxe"));
            createDir(new File(grouproot,"java"));
            createDir(new File(grouproot,"kt"));
            createDir(new File(grouproot,"php"));
            createDir(new File(grouproot,"py"));
            createDir(new File(grouproot,"rs"));
            createDir(new File(grouproot,"scala"));
            createDir(new File(grouproot,"sol"));
            createDir(new File(grouproot,"swift"));
        };
    }
    
    private void createDir(File dir) {
        if ( !dir.isDirectory() )
            try {
            Files.createDirectory(dir.toPath());
        }
        catch (IOException ex) {
            throw new RuntimeException("error while creating " + dir.getAbsolutePath());
        }
    }
    
    @Override public List<String> getCustomParameters() {
        return Arrays.asList();
    }

    @Override public List<OutputGenerator> getEnumGenerators(TuqanExecutionParameters params) {
        List<Function<TuqanExecutionParameters, OutputGenerator>> suppliers = Arrays.asList(
                C99EnumOutputGenerator::new,
                CppEnumOutputGenerator::new,
                CsEnumOutputGenerator::new,
                DelphiEnumOutputGenerator::new,
                GoEnumOutputGenerator::new,
                HaxeEnumOutputGenerator::new,
                JavaEnumOutputGenerator::new,
                KotlinEnumOutputGenerator::new,
                PhpEnumOutputGenerator::new,
                PythonEnumOutputGenerator::new,
                RustEnumOutputGenerator::new,
                ScalaEnumOutputGenerator::new,
                SolidityEnumOutputGenerator::new,
                SwiftEnumOutputGenerator::new
        );
        return suppliers
            .stream()
            .map(supplier-> supplier.apply(params))
            .collect(Collectors.toList());
    }
    
} //end of class
