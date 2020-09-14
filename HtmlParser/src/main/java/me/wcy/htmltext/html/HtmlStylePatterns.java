package me.wcy.htmltext.html;

import java.util.regex.Pattern;

public class HtmlStylePatterns {

    private static Pattern sTextAlignPattern;
    private static Pattern sForegroundColorPattern;
    private static Pattern sBackgroundColorPattern;
    private static Pattern sTextDecorationPattern;
    private static Pattern sFontFamilyPattern;//font-family
    private static Pattern sFontSizePattern;//font-size
    private static Pattern sTextIndentPattern;//text-indent:2em

    public static Pattern getTextAlignPattern() {
        if (sTextAlignPattern == null) {
            sTextAlignPattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b");
        }
        return sTextAlignPattern;
    }

    public static Pattern getForegroundColorPattern() {
        if (sForegroundColorPattern == null) {
            sForegroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)color\\s*:\\s*(\\S*)\\b");
        }
        return sForegroundColorPattern;
    }

    public static Pattern getBackgroundColorPattern() {
        if (sBackgroundColorPattern == null) {
            sBackgroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)background(?:-color)?\\s*:\\s*(\\S*)\\b");
        }
        return sBackgroundColorPattern;
    }

    public static Pattern getTextDecorationPattern() {
        if (sTextDecorationPattern == null) {
            sTextDecorationPattern = Pattern.compile(
                    "(?:\\s+|\\A)text-decoration\\s*:\\s*(\\S*)\\b");
        }
        return sTextDecorationPattern;
    }

    public static Pattern getFontFamilyPattern() {
        if (sFontFamilyPattern == null) {
            sFontFamilyPattern = Pattern.compile(
                    "(?:\\s+|\\A)font-family\\s*:\\s*(\\S*)\\b");
        }
        return sFontFamilyPattern;
    }

    public static Pattern getFontSizePattern() {
        if (sFontSizePattern == null) {
            sFontSizePattern = Pattern.compile(
                    "(?:\\s+|\\A)font-size\\s*:\\s*(\\S*)\\b");
        }
        return sFontSizePattern;
    }

    public static Pattern getTextIndentPattern() {
        if (sTextIndentPattern == null) {
            sTextIndentPattern = Pattern.compile(
                    "(?:\\s+|\\A)text-indent\\s*:\\s*(\\S*)\\b");
        }
        return sTextIndentPattern;
    }
}
