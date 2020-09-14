package me.wcy.htmltext.html;

public class Html {

    public static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0x00000000;

    public static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 0x00000001;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 0x00000001;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 0x00000002;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 0x00000004;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 0x00000008;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 0x00000010;

    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 0x00000020;

    public static final int FROM_HTML_OPTION_USE_CSS_COLORS = 0x00000100;

    public static final int FROM_HTML_MODE_LEGACY = 0x00000000;

    public static final int FROM_HTML_MODE_COMPACT = FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    | FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                    | FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    | FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE;

    private static final int TO_HTML_PARAGRAPH_FLAG = 0x00000001;
}
