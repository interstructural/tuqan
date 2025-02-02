package pl.zenit.tuqan.execution.parameters;

/** pls someone clean this up */
public class TuqanExecutionParameters {

    private final TuqanBasicParameters basic;

    private final TuqanEnvironmentalParameters enviourment;


    private final TuqanCustomParameters customParams = new TuqanCustomParameters();

    public TuqanExecutionParameters(TuqanBasicParameters basic, TuqanEnvironmentalParameters enviourment, TuqanCustomParameters customParams) {
        this.basic = basic;
        this.enviourment = enviourment;
        this.customParams.addAll(customParams);
    }

    public TuqanBasicParameters getBasic() {
        return basic;
    }

    public TuqanEnvironmentalParameters getEnviourment() {
        return enviourment;
    }

    public TuqanCustomParameters getCustomParams() {
        return customParams;
    }

} // end of class
