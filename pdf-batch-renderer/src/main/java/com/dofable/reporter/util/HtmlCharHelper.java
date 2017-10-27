package com.dofable.reporter.util;

public class HtmlCharHelper {

    /**
     * 替换掉所有的连续空格，html解析的时候会把连续的空格字符合并为一个
     *
     * @param string
     * @return
     */
    public static String replaceBlankChar(String string) {
        string = replaceToHtmlAmp(string); // 替换&，这个是特殊字符
        string = replaceToHtmlLt(string); // 替换<，这个是特殊字符
        string = string.replace(" ", "&#160;"); // 保留空格,这里不能使用&nbsp

        return string;
    }

    /**
     * 替换字符串中的&为html中的&amp;
     *
     * @param string
     * @return
     */
    public static String replaceToHtmlAmp(String string) {
        return string.replace("&", "&amp;");
    }

    /**
     * 替换字符串中的<为html中的&lt;
     *
     * @param string
     * @return
     */
    public static String replaceToHtmlLt(String string) {
        return string.replace("<", "&lt;");
    }
}
