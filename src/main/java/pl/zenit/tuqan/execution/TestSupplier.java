package pl.zenit.tuqan.execution;

import java.util.List;

public interface TestSupplier {

    public abstract List<TestSupplier.RunnableAsTest> getTests(); 
    
    public static interface RunnableAsTest {
        public abstract void runTest() throws TestFuckup; 
    } 

}
