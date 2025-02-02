package pl.zenit.tuqan.execution.parameters;

import pl.zenit.tuqan.execution.GeneratorSetup;
import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.fuckup.ProcessingFuckup;

import java.io.File;
import java.io.IOException;

public class ConfigFileToParams {

    public TuqanExecutionParameters fileToParams(String code, ConfigFileManager cfg)
    throws  ParsingFuckup,
            ProcessingFuckup,
            GenerationFuckup,
            IOException,
            InterruptedException,
            TestFuckup {

        TuqanBasicParameters basic = TuqanBasicParameters
                .builder()
                .indentSize(Integer.parseInt(cfg.get("indent size", "4")))
                .targetJavaVersion(cfg.get("java version"))
                .addId(true)
                .cleanOutput(cfg.get("clear output", "true").equalsIgnoreCase("true"))
                .skipTests(cfg.get("skip tests", "false").equalsIgnoreCase("true"))
                .debug(cfg.get("debug", "false").equalsIgnoreCase("true"))
                .build();

        TuqanEnvironmentalParameters environment = TuqanEnvironmentalParameters
                .builder()
                .code(code)
                .outputRootDir(new File(cfg.get("output dir", ".")))
                .threadCount(Integer.parseInt(cfg.get("thread count", "1")))
                .generatorSetup(new GeneratorSetup(true, true, true))
                .useCustomAccessControl(cfg.get("use custom access control", "false").equalsIgnoreCase("true"))
                .build();

        TuqanCustomParameters tcp = new TuqanCustomParameters();
        tcp.set(SpringCustomParams.SERVER_PROTOCOLHOST.name(), cfg.get("rest server", "http://localhost"));
        tcp.set(SpringCustomParams.SERVER_PORT.name(), cfg.get("rest port", "8443"));
        tcp.set(SpringCustomParams.SERVER_ROOT_PATH.name(), cfg.get("rest root path", "endpoints"));
        tcp.set(SpringCustomParams.APPLICATION_NAME.name(), cfg.get("app name", "tuqan"));
        tcp.set(SpringCustomParams.APPLICATION_GROUP_ID.name(), cfg.get("app group", "pl.zenit"));
        tcp.set(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE.name(), cfg.get("app group", "pl.zenit") + "." + cfg.get("app name") );
        tcp.set(SpringCustomParams.DB_URL.name(), cfg.get("db url", "jdbc:mysql://localhost:3306/tuqanpublic"));
        tcp.set(SpringCustomParams.DB_USERNAME.name(), cfg.get("db user", "tuqanpublic"));
        tcp.set(SpringCustomParams.DB_PASSWORD.name(), cfg.get("db pass", "tuqanpublic"));
        tcp.set(SpringCustomParams.ENDPOINT_POSTMAN.name(), cfg.get("postman endpoint", "/doc"));
        tcp.set(SpringCustomParams.HIBERNATE_DDL_AUTO.name(), cfg.get("hibernate ddl auto", "none"));

        return new TuqanExecutionParameters(basic, environment, tcp);
    }

    public void fillWithParams(ConfigFileManager cfg, TuqanExecutionParameters tep) {
        TuqanBasicParameters basic = tep.getBasic();
        cfg.put("indent size", String.valueOf(basic.getIndentSize()));
        cfg.put("java version", basic.getTargetJavaVersion());
        cfg.put("auto id", basic.getAddId() ? "true" : "false");
        cfg.put("clear output", basic.getCleanOutput() ? "true" : "false");
        cfg.put("skip tests", basic.getSkipTests() ? "true" : "false");
        cfg.put("debug", basic.isDebug() ? "true" : "false");

        TuqanEnvironmentalParameters en = tep.getEnviourment();
        cfg.put("output dir", en.getOutputRootDir().getPath());
        cfg.put("thread count", String.valueOf(en.getThreadCount()));
        cfg.put("use custom access control", en.isUseCustomAccessControl() ? "true" : "false");

        TuqanCustomParameters tcp = tep.getCustomParams();
        cfg.put("rest server", tcp.get(SpringCustomParams.SERVER_PROTOCOLHOST.name()));
        cfg.put("rest port", tcp.get(SpringCustomParams.SERVER_PORT.name()));
        cfg.put("rest root path", tcp.get(SpringCustomParams.SERVER_ROOT_PATH.name()));
        cfg.put("app name", tcp.get(SpringCustomParams.APPLICATION_NAME.name()));
        cfg.put("app group", tcp.get(SpringCustomParams.APPLICATION_GROUP_ID.name()));
        cfg.put("db url", tcp.get(SpringCustomParams.DB_URL.name()));
        cfg.put("db user", tcp.get(SpringCustomParams.DB_USERNAME.name()));
        cfg.put("db pass", tcp.get(SpringCustomParams.DB_PASSWORD.name()));
        cfg.put("postman endpoint", tcp.get(SpringCustomParams.ENDPOINT_POSTMAN.name()));
        cfg.put("hibernate ddl auto", tcp.get(SpringCustomParams.HIBERNATE_DDL_AUTO.name()));
    }

}
