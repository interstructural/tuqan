package pl.zenit.tuqan.codeeditor;

import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import pl.zenit.tuqan.lang.struct.ReservedNoUseKeywords;

public class TuqanTokenMaker extends AbstractTokenMaker {
    
    private boolean ignoreCase = true;

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;
        int end = offset + count;

        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        int newStartOffset = startOffset - offset;

        int currentTokenStart = offset;
        int currentTokenType = initialTokenType;

        for (int i = offset; i < end; i++) {
            char c = Character.toUpperCase(array[i]);
            if (ignoreCase)
                c = Character.toUpperCase(c);
            
            switch (currentTokenType) {
                //---------------------------------------------------------------------------------------------------
                case Token.NULL:
                    currentTokenStart = i;   // Starting a new token here.
                    switch (c) {
                        case ' ':
                        case '\t':                        
                        case '[':
                        case ']':
                        case ',':
                        case ';':
                        case '(':
                        case ')':
                            currentTokenType = Token.WHITESPACE;                            
                            break;
                        case '/': 
                            currentTokenType = Token.COMMENT_MULTILINE;                            
                            break;
                        case '"':
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        default:
                            if (RSyntaxUtilities.isDigit(c)) {
                                currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                                break;
                            } 
                            else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }                            
                            currentTokenType = Token.IDENTIFIER; // Anything not currently handled - mark as an identifier
                            break;
                    } // End of switch (c).
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.WHITESPACE:
                    switch (c) {
                        case ' ':
                        case '\t':                        
                        case '[':
                        case ']':
                        case ',':
                        
                        case '(':
                        case ')':
                            break;   // Still whitespace.
                        case '~': //( )
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.SEPARATOR;                            
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        case '#':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.COMMENT_EOL;
                            break;
                        default:   // Add the whitespace token and start anew.
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            if (RSyntaxUtilities.isDigit(c)) {
                                currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                                break;
                            }
                            else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }
                            currentTokenType = Token.IDENTIFIER;                            // Anything not currently handled - mark as identifier
                    } // End of switch (c).
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.LITERAL_NUMBER_DECIMAL_INT:
                    switch (c)
                    {
                        case ' ':
                        case '\t':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case ',':
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;

                        default:

                            if (RSyntaxUtilities.isDigit(c))
                            {
                                break;   // Still a literal number.
                            }

                            // Otherwise, remember this was a number and start over.
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset + currentTokenStart);
                            i--;
                            currentTokenType = Token.NULL;

                    } // End of switch (c).

                    break;
                //----------------------------------------------------------------------
                case Token.SEPARATOR:
                    i = end - 1;
                    addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
                    // We need to set token type to null so at the bottom we don't add one more token.
                    currentTokenType = Token.NULL;
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.LITERAL_STRING_DOUBLE_QUOTE:
                    if (c == '"')
                    {
                        addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset + currentTokenStart);
                        currentTokenType = Token.NULL;
                    }
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.COMMENT_MARKUP:
                    switch (c) {
                        case '*':
                            currentTokenType = Token.COMMENT_MULTILINE;
                            break;
                            
                        //case '/': 
                        //    i = end - 1;
                        //    addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
                            // We need to set token type to null so at the bottom we don't add one more token.
                        //    currentTokenType = Token.NULL;
                        //    break;
                        default: 
                            break; //still a comment
                    }
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.COMMENT_MULTILINE:
                    switch (c) {
                        case '*':
                            currentTokenType = Token.COMMENT_EOL;
                            break;
                        default: 
                            break; //still a comment
                    }
                    break;
                //---------------------------------------------------------------------------------------------------
                case Token.COMMENT_EOL:
                    switch (c) {
                        case '/':
                            addToken(text, currentTokenStart, i, Token.COMMENT_MULTILINE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.NULL;
                            break;
                        
                        default: 
                            currentTokenType = Token.COMMENT_MULTILINE;
                            break;
                    }
                    break;
                //---------------------------------------------------------------------------------------------------
                default: // Should never happen
                case Token.IDENTIFIER:
                    switch (c) {
                        case ' ':
                        case '\t':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case ',':
                        case ';':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;
                        case '*':
                            currentTokenType = Token.COMMENT_MULTILINE;
                            break;
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        default:
                            if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
                                break;   // Still an identifier of some type.
                            }
                            // Otherwise, we're still an identifier (?).
                    } // End of switch (c).
                    break;

            } // End of switch (currentTokenType).

        } // End of for (int i=offset; i<end; i++).

        switch (currentTokenType) {
            // Remember what token type to begin the next line with.
            case Token.LITERAL_STRING_DOUBLE_QUOTE:
                addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
                break;
            // Do nothing if everything was okay.
            case Token.NULL:
                addNullToken();
                break;
            // All other token types don't continue to the next line...
            default:
                addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
                addNullToken();
        }
        // Return the first token in our linked list.
        return firstToken;
    }

    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tokenMap = new TokenMap();

        //push ppl to use UPPER or lower case and not RetARDcaSe
        tokenMap.put("CREATE", Token.RESERVED_WORD);
        tokenMap.put("SCOPE", Token.RESERVED_WORD);
        tokenMap.put("ENUM", Token.RESERVED_WORD_2);
        tokenMap.put("LEDGER", Token.RESERVED_WORD_2);
        tokenMap.put("VIRTUAL", Token.FUNCTION);
        tokenMap.put("MIRAGE", Token.FUNCTION);
        
        tokenMap.put("BOOL", Token.DATA_TYPE);
        tokenMap.put("INT", Token.DATA_TYPE);
        tokenMap.put("LONG", Token.DATA_TYPE);
        tokenMap.put("FLOAT", Token.DATA_TYPE);
        tokenMap.put("STRING", Token.DATA_TYPE);
        tokenMap.put("DATE", Token.DATA_TYPE);
        tokenMap.put("TIME", Token.DATA_TYPE);
        tokenMap.put("DATETIME", Token.DATA_TYPE);
        tokenMap.put("BINARY", Token.DATA_TYPE);
        
        tokenMap.put("LINK", Token.FUNCTION);
        tokenMap.put("LIST", Token.FUNCTION);
        tokenMap.put("OF", Token.FUNCTION);
        tokenMap.put("CHILD", Token.FUNCTION);
        tokenMap.put("CHILDREN", Token.FUNCTION);
        
        ReservedNoUseKeywords.get().forEach(word-> tokenMap.put(word.toUpperCase(), Token.ERROR_IDENTIFIER));

        tokenMap.put("create", Token.RESERVED_WORD);
        tokenMap.put("scope", Token.RESERVED_WORD);
        tokenMap.put("enum", Token.RESERVED_WORD_2);
        tokenMap.put("ledger", Token.RESERVED_WORD_2);
        tokenMap.put("virtual", Token.FUNCTION);
        tokenMap.put("mirage", Token.FUNCTION);

        tokenMap.put("bool", Token.DATA_TYPE);
        tokenMap.put("int", Token.DATA_TYPE);
        tokenMap.put("long", Token.DATA_TYPE);
        tokenMap.put("float", Token.DATA_TYPE);
        tokenMap.put("string", Token.DATA_TYPE);
        tokenMap.put("date", Token.DATA_TYPE);
        tokenMap.put("time", Token.DATA_TYPE);
        tokenMap.put("datetime", Token.DATA_TYPE);
        tokenMap.put("binary", Token.DATA_TYPE);

        tokenMap.put("link", Token.FUNCTION);
        tokenMap.put("list", Token.FUNCTION);
        tokenMap.put("of", Token.FUNCTION);
        tokenMap.put("child", Token.FUNCTION);
        tokenMap.put("children", Token.FUNCTION);

        ReservedNoUseKeywords.get().forEach(word-> tokenMap.put(word.toLowerCase(), Token.ERROR_IDENTIFIER));
        
        tokenMap.put("(", Token.OPERATOR);
        tokenMap.put(")", Token.OPERATOR);
        
        tokenMap.put(";", Token.SEPARATOR);
        tokenMap.put("[", Token.SEPARATOR);
        tokenMap.put("]", Token.SEPARATOR);
        tokenMap.put(",", Token.SEPARATOR);

        tokenMap.put("!", Token.ERROR_CHAR);
        tokenMap.put("@", Token.ERROR_CHAR);
        tokenMap.put("#", Token.ERROR_CHAR);
        tokenMap.put("$", Token.ERROR_CHAR);
        tokenMap.put("%", Token.ERROR_CHAR);
        tokenMap.put("^", Token.ERROR_CHAR);
        tokenMap.put("&", Token.ERROR_CHAR);
        tokenMap.put("*", Token.ERROR_CHAR);
        tokenMap.put("{", Token.ERROR_CHAR);
        tokenMap.put("}", Token.ERROR_CHAR);
        tokenMap.put("'", Token.ERROR_CHAR);
        tokenMap.put("\"", Token.ERROR_CHAR);
        tokenMap.put("/", Token.ERROR_CHAR);
        tokenMap.put("?", Token.ERROR_CHAR);
        tokenMap.put("<", Token.ERROR_CHAR);
        tokenMap.put(">", Token.ERROR_CHAR);
        tokenMap.put(".", Token.ERROR_CHAR);
        
        return tokenMap;
    }
    
    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        // This assumes all keywords, etc. were parsed as "identifiers."
        if ( tokenType == Token.IDENTIFIER ) {
            int value = wordsToHighlight.get(segment, start, end);
            if ( value != -1 ) {
                tokenType = value;
            }
        }
        super.addToken(segment, start, end, tokenType, startOffset);
    }

}
