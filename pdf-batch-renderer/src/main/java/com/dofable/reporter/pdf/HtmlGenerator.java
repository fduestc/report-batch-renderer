package com.dofable.reporter.pdf;

import com.dofable.reporter.util.FileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

/**
 * @author BigBear
 */
public class HtmlGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlGenerator.class);
    private static Random random = new Random(10011);

    /***
     * 根据路径名获取ftl模板
     *
     * @param templateName
     *          模板名字（name）
     * @return ftl模板
     */
    private static Template getTemplate(String templateName) {
        // ftl文件所在文件
        Configuration config = FreemarkerConfiguration.getConfiguration();

        Template template;
        try {
            template = config.getTemplate(templateName + ".ftl");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return template;
    }

    /***
     * 由ftl模板生成对应的html文件，返回html文件的路径
     *
     * @param templateName
     *          模板名字（name）
     * @param dataMap
     *          数据模型
     * @return
     * @throws Exception
     */
    public static String generateHTML(String templateName, Map<String, Object> dataMap) {
        String htmlPath = FreemarkerConfiguration.getTemplateHtmlPath(templateName);

        writeToFile(templateName, dataMap, new File(htmlPath));

        return htmlPath;
    }

    /**
     * 生成通知单css文件
     *
     * @param cssName css模板名称
     * @param dataMap 数据字典
     * @throws Exception
     */
    public static void generateCss(String cssName, Map<String, Object> dataMap) {
        String cssPath = FreemarkerConfiguration.getTemplateDir() + "ntc.css";

        // 通知单的css样式已经存在，重新生成
        File outFile = new File(cssPath);
        // 先删除
        try {
            FileUtils.forceDelete(outFile);
        } catch (IOException e) {
            throw new IllegalStateException();
        }

        writeToFile(cssName, dataMap, outFile);
    }

    private static void writeToFile(String fileName, Map<String, Object> dataMap, File outFile) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8.name()));
            Template tp = getTemplate(fileName);
            tp.process(dataMap, out);
            out.flush();
            out.close();
        } catch (IOException | TemplateException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 生成通知单css文件
     *
     * @param cssName css模板名称
     * @param dataMap 数据字典
     * @return cssPath css路径，文件名：ntc-当前毫秒.css
     * @throws Exception
     */
    public static String concurrentGenerateCss(String cssName, Map<String, Object> dataMap) {
        // 要求唯一，{@link java.util.Random#Random(long)}
        String cssPath = String.format("%sntc-%d-%d.css", FreemarkerConfiguration.getTemplateDir(),
                System.nanoTime(), random.nextLong());

        // 字符编码设置好
        writeToFile(cssName, dataMap, new File(cssPath));

        return cssPath;
    }
}
