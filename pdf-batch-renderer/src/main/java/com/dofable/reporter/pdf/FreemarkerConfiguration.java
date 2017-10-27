package com.dofable.reporter.pdf;

import com.dofable.reporter.util.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * freemaker 配置类
 *
 * @author XWQ
 */
public final class FreemarkerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerConfiguration.class);

    private static Configuration config;
    private static String templateDirectoryPath;
    private static Random random = new Random(10086);

    static {
        config = new Configuration();

        // /D:/tomcat/fee-dev/WEB-INF/classes/
        String resourcePath = FreemarkerConfiguration.class.getClassLoader().getResource("").getPath();

        // window下删掉路径开头的/，类unix系统中不需要
        if (resourcePath.contains(":") && resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        if (resourcePath.lastIndexOf("/") > 7) {
            int index = resourcePath.lastIndexOf("/classes");
            if (index != -1) {
                resourcePath = resourcePath.substring(0, index);
            }
        }

        // V3.35.0,考虑路径以分隔符结尾的情况，考虑完整
        if (resourcePath.endsWith(StringUtils.BACK_SLASH) || resourcePath.endsWith(StringUtils.SLASH)) {
            templateDirectoryPath = resourcePath + "template";
        } else {
            templateDirectoryPath = resourcePath + "/" + "template";
        }

        File file = new File(templateDirectoryPath);
        try {
            config.setDirectoryForTemplateLoading(file);
        } catch (IOException e) {

            LOGGER.error(e.getMessage(), e);
        }
        config.setObjectWrapper(new DefaultObjectWrapper());
        config.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    /**
     * 返回模板对应的html路径，要求唯一，所以使用了 {@link java.util.UUID#randomUUID()} 和
     * {@link Random#Random(long)}
     *
     * @param templateName
     * @return
     */
    public static String getTemplateHtmlPath(String templateName) {
        // 产生精确的唯一的标识
        String htmlPath = String.format("%s/temp/%s-%d-%d.html", templateDirectoryPath, templateName,
                System.nanoTime(), random.nextLong());

        return htmlPath;
    }

    /**
     * 获取模板所在的目录
     *
     * @return
     */
    public static String getTemplateDir() {

        // 产生唯一的标识
        return templateDirectoryPath + "/temp/";
    }

    public static Configuration getConfiguration() {
        return config;
    }

    public void setConfiguration(String baseDirectory) throws IOException {
        config.setDirectoryForTemplateLoading(new File(baseDirectory));
    }
}
