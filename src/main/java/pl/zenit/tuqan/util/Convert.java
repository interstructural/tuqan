package pl.zenit.tuqan.util;

import java.util.Date;

public class Convert {
      
      public static int strToInt(String s, int defValue) {
            try {
                  return Integer.parseInt(s);
            }
            catch (Throwable th) {
                  return defValue;
            }
      }
      
      public static String longToStr(long l) {
            return Long.toString(l);
      }
      
      /** negative = no finite precision */
      public static String doubleToStr(double d, int precision) {
            if (precision < 0)
                   return Double.toString(d);
            return String.format("%." + precision + "f", d);
      }

      public static long strToLong(String s) {
            return strToLong(s, 0);
      }
      public static long strToLong(String s, long defValue) {
            try {
                  return Long.parseLong(s);
            }
            catch (Throwable th) {
                  return defValue;
            }
      }
      
      public static String dateToStr(Date d) {
            return longToStr(d.getTime());
      }
      
      public static Date strToDate(String s) {
            return new Date(strToLong(s));
      }
      
      public static double strToDouble(String s) {
            return strToDouble(s, 0);
      }

      public static double strToDouble(String s, double defValue) {
            try {
                  return Double.parseDouble(s);
            }
            catch (Throwable th) {
                  return defValue;
            }
      }

} //end of class Longin
