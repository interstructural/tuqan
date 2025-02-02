package pl.zenit.tuqan.lang.expression;

public class TextExpression implements Expression<String> {

      private final String text;

      public TextExpression(String text) {
            this.text = text;
      }
      
      @Override public String getValue() {
            return text;
      }

}