package pl.zenit.tuqan.codeeditor;

import java.awt.Color;
import java.awt.Font;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;

public class LanguageManager {
    
    public static void registerTuqanSyntax() {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/tuqan", "pl.zenit.tuqan.codeeditor.TuqanTokenMaker");
        FoldParserManager.get().addFoldParserMapping("text/tuqan", new TuqanFoldParser());
    }
    public static void setTuqanSyntax(RSyntaxTextArea area) {
        area.setSyntaxEditingStyle("text/tuqan"); 
    }
    
    public static void setTuqanColoring(RSyntaxTextArea area, Font font) {
        SyntaxScheme scheme = area.getSyntaxScheme();
        scheme.restoreDefaults(font);

        scheme.setStyle(Token.RESERVED_WORD, new Style(Color.BLACK));
        scheme.setStyle(Token.RESERVED_WORD_2, new Style( new Color(120, 0, 250) ));
        scheme.setStyle(Token.DATA_TYPE, new Style( new Color(0, 120, 0) ));
        scheme.setStyle(Token.FUNCTION, new Style( new Color(160, 120, 0) ));
        scheme.setStyle(Token.OPERATOR, new Style( new Color(160, 120, 0) ));
        scheme.setStyle(Token.SEPARATOR, new Style( Color.BLACK ));
        scheme.setStyle(Token.IDENTIFIER, new Style( Color.BLACK  ));
        scheme.setStyle(Token.COMMENT_MULTILINE, new Style( Color.GRAY ));
        scheme.setStyle(Token.ERROR_IDENTIFIER, new Style( Color.RED ));
        
        scheme.getStyle(Token.RESERVED_WORD).font = font.deriveFont(Font.BOLD);
        scheme.getStyle(Token.RESERVED_WORD_2).font = font.deriveFont(Font.BOLD);
        scheme.getStyle(Token.DATA_TYPE).font = font.deriveFont(Font.BOLD);
        scheme.getStyle(Token.FUNCTION).font = font.deriveFont(Font.BOLD);
        scheme.getStyle(Token.OPERATOR).font = font.deriveFont(Font.BOLD);
        scheme.getStyle(Token.COMMENT_MULTILINE).font = font.deriveFont(Font.ITALIC);
        scheme.getStyle(Token.ERROR_IDENTIFIER).font = font.deriveFont(Font.BOLD);
    }

    public static void prepareEditorForTuqanCode(RSyntaxTextArea codeEditor) {
        codeEditor.setBracketMatchingEnabled(true);
        codeEditor.setAnimateBracketMatching(true);
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setAntiAliasingEnabled(true);   
    }
}