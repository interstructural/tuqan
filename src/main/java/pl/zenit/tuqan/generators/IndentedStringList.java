package pl.zenit.tuqan.generators;

import java.util.ArrayList;
import java.util.List;

public class IndentedStringList {

      private final List<String> sl = new ArrayList();

      private final Indent indent;

      public IndentedStringList(int indentSize) {
            indent = new Indent(indentSize);
      }
      
      public boolean add(String S) {
            return sl.add(indent.get() + S);
      }
      
      public int size() {
            return sl.size();
      }

      public boolean remove(Object o) {
            return sl.remove(o);
      }

      public void clear() {
            sl.clear();
      }

      public void popIndent() {
            indent.pop();
      }

      public void pushIndent() {
            indent.push();
      }
      
      public List<String> getAsStringList() {
            return new ArrayList<>(sl);
      }
      
      private class Indent {

            private final String base;
            private int level = 0;

            public Indent(int size) {
                  String b = "";
                  for (int i = 0 ; i < size ; ++i)
                        b += " ";
                  base = b;
            }

            public void reset() {
                  level = 0;
            }

            public void pop() {
                  --level;
                  if (level < 0) level = 0;
            }
            public void push() {
                  ++level;
            }
            public String get() {
                  String a = "";
                  for ( int i = 0 ; i < level ; ++i )
                        a += base;
                  return a;
            }

      }

} 
