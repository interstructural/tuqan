package pl.zenit.tuqan.lang.struct;

import java.util.Arrays;
import java.util.List;

public class ReservedNoUseKeywords {
    
    public static List<String> get() {
        return Arrays.asList("desc", "order", "group", "join", "limit", "index", 
            "insert", "update", "delete", "select", "where", "status", 
            "class", "attribute", "all", "table", "view", "trigger", "database", "function");
    }
    
}
