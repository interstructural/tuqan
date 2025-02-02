package pl.zenit.tuqan.lang.statement;

import java.util.Arrays;
import java.util.List;

import pl.zenit.tuqan.lang.Keywords;
import pl.zenit.tuqan.lang.expression.TextExpression;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public class StatementParser {

      public static final String createTag = "CREATE";
      public static final String virtualTag = "VIRTUAL";
      public static final String mirageTag = "MIRAGE";
      
      private final TuqanExecutionParameters params;
      private final TuqanContext context;

      public StatementParser(TuqanExecutionParameters params, TuqanContext context) {
            this.params = params;
            this.context = context;
      }

      public TuqanObject parse(TextExpression expression) throws ParsingFuckup {
            TuqanStatement statement = inferStatementFor(expression);
            if (statement == null) 
                  throw new ParsingFuckup("not a tuqan expression: \"" + expression.getValue() + "\"");
            return statement.processInput(expression, context);
      }
      
      private TuqanStatement inferStatementFor(TextExpression expression) throws ParsingFuckup {
            List<String> sl = Arrays.asList(expression.getValue().split(" "));

            if (sl.get(0).equalsIgnoreCase(createTag)) { //groupTag

                  String commandTag = sl.get(1);

                  if (commandTag.equalsIgnoreCase(Keywords.VIRTUAL.name())
                        || commandTag.equalsIgnoreCase(Keywords.MIRAGE.name()))
                        commandTag = sl.get(2);

                  Keywords kw = Keywords.BAD_TOKEN;
                  try {
                        kw = Keywords.valueOf(commandTag.toUpperCase());
                  }
                  catch (IllegalArgumentException ignored) {
                  }
                  switch (kw) {
                        case SCOPE:
                        case LEDGER:
                              return new ScopeStatement(params);
                        case ENUM:
                              return new EnumStatement(params);
                        case BAD_TOKEN:
                        default:
                              throw new ParsingFuckup("trying to CREATE unknown entity \"" + commandTag + "\"");
                  }
            }
            else throw new ParsingFuckup("unknown command \"" + sl.get(0) + "\"");
      }

}
