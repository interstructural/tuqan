package pl.zenit.tuqan.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProcessPrinter {

      private final Consumer<String> output;

      public ProcessPrinter(Consumer<String> output) {
            this.output = output;
      }

      public static List<String> flatRun(String[] cmds) throws IOException, InterruptedException {
            List<String> sl = new ArrayList<>();
            ProcessPrinter p = new ProcessPrinter(sl::add);
            p.run(cmds);
            return sl;
      }

      public void run(String[] cmds) throws IOException, InterruptedException {
            run(null, cmds);
      }

      public void run(File initDir, String[] cmds) throws IOException, InterruptedException {
            Process proc = new ProcessBuilder(cmds)
                    .directory(initDir)
                    .start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            
            String s = null;
            while ((s = stdInput.readLine()) != null)
            output.accept(s);

            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null) 
            output.accept(s);
            
            proc.waitFor();
      }

}
