package org.logconsole;

import java.util.ArrayList;

public class SimpleTokenizer {
    /**
     * Like String.split(), but returns an empty token if a separator occurs right after another separator.
     * Designed to be very fast for parsing large amounts of data
     *
     * Examples:
     *   "a,b,c"   ==> "a", "b", "c",
     *   "a,,c"    ==> "a", "", "c",
     *   "a,,,c"   ==> "a", "", "", "c",
     *   ",a,b,c"  ==> "", "a", "b", "c",
     *   ",,a,b,c" ==> "", "", "a", "b", "c",
     *   "a,b,c,"  ==> "a", "b", "c", "",
     *   "a,b,c,," ==> "a", "b", "c", "", "",
     */
    public static String[] split(String string, char separator) {
        ArrayList<String> list = new ArrayList<>();
        int start = 0;
        int end;
        while (true) {
            end = string.indexOf(separator, start);
            if (end == -1)
                break;
            list.add(string.substring(start, end));
            start = end + 1;
            if (start == string.length())
                break;
        }
        list.add(string.substring(start));

        return list.toArray(new String[0]);
    }

    private static void test(String string, char separator) {
        String[] tokens = split(string, separator);
        System.out.printf("%s ==> ", string);
        for (String token : tokens)
            System.out.printf("\"%s\", ", token);
        System.out.println();
    }

    public static void main(String[] args) {
        test("a,b,c", ',');
        test("a,,c", ',');
        test("a,,,c", ',');
        test(",a,b,c", ',');
        test(",,a,b,c", ',');
        test("a,b,c,", ',');
        test("a,b,c,,", ',');
    }
}

