package pl.zenit.tuqan.codeeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;


public class TuqanFoldParser implements FoldParser {
    
    private static class Cache {
        public volatile Fold currentFold = null;
        public volatile int ptr = 0;
        public volatile ParserState state = ParserState.NONE;
        public final RSyntaxTextArea textArea;

        public Cache(RSyntaxTextArea textArea) {
            this.textArea = textArea;
        }
    }
    private static enum ParserState {
        NONE, SCOPE, COMMENT
    }    
	
	@Override
	public List<Fold> getFolds(RSyntaxTextArea textArea) {
		List<Fold> folds = new ArrayList<>();
        
		String code = textArea.getText();
        if (code == null || code.length() < 2) return folds;
        
        try {            
            Cache cache = new Cache(textArea);
            while (true) {                 
                Positions p = Positions.next(code, cache.ptr);
                if (cache.state == ParserState.NONE) {
                        if (parserCycleNone(p, cache)) continue;
                    else break;
                }
                else if (cache.state == ParserState.SCOPE) {
                    if (parserCycleScope(p, cache)) {
                        folds.add(cache.currentFold);
                        cache.currentFold = null;
                        continue;
                    }
                    else break;
                }
                else if (cache.state == ParserState.COMMENT) {
                    if (parserCycleComment(p, cache)) {
                        folds.add(cache.currentFold);
                        cache.currentFold = null;
                        continue;
                    }
                    else break;
                }
            }
        }
        catch (BadLocationException ex) {
            Logger.getLogger(TuqanFoldParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            return folds;
        }
	}
    
    private boolean parserCycleNone(Positions p, Cache cache) throws BadLocationException {
        if (!p.anyOpened()) 
            return false;
        int s = p.getScopeOpenPos();
        int c = p.getCommentOpenPos();
        
        if (s > -1 && c > -1) {
            boolean scope = s < c;
            if (scope) 
                openScopeFold(p, cache);
            else openCommentFold(p, cache);
        }
        else if (s > -1) openScopeFold(p, cache);        
        else if (c > -1) openCommentFold(p, cache);
        
        return true;
    }
    private void openScopeFold(Positions p, Cache cache) throws BadLocationException {
        int foldStart = p.getScopeOpenPos();
        int foldType = FoldType.CODE;
        cache.state = ParserState.SCOPE;
        cache.ptr = p.behindScopeOpenPos();
        cache.currentFold = new Fold(foldType, cache.textArea, foldStart);
    }
    private void openCommentFold(Positions p, Cache cache) throws BadLocationException {
        int foldStart = p.getCommentOpenPos();
        int foldType = FoldType.COMMENT;
        cache.state = ParserState.COMMENT;
        cache.ptr = p.behindCommentOpenPos();
        cache.currentFold = new Fold(foldType, cache.textArea, foldStart);
    }
    
    private boolean parserCycleScope(Positions p, Cache cache) throws BadLocationException {
        if (p.getScopeClosePos() == -1)
            return false;

        cache.currentFold.setEndOffset(p.getScopeClosePos());
        cache.ptr = p.behindScopeClosePos();
        cache.state = ParserState.NONE;
        return true;
    }    
    private boolean parserCycleComment(Positions p, Cache cache) throws BadLocationException {
        if (p.getCommentClosePos() == -1)
            return false;

        cache.currentFold.setEndOffset(p.getCommentClosePos());
        cache.ptr = p.behindCommentClosePos();
        cache.state = ParserState.NONE;
        return true;
    }

}
