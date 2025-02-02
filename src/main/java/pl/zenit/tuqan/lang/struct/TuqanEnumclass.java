package pl.zenit.tuqan.lang.struct;

import java.util.ArrayList;
import java.util.List;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;

public class TuqanEnumclass extends TuqanEntangledEntity {
      
      private final String name;

      private final List<String> values = new ArrayList<>();
      
      public TuqanEnumclass(String name, List<String> values, TuqanContext context) throws ParsingFuckup {
            super(context);
            this.name = name;
            this.values.addAll(values);
      }
      
      @Override public String getName() {
            return name;
      }
      
      public List<String> getValues() {
            return new ArrayList<>(values);
      }

}
