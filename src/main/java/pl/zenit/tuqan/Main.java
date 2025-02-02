package pl.zenit.tuqan;

import pl.zenit.tuqan.gui.MainFrame;
import java.io.File;
import java.util.function.Consumer;

import pl.zenit.tuqan.batch.TuqanBatchRunner;
import pl.zenit.tuqan.util.AppInfo;

public class Main {

    private static final Consumer<String> logger = System.out::println;
    public static void log(String input) {
        logger.accept(input);
    }

    private static AppInfo appInfo;
    public static AppInfo getAppInfo() {
        return appInfo;
    }

    public static void main(String[] args) {
        appInfo = new AppInfo("Tuqan","3.48", new File(System.getProperty("user.dir")));

        if (args.length == 0) {
            //new TuqanMainFrame().setVisible(true);
            new MainFrame().setVisible(true);
        }
        else if (args.length == 3 && args[0].equals("-batch")) {
            Thread.setDefaultUncaughtExceptionHandler(new BatchExceptionHandler());
            new TuqanBatchRunner(new File(args[1]), new File(args[2])).run();
        }
        else {
            System.out.println("expected batch cli format is -jar /path/to/jar -batch path/to/code path/to/properties");
        }
        System.out.println("exiting tuqan");
    }

    private static class BatchExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override public void uncaughtException(Thread t, Throwable e) {
            System.out.println("*** WILD EXCEPTION APPEARED ***");
            System.out.println(t.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
