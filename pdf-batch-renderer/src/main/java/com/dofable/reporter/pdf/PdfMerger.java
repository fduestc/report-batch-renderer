package com.dofable.reporter.pdf;

import com.dofable.reporter.util.FileUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF合并和分裂工具类
 *
 * @author BigBear
 */
public class PdfMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfMerger.class);

    private PdfMerger() {
        // util class
    }

    /**
     * 合并pdf
     *
     * @param pdfPaths 待合并pdf的文件路径数组
     * @param savePath 合并后，生成的pdf的保存路径
     */
    public static void merge(List<String> pdfPaths, String savePath) {
        if (pdfPaths == null || pdfPaths.isEmpty()) {
            return;
        }

        try {
            Document document = new Document(new PdfReader(pdfPaths.get(0)).getPageSize(1));

            PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(savePath));

            document.open();

            for (String pdfPath : pdfPaths) {
                PdfReader reader = new PdfReader(pdfPath);

                int nPages = reader.getNumberOfPages();

                for (int pageIndex = 1; pageIndex <= nPages; pageIndex++) {
                    document.newPage();
                    PdfImportedPage page = pdfCopy.getImportedPage(reader, pageIndex);
                    pdfCopy.addPage(page);
                }
            }

            document.close();
        } catch (IOException | DocumentException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 把一个pdf文件分割为多个pdf文件
     *
     * @param pdfPath       待分割pdf的文件路径
     * @param numberOfPDFs  分为多少份
     * @param outputBaseDir 输出路径
     */
    public static void split(String pdfPath, int numberOfPDFs, String outputBaseDir) {
        PdfReader reader;
        try {
            reader = new PdfReader(pdfPath);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        int totalPageNum = reader.getNumberOfPages();

        if (totalPageNum < numberOfPDFs) {
            throw new IllegalArgumentException(String.format("The document does not have %s pages to partition !", numberOfPDFs));
        }

        outputBaseDir = appendSeparator(outputBaseDir);

        // 每一个PDF的页数
        int pagesPerPDF = totalPageNum / numberOfPDFs;

        // 输出PDF文件名
        ArrayList<String> pdfNames = getOutPdfNames(pdfPath, numberOfPDFs, outputBaseDir);

        // 生成前n-1个完整PDF
        for (int pdfIndex = 0; pdfIndex < numberOfPDFs; pdfIndex++) {
            Document document = new Document(reader.getPageSize(1));

            String savePath = pdfNames.get(pdfIndex);

            try {
                // 曾经生成过则删除
                PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(savePath));
                document.open();

                // 生成最后一个PDF，可能页数比前面更多，因此单独处理
                int ceiling = (pdfIndex == numberOfPDFs - 1) ? totalPageNum : pagesPerPDF * (pdfIndex + 1);
                for (int j = pagesPerPDF * pdfIndex + 1; j <= ceiling; j++) {
                    document.newPage();
                    PdfImportedPage page = pdfCopy.getImportedPage(reader, j);
                    pdfCopy.addPage(page);
                }
            } catch (DocumentException | IOException e) {
                throw new IllegalStateException(e);
            }

            document.close();
        }
    }

    private static String appendSeparator(String outputBaseDir) {
        if (!outputBaseDir.endsWith(File.separator)) {
            outputBaseDir += File.separator;
        }
        return outputBaseDir;
    }

    private static ArrayList<String> getOutPdfNames(String pdfPath, int numberOfPDFs, String outputBaseDir) {
        String pdfName = FileUtils.getFileNameWithoutExtension(pdfPath);
        ArrayList<String> savePaths = new ArrayList<>();
        for (int i = 1; i <= numberOfPDFs; i++) {
            String savePath = outputBaseDir + pdfName + i + ".pdf";
            savePaths.add(savePath);
        }
        return savePaths;
    }
}
