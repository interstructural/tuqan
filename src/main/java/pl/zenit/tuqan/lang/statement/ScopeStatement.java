package pl.zenit.tuqan.lang.statement;

import pl.zenit.tuqan.lang.Keywords;
import pl.zenit.tuqan.lang.struct.TuqanField;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.struct.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.CodeFormatting;
import pl.zenit.tuqan.lang.TuqanContext;
import pl.zenit.tuqan.lang.struct.DataTypeInfo;
import pl.zenit.tuqan.lang.struct.FieldType;
import pl.zenit.tuqan.lang.struct.ScopeInfo;
import pl.zenit.tuqan.lang.expression.Expression;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.expression.TextExpression;
import pl.zenit.tuqan.generators.literals.TuqanLiterals;

/**
 * CREATE SCOPE ScopeName (TYPE1 NAME1, TYPE2 NAME2, TYPE3 NAME3)
 */
public class ScopeStatement implements TuqanStatement<TuqanScopeclass> {

    private static final String dblq = "\"";
    
    private final TuqanExecutionParameters params;

    public ScopeStatement(TuqanExecutionParameters params) {
        this.params = params;
    }
      
    @Override public TuqanScopeclass processInput(Expression input, TuqanContext context) throws ParsingFuckup {
        List<TuqanField> fields = new ArrayList<>();

        if ( input == null ) {
            throw new ParsingFuckup("input is null");
        }
        if ( !(input instanceof TextExpression) ) {
            throw new ParsingFuckup("input is not parsable");
        }

        String inputText = CodeFormatting.fullTrim(new Upcaster(input).as(TextExpression.class).getValue());
        inputText = inputText.substring(StatementParser.createTag.length());
        inputText = CodeFormatting.fullTrim(inputText);

        if ( inputText.isEmpty() ) {
            throw new ParsingFuckup("expression is empty");
        }
        if ( !inputText.contains("(") ) {
            throw new ParsingFuckup(dblq + "(" + dblq + " expected");
        }
        if ( !inputText.contains(")") ) {
            throw new ParsingFuckup(dblq + ")" + dblq + " expected");
        }        
        if (!areTagsValid(inputText, "(", ")")) {
            throw new ParsingFuckup("bad descriptor ( ) format ");
        }
        
        //all before the brackets
        String pre = inputText.substring(0, inputText.indexOf("("));
        
        boolean isVirtual = false;
        if (pre.startsWith(Keywords.VIRTUAL.name())) {
            isVirtual = true;
            pre = pre.substring(Keywords.VIRTUAL.name().length());
        }
        pre = CodeFormatting.fullTrim(pre);

        boolean isMirage = false;
        if (pre.startsWith(Keywords.MIRAGE.name())) {
            isMirage = true;
            pre = pre.substring(Keywords.MIRAGE.name().length());
        }
        pre = CodeFormatting.fullTrim(pre);

        boolean isDefaultScope = pre.toUpperCase().startsWith(Keywords.SCOPE.name().toUpperCase());
        boolean isLedger = pre.toUpperCase().startsWith(Keywords.LEDGER.name().toUpperCase());
        
        if (isDefaultScope) {
            pre = pre.substring(Keywords.SCOPE.name().length());
        }
        else if (isLedger) {
            pre = pre.substring(Keywords.LEDGER.name().length());
        }
        else throw new ParsingFuckup("unknown enitity type in: " + pre);

        String scopeName = CodeFormatting.fullTrim(pre);

        if ( scopeName.isEmpty() ) {
            throw new ParsingFuckup("scope name is empty");
        }
        if ( scopeName.equals(scopeName.toUpperCase()) ) {
            throw new ParsingFuckup("scope name (\"" + scopeName + "\") cannot be upper cased");
        }
        
        
        if (scopeName.contains(" ")) {
            throw new ParsingFuckup("scope name (\"" + scopeName + "\") cannot contain a space");
        }

        //all after them brackets
        String inside = inputText.substring(inputText.indexOf("("));
        inside = CodeFormatting.fullTrim(inside);
        inside = inside.substring("(".length());
        inside = inside.substring(0, inside.lastIndexOf(")"));
        inside = CodeFormatting.fullTrim(inside);
        List<String> sl = Arrays.asList(inside.split(","));
                
        for ( int i = 0; i < sl.size(); ++i ) {
            String delimitedItem = sl.get(i);
            delimitedItem = CodeFormatting.fullTrim(delimitedItem);
            delimitedItem = CodeFormatting.normalizeBlankChars(delimitedItem);
            
            List<String> fline = new ArrayList<>();
            fline.addAll(Arrays.asList(delimitedItem.split(" ")));
            fline.removeIf(f -> f.isEmpty());   

            String name = "";
            String type = "";
            String param = "";
            if ( fline.size() == 2 ) {
                //String nazwaZmiennej
                //NazwaEnuma nazwaZmiennej
                type = fline.get(0).trim();
                name = fline.get(1).trim();
            }
            else if ( fline.size() == 3) {
                //CHILDREN typ nazwa
                //CHILD typ nazwa
                type = fline.get(0).trim();
                param = fline.get(1).trim();
                name = fline.get(2).trim();
            }
            else if ( fline.size() == 4 ) {
                //LINK OF Typ nazwaZmiennej
                //LIST OF Typ nazwaZmiennej
                type = fline.get(0).trim() + " " + fline.get(1).trim();
                param = fline.get(2).trim();
                name = fline.get(3).trim();
            }
            else {
                throw new ParsingFuckup("cant infer field from " + dblq + sl.get(i) + dblq);
            }
            
            DataType datatype = TuqanLiterals.fromTuqanCode(type);
            DataTypeInfo datatypeInfo = getDatatypeInfo(type, param);

            if ( name.equals(name.toUpperCase()) ) {
                throw new ParsingFuckup("object names (\"" + name + "\") cannot be upper cased");
            }

            fields.add(new TuqanField(name, new FieldType(datatype, datatypeInfo), context));
        }

        if ( fields.isEmpty() ) {
            throw new ParsingFuckup("scope " + dblq + scopeName + dblq + " has no fields");
        }

        ArrayList<TuqanField> out = new ArrayList<>();

        if ( params.getBasic().getAddId() ) {
            out.add(TuqanField.FIELD_ID);
        }

        out.addAll(fields);
        
        scopeName = CodeFormatting.firstWordLargeLetter(scopeName);        
        
        ScopeInfo scopeInfo = new ScopeInfo(scopeName, isLedger, isVirtual, isMirage);
        return new TuqanScopeclass(scopeInfo, out, context);
    }
    
    private DataTypeInfo getDatatypeInfo(String type, String param) throws ParsingFuckup {
        DataType datatype = TuqanLiterals.fromTuqanCode(type);
        switch (datatype) {
            default: 
                return DataTypeInfo.primitive();
            case ENUM: 
                return DataTypeInfo.forTarget(type);
            case LINK:
            case CHILD:
            case LIST:
            case CHILDREN:
                if (CodeFormatting.fullTrim(param).isEmpty())
                    throw new ParsingFuckup("target scope not defined.");
                return DataTypeInfo.forTarget(param);
        }
    }
    
    public String substringBetween(String input, String openingTag, String closingTag) {
        int startIndex = input.indexOf(openingTag);
        int endIndex = input.lastIndexOf(closingTag);

        if ( startIndex != -1 && endIndex != -1 && startIndex < endIndex ) {
            return input.substring(0, startIndex) + input.substring(endIndex + closingTag.length());
        }
        return input;
    }
    
    public String removeTagsAndContent(String input, String openingTag, String closingTag) {
        int startIndex = input.indexOf(openingTag);
        int endIndex = input.lastIndexOf(closingTag);

        if ( startIndex != -1 && endIndex != -1 && startIndex < endIndex ) {
            return input.substring(0, startIndex) + input.substring(endIndex + closingTag.length());
        }
        return input;
    }

    public boolean areTagsValid(String input, String openingTag, String closingTag) {
        int startIndex = input.indexOf(openingTag);
        int endIndex = input.indexOf(closingTag);

        boolean noTagsPresent = (startIndex == -1 && endIndex == -1);
        boolean tagsInOrder = (startIndex != -1 && endIndex != -1 && startIndex < endIndex);

        return noTagsPresent || tagsInOrder;
    }

} //end of class CreateStatement
