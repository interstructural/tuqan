package pl.zenit.tuqan.lang.struct;

import pl.zenit.tuqan.lang.TuqanContext;

/** klasa wyrażająca first class citizen tuqana */
public abstract class TuqanEntangledEntity implements TuqanObject {
      
      private final TuqanContext context;

      public TuqanEntangledEntity(TuqanContext context) {
            this.context = context;
      }

      public TuqanContext getContext() {
            return context;
      }      

} //end of class
