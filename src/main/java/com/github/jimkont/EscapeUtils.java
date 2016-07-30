package com.github.jimkont;

/**
 * @author Dimitris Kontokostas
 * @since 29/7/2016 10:50 μμ
 */
public final class EscapeUtils {
    private EscapeUtils() {}

    public static String escapeNodeName(String name) {
        return name
                .replace("-","")
                .replace(":", "_")
                .replace(".","_");
    }

    public static String escapeStringValue(String value) {
        return value.replace("\"", "\\\"");
    }
}
