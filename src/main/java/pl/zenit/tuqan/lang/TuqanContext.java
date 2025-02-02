package pl.zenit.tuqan.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.ProcessingFuckup;
import pl.zenit.tuqan.util.Upcaster;

/**
 * CONTEXT IS MUTABLE AND VOLATILE
 */
public class TuqanContext {
    
    private static TuqanContext current;
    @Deprecated public static TuqanContext getCurrent() {
        return current;
    }
    @Deprecated public static void setCurrent(TuqanContext current) {
        TuqanContext.current = current;
    }    

      private final List<TuqanEnumclass> enums = new ArrayList<>();
      private final List<TuqanScopeclass> scopes = new ArrayList<>();

      public TuqanEnumclass findEnum(String name) {
            return enums.stream()
                  .filter(p-> p.getName().equalsIgnoreCase(name))
                  .findFirst().orElse(null);
      }
      
      public TuqanScopeclass findScope(String name) {
            return scopes.stream()
                  .filter(p-> p.getName().equalsIgnoreCase(name))
                  .findFirst().orElse(null);
      }

      public List<TuqanEnumclass> getEnums() {
            return enums.stream().collect(Collectors.toList());
      }

      public List<TuqanScopeclass> getAllScopes() {
            return scopes.stream().collect(Collectors.toList());
      }
      
      public List<TuqanScopeclass> getDefaultScopes() {
            return scopes.stream().filter(p-> !p.isLedger()).collect(Collectors.toList());
      }

      public List<TuqanScopeclass> getLedgers() {
            return scopes.stream().filter(p-> p.isLedger()).collect(Collectors.toList());
      }

      public List<TuqanScopeclass> getMirages() {
        return scopes.stream().filter(p-> p.isMirage()).collect(Collectors.toList());
    }
      
      public void register(TuqanObject object) throws ProcessingFuckup {
            if (findEnum(object.getName()) != null)
            throw new ProcessingFuckup("multiple use of \"" + object.getName() + "\"");
                  
            if (findScope(object.getName()) != null)
            throw new ProcessingFuckup("multiple use of \"" + object.getName() + "\"");
            
            if (object instanceof TuqanEnumclass)
                  enums.add(new Upcaster(object).asInferred());
            
            else if (object instanceof TuqanScopeclass)
                  scopes.add(new Upcaster(object).asInferred());
            
            else throw new ProcessingFuckup("cannot handle unknown tuqan object");
      }
      
} //end of class TuqanMemory
