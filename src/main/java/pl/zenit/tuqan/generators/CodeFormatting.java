package pl.zenit.tuqan.generators;

public class CodeFormatting {

      public static String firstWordLargeLetter(String text) {
            String firstWord = text.substring(0, secondWordCamelCaseIndex(text));
            String rest = text.substring(secondWordCamelCaseIndex(text));
            return firstWord.substring(0, 1).toUpperCase() + firstWord.substring(1).toLowerCase() + rest;
      }
      public static String firstWordSmallLetter(String text) {
            String firstWord = text.substring(0, secondWordCamelCaseIndex(text));
            String rest = text.substring(secondWordCamelCaseIndex(text));
            return firstWord.toLowerCase() + rest;
      }
      private static int secondWordCamelCaseIndex(String input) {
            for ( int i = 1 ; i < input.length() ; ++i ) 
            if (input.substring(i, 1).equals(input.substring(i, 1).toUpperCase()))
            return i;
            return -1;
      }

      public static String fullTrim(String s) {
            while (s.startsWith(" ") || s.endsWith(" ")) 
                  s = s.trim();
            return s;
      }
      public static String normalizeBlankChars(String s) {
          s = s
                .replace("\r", "\n")
                .replace("\n", " ")
                .replace("\b", " ")
                .replace("\t", " ")
            ;
            while (s.contains("  ")) 
                  s = s.replace("  ", " ");
            
            return s;
      }
      public static String allBlanksToSpace(String s) {
            return s
                  .replace("\r", " ")
                  .replace("\n", " ")
                  .replace("\t", " ")
            ;
      }
      
} //end of class CodeFormatting
