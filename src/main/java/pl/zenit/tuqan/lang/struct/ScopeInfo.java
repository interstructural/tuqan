package pl.zenit.tuqan.lang.struct;

public class ScopeInfo {

    public final String name;

    private final boolean ledger;

    private final boolean virtual;

    private final boolean mirage;

    public ScopeInfo(String name, boolean ledger, boolean virtual, boolean mirage) {
        this.name = name;
        this.ledger = ledger;
        this.virtual = virtual;
        this.mirage = mirage;
    }

    public String getName() {
        return name;
    }

    public boolean isLedger() {
        return ledger;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public boolean isMirage() {
        return mirage;
    }
}
