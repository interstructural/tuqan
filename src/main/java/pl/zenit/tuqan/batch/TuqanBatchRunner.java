package pl.zenit.tuqan.batch;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import pl.zenit.tuqan.Main;
import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.execution.TuqanExecutor;
import pl.zenit.tuqan.execution.parameters.*;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.fuckup.ProcessingFuckup;

public class TuqanBatchRunner implements Runnable {

    private final File codeFile;

    private final File configFile;

    public TuqanBatchRunner(File codeFile, File propertiesFile) {
        this.codeFile = codeFile;
        this.configFile = propertiesFile;
    }

    @Override
    public void run() {
        if ( !codeFile.isFile() ) {
            Main.log("code file not found!");
            return;
        }
        if ( !configFile.isFile() ) {
            Main.log("config file not found!");
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(codeFile.toPath());
            String code = new String(bytes, Charset.forName("UTF-8"));

            ConfigFileManager cfg = new ConfigFileManager(configFile);
            cfg.loadConfig();
            displayLogo();
            runTuqan(code, cfg);
        }
        catch (Exception any) {
            any.printStackTrace();
        }
    }

    private void displayLogo() throws InterruptedException {
        String version = Main.getAppInfo().getVersion();
        int targetLen = 6;
        if (version.length() > targetLen)
            version = version.substring(0, targetLen);
        while (version.length() < targetLen)
            version += " ";

        List<String> logo = Arrays.asList(
            "                       ,                               ",
            " TUQAN          /  .mP  ~.                             ",
            " " + version + "        /  '`PV      .                 ",
            "               /   .  ,{      `.                       ",
            "            -`      -  '~=.,    ,                      ",
            "          .`          ,       -N|                      ",
            "        .C                                             ",
            "       '             '                                 ",
            "            .      -`                                  ",
            "     ('' .<`    -'                      MEET  TUQAN    ",
            "      .*'.  -  /                   A VOLATILE FRAMEWORK",
            "   ___-xV '`__`'_______________________________________",
            "    /   `                                              ",
            "   ,   ;                             THAT COMES WITH   ",
            "   ,  ,                            NO UNDEPLOYMENT COST",
            "    ~~                                                 ",
            ""
        );
        for (String s : logo) 
        Main.log(s);
    }
    
    private void runTuqan(String code, ConfigFileManager cfg) 
    throws ParsingFuckup, ProcessingFuckup, GenerationFuckup, IOException, InterruptedException, TestFuckup {        
        TuqanExecutionParameters params = new ConfigFileToParams().fileToParams(code, cfg);

        try {
        new TuqanExecutor(Main::log)
                .setDebugLog(params.getBasic().isDebug())
              .execute(params);
        }
        catch (Throwable th) {
            Main.log("TUQAN ERROR: " + th.getClass().getSimpleName() + ": " + th.getMessage());
            if (params.getBasic().isDebug())
                th.printStackTrace();
            return;
        }        
    }

} //end of class