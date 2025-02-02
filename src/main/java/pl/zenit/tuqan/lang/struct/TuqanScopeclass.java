package pl.zenit.tuqan.lang.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.lang.TuqanContext;

public class TuqanScopeclass extends TuqanEntangledEntity {

      private final String scopeName;
      
      private final boolean ledger;
      
      private final boolean virtual;

    private final boolean mirage;
      
      private final ArrayList<TuqanField> fields = new ArrayList<>();

      public TuqanScopeclass(ScopeInfo info, List<TuqanField> fields, TuqanContext context) {
            super(context);
            this.scopeName = info.name;
            this.ledger = info.isLedger();
            this.virtual = info.isVirtual();
            this.mirage = info.isMirage();
            this.fields.addAll(fields);
      }

      @Override public String getName() {
            return scopeName;
      }

      public List<TuqanField> getFields() {
            return fields.stream().collect(Collectors.toList());
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
