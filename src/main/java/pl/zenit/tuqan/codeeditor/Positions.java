package pl.zenit.tuqan.codeeditor;

class Positions {

    public static final String scopeOpen = "(";
    public static final String scopeClose = ")";
    public static final String commentOpen = "/*";
    public static final String commentClose = "*/";

    public static Positions next(String input, int offset) {
        Positions p = new Positions();
        p.scopeOpenPos = input.indexOf(scopeOpen, offset);
        p.scopeClosePos = input.indexOf(scopeClose, offset);
        p.commentOpenPos = input.indexOf(commentOpen, offset);
        p.commentClosePos = input.indexOf(commentClose, offset);
        return p;
    }

    private int scopeOpenPos;
    private int scopeClosePos;
    private int commentOpenPos;
    private int commentClosePos;

    private Positions() {
    }

    public int getScopeOpenPos() {
        return scopeOpenPos;
    }

    public int getScopeClosePos() {
        return scopeClosePos;
    }

    public int getCommentOpenPos() {
        return commentOpenPos;
    }

    public int getCommentClosePos() {
        return commentClosePos;
    }

    public boolean anyOpened() {
        return scopeOpenPos != -1 || commentOpenPos != -1;
    }

    public boolean anyClosed() {
        return scopeClosePos != -1 || commentClosePos != -1;
    }
    
    public int behindScopeOpenPos() {
        return scopeOpenPos + scopeOpen.length();
    }

    public int behindScopeClosePos() {
        return scopeClosePos + scopeClose.length();
    }

    public int behindCommentOpenPos() {
        return commentOpenPos + commentOpen.length();
    }

    public int behindCommentClosePos() {
        return commentClosePos + commentClose.length();
    }

}
