package com.dofable.reporter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author BigBear
 */
public class FontHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FontHelper.class);
    public static final double WORD_FONT_SIZE_2_HTML_EM_SCALE = 12.0;

    private FontHelper() {
        // util class
    }

    /**
     * 获取通知单PDF支持的所有的中英文字体路径
     *
     * @return 通知单PDF支持的所有的中英文字体路径
     */
    public static List<String> supportedFontPaths() {
        String supportedFontRootDir = ClassLoader.getSystemResource("fonts").getPath();

        // 中文字体: 宋体 、黑体、 楷体 、隶书 、幼圆
        // 英文字体: Tohama/Arial/Times New Roman/Verdana
        return FileUtils.getFileNames(supportedFontRootDir, false);
    }

    /**
     * 获取HTML支持的中文字体
     *
     * @return SimSun, SimHei, KaiTi, LiSu, YouYuan
     */
    public static String supportedChineseFontFamily() {
        return "SimSun, SimHei, KaiTi, LiSu, YouYuan";
    }

    /**
     * 获取HTML支持的英文字体
     *
     * @return 'Times New Roman', Arial, Tohama, Verdana
     */
    public static String supportedEnglishFamily() {
        return "'Times New Roman', Arial, Tohama, Verdana";
    }

    /**
     * 将word的字体大小转换为html中的字体大小，依据是1em是12
     *
     * @param wordFontSize
     * @return
     */
    public static String htmlEmForWordFontSize(String wordFontSize) {
        int fontSize = Integer.parseInt(wordFontSize);
        return htmlEmForWordFontSize((double) fontSize);
    }

    /**
     * 将word的字体大小转换为html中的字体大小，依据是1em是12
     *
     * @param wordFontSize
     * @return
     */
    public static String htmlEmForWordFontSize(int wordFontSize) {
        return htmlEmForWordFontSize((double) wordFontSize);
    }

    /**
     * 将word的字体大小转换为html中的字体大小，依据是1em是12
     *
     * @param wordFontSize
     * @return
     */
    public static String htmlEmForWordFontSize(double wordFontSize) {
        // 1em是12
        double emSize = wordFontSize / WORD_FONT_SIZE_2_HTML_EM_SCALE;
        return emSize + "em;";
    }
}
