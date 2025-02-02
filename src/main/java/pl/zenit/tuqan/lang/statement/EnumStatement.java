package pl.zenit.tuqan.lang.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.Keywords;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.TuqanEnumclass;
import pl.zenit.tuqan.lang.expression.Expression;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.expression.TextExpression;
import pl.zenit.tuqan.util.Upcaster;

public class EnumStatement implements TuqanStatement<TuqanEnumclass> {

      private static final String dblq = "\"";

      private final TuqanExecutionParameters params;

      public EnumStatement(TuqanExecutionParameters params) {
            this.params = params;
      }
      
      @Override public TuqanEnumclass processInput(Expression input, TuqanContext context) throws ParsingFuckup {
            String enumName = "";
            List<String> enumValues = new ArrayList<>();

            if (input == null)
                  throw new ParsingFuckup("input is null");
            if (! (input instanceof TextExpression))
                  throw new ParsingFuckup("input is not parsable");

            String inputText = CodeFormatting.fullTrim(new Upcaster(input).as(TextExpression.class).getValue());
            inputText = inputText.substring(StatementParser.createTag.length());
            inputText = CodeFormatting.fullTrim(inputText);
            
            if (inputText.isEmpty())
                  throw new ParsingFuckup("expression is empty");
            if (!inputText.contains("("))
                  throw new ParsingFuckup(dblq + "(" + dblq + " expected");
            if (!inputText.contains(")"))
                  throw new ParsingFuckup(dblq + ")" + dblq + " expected");
                                    
            String pre = inputText.substring(0, inputText.indexOf("("));
            if (pre.toUpperCase().startsWith(Keywords.VIRTUAL.name())) {
                throw new ParsingFuckup("enum cannot be virtual");
            }
            
            pre = pre.substring(Keywords.ENUM.name().length());
            pre = CodeFormatting.fullTrim(pre);
            enumName = pre;
            
            if (enumName.isEmpty())
                  throw new ParsingFuckup("enum name is empty");
            
            if (enumName.equals(enumName.toUpperCase()))
                 throw new ParsingFuckup("object names (\"" + enumName + "\") cannot be upper cased");

            String inside = inputText.substring(inputText.indexOf("("));
            inside = CodeFormatting.fullTrim(inside);            
            inside = inside.substring("(".length());
            inside = inside.substring(0, inside.lastIndexOf(")"));
            inside = CodeFormatting.fullTrim(inside);
            List<String> sl = Arrays.stream(inside.split(",")).collect(Collectors.toList());

            for ( int i = 0 ; i < sl.size() ; ++i ) {
                  String value = CodeFormatting.fullTrim(sl.get(i));
                  if (value.isEmpty())
                        throw new ParsingFuckup("value of " +  dblq + enumName + dblq + " is empty!");
                  if (!value.equals(value.toUpperCase()))
                        throw new ParsingFuckup("enum values (\"" + value + "\") have to be upper cased!");
                  enumValues.add(value);
            }
                        
            if (enumValues.isEmpty())
                  throw new ParsingFuckup("enum " + dblq + enumName + dblq + " has no values");

            enumName = CodeFormatting.firstWordLargeLetter(enumName);
            return new TuqanEnumclass(enumName, enumValues, context);
      }
            
} //end of class CreateStatement
