package pl.zenit.tuqan.util;

/**
 * pl.zenit.koziel.lib.generic
 */
@SuppressWarnings("all")
public final class Upcaster {

    private final Object obj;

    public Upcaster(final Object obj) {
        this.obj = obj;
    }

    public Object asIs() {
        return obj;
    }

    public <type> type asInferred() {
        if ( obj == null ) {
            return null;
        }
        try {
            return (type) obj;
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    public <type> type as(final Class<type> clazz) {
        if ( obj == null ) {
            return null;
        }
        try {
            return (type) clazz.cast(obj);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    @Deprecated
    @Override
    public String toString() {
        return super.toString();
    }

} //end of class Upcaster
