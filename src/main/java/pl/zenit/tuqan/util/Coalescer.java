package pl.zenit.tuqan.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * pl.zenit.koziel.lib.generic
 */

/**
 * "Why all newer languages dont have explicit exceptions? Because they learnt from javas mistake"
 * - Bob Martin
 */
public class Coalescer {

    public static void ignore(ExceptionableCaller<? extends Throwable> code) {
        try {
            code.call();
        }
        catch (Throwable ex) {
            return;
        }
    }

    public static void suppress(ExceptionableCaller<? extends Throwable> code) {
        try {
            code.call();
        }
        catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void handle(ExceptionableCaller<? extends Throwable> code, Consumer<Throwable> reporter) throws Throwable {
        try {
            code.call();
        }
        catch (Throwable ex) {
            reporter.accept(ex);
            throw ex;
        }
    }

    public static void shortLog(ExceptionableCaller<? extends Throwable> code, Consumer<String> logger) throws Throwable {
        handle(code, ex -> logger.accept(ex.getClass().getSimpleName() + ": " + ex.getMessage()));
    }

    public static void stackTraceLog(ExceptionableCaller<? extends Throwable> code, Consumer<String> logger) throws Throwable {
        handle(code, ex -> genericReporter(ex).forEach(logger));
    }

    /** z myślą o safe navigation */
    public static <ValueType> ValueType coalesce(ExceptionableSupplier<? extends Throwable, ValueType> caller,
                                                 ValueType defaultValue) {
        try {
            return caller.get();
        }
        catch (Throwable ex) {
            return defaultValue;
        }
    }

    //---------------------------------------------------------------------------------------------------
    @FunctionalInterface
    public static interface ExceptionableCaller<ExceptionType extends Throwable> {

        public abstract void call() throws ExceptionType;
    }

    @FunctionalInterface
    public static interface ExceptionableSupplier<ExceptionType extends Throwable, OutputType extends Object> {

        public abstract OutputType get() throws ExceptionType;
    }

    @FunctionalInterface
    public static interface ExceptionableConsumer<ExceptionType extends Throwable, InputType extends Object> {

        public abstract void accept(InputType input) throws ExceptionType;
    }

    @FunctionalInterface
    static interface ExceptionableFunction<ExceptionType extends Throwable, InputType extends Object, OutputType extends Object> {

        public abstract OutputType apply(InputType input) throws ExceptionType;
    }

    public static <ThrowableType extends Throwable> List<String> genericReporter(Throwable x) {
        List<String> list = new ArrayList<>();
        list.add(x.getClass().getSimpleName() + ": " + x.getMessage());

        Arrays.asList(x.getStackTrace())
              .stream()
              .map(m -> m.getFileName() + " -> " + m.getMethodName() + "() line " + m.getLineNumber())
              .forEachOrdered(list::add);
        return list;
    }

} //end of class Coalescer
