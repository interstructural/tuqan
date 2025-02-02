package pl.zenit.tuqan.util;

import java.util.Arrays;
import java.util.function.Consumer;

public class StackTracePrinter {

    private final Consumer<String> output;

    public StackTracePrinter(Consumer<String> output) {
        this.output = output;
    }

    public void printStackTrace(Throwable th) {
        th.printStackTrace();

        output.accept(th.getClass().getName() + ": " + th.getMessage());

        Throwable issue = th;
        while ( true ) {
            Arrays.asList(issue.getStackTrace()).forEach(line -> output.accept(line.getFileName() + " line " + line.getLineNumber() + ": "
                    + line.getClassName() + "::" + line.getMethodName() + "()"));
            issue = issue.getCause();
            if ( issue == null ) {
                break;
            }
            else {
                output.accept("---------------------- caused by: ----------------------");
            }
        }
    }

    public void printHeader(Throwable th) {
        output.accept(th.getClass().getSimpleName() + ": " + th.getMessage());
    }

}
