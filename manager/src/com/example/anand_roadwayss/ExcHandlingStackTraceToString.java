package com.example.anand_roadwayss;

public class ExcHandlingStackTraceToString {

    /**
     * Convert the result of Exception.getStackTrace to a String
     */
    public static String StackTraceToString(Exception ex) {
        String result = ex.toString() + "\n";
        StackTraceElement[] trace = ex.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            result += trace[i].toString() + "\n";
        }
        return result;
    }
}