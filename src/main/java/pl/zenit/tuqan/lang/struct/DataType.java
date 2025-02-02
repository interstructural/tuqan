package pl.zenit.tuqan.lang.struct;

public enum DataType {
        NONE,       //zły typ
        BOOL,
        INT,        //całkowite
        LONG,       //całkowite ale duże
        FLOAT,      //wszelkie przecinkowe
        STRING,
        //DATE,       //mógłby taki być dzień w kalendarzu (czy to się w ogóle da trzymać w bazie bez odniesienia do punktu w czasoprzestrzeni typu unix epoch? w formie YYYMMDD? xD
        //DATETIME,   //mógłby taki być dzień + czas. czyli kalendarzowy format dla timestampu+timezone? 1 postać normalna???
        ENUM,
        BINARY,     //byte[]
        CHILD,
        LINK,
        LIST,
        CHILDREN
    ;
}
