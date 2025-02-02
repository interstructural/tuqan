package pl.zenit.tuqan.execution.parameters;

import java.util.HashMap;
import java.util.Map;

public class TuqanCustomParameters {

    private final Map<String, String> map = new HashMap<>();

    public TuqanCustomParameters() {
    }
    
    private TuqanCustomParameters(TuqanCustomParameters src) {
        this.map.putAll(src.map);
    }

    public String get(String paramName) {
        return map.get(paramName);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(map);
    }

    public void set(String paramName, String paramValue) {
        map.put(paramName, paramValue);
    }

    public void delete(String paramName) {
        if ( map.containsKey(paramName) ) {
            map.remove(paramName);
        }
    }

    void addAll(TuqanCustomParameters customParams) {
        this.map.putAll(customParams.map);
    }
    
} //end of class
