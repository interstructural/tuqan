package pl.zenit.tuqan.lang.statement;

import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.expression.Expression;
import pl.zenit.tuqan.lang.struct.TuqanObject;

public interface TuqanStatement<Type extends TuqanObject> {
            
      public abstract Type processInput(Expression input, TuqanContext context) throws ParsingFuckup;
      
} 

