package com.dofable.reporter.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author BigBear
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
    /**
     * 根据文件路径获取文件名,不含后缀名，考虑了文件路径中/符号和\\符号混合的情况
     *
     * @param path 文件的绝对路径名
     * @return 文件名
     */
    public static String getFileNameWithoutExtension(String path) {
        int slashIndex = path.lastIndexOf(StringUtils.SLASH);
        int backSlashIndex = path.lastIndexOf(StringUtils.BACK_SLASH);

        String fileName = path.substring(Math.max(slashIndex, backSlashIndex) + 1);

        int indexOfDot = fileName.lastIndexOf(StringUtils.DOT);
        return indexOfDot < 0 ? fileName : fileName.substring(0, indexOfDot);
    }

    /**
     * 递归获取指定目录下所有的文件的文件名
     *
     * @param baseDir
     * @return
     */
    public static List<String> getFileNames(String baseDir, boolean includeHiddenFile) {
        File baseFile = new File(baseDir);

        if (!baseFile.exists() || baseFile.isFile()) {
            return Collections.emptyList();
        }

        List<String> fileNameList = new ArrayList<>();

        File[] srcFiles = baseFile.listFiles();

        for (File file : srcFiles) {
            if (file.isDirectory()) {
                fileNameList.addAll(getFileNames(file.getAbsolutePath(), includeHiddenFile));
            } else {
                if (includeHiddenFile || !isHidden(file.getName())) {
                    fileNameList.add(file.getAbsolutePath());
                }
            }
        }

        return fileNameList;
    }

    private static boolean isHidden(String fileName) {
        return fileName.startsWith(StringUtils.DOT);
    }

    /**
     * 根据文件路径获取文件名
     *
     * @param path 文件的绝对路径名
     * @return 文件名
     */
    public static String getFileName(String path) {
        int slashIndex = path.lastIndexOf(StringUtils.SLASH);
        int backSlashIndex = path.lastIndexOf(StringUtils.BACK_SLASH);

        return path.substring(Math.max(slashIndex, backSlashIndex) + 1);
    }

    /**
     * 为指定路径创建文件
     *
     * @param path 指定路径
     * @throws IOException
     */
    public static void forceMkdir(String path) throws IOException {
        forceMkdir(new File(path));
    }

    public static void forceDelete(String path) throws IOException {
        forceDelete(new File(path));
    }
}
