package com.dofable.reporter.demo;

import com.dofable.reporter.pdf.PdfRenderer;
import com.dofable.reporter.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.utilities.Assert;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * PDFRenderer supports headless rendering of XHTML documents, outputting
 * to PDF format. There is a static utility method, rendering
 * a {@link URL}, {@link #batchRender(String, String)} </p>
 * <p>You can use this utility from the command line by passing in
 * the base directory for the htmls as first parameter, and PDF output base path as second
 * parameter:
 * <pre>
 * java -cp %classpath% com.dofable.reporter.PdfRendererDemo
 * </pre>
 *
 * @author BigBear
 */
public class PdfRendererDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfRendererDemo.class);
    public static final int ARGS_COUNT = 2;
    public static final String HTML_SUFFIX = ".html";
    public static final String PDF_SUFFIX = ".pdf";

    /**
     * Renders a file or URL to a PDF. Command line use: first
     * argument is URL or file path, second
     * argument is path to PDF file to generate.
     *
     * @param args see desc
     */
    public static void main(String[] args) {
        if (args.length < ARGS_COUNT) {
            usage("Incorrect argument list.");
            return;
        }

        batchRender(args[0], args[1]);
    }

    private static void batchRender(String baseDir, String outputBaseDir) {
        long startTime = System.currentTimeMillis();
        List<String> fileNames = FileUtils.getFileNames(baseDir, false);

        List<String> htmlPaths = new ArrayList<>();
        List<String> outPdfPaths = new ArrayList<>();
        for (String fileName :
                fileNames) {
            if (fileName.endsWith(HTML_SUFFIX)) {
                htmlPaths.add(fileName);
                String pdfName = FileUtils.getFileNameWithoutExtension(fileName) + PDF_SUFFIX;
                outPdfPaths.add(outputBaseDir + pdfName);
            }
        }


        Assert.that(htmlPaths.size() == outPdfPaths.size(),"输出html数量和输出pdf数量不一致");

        PdfRenderer.batchRender(htmlPaths, outPdfPaths);

        LOGGER.info("Time For rendering PDFs : {} ms", (System.currentTimeMillis() - startTime));
    }

    /**
     * prints out usage information, with optional error message
     *
     * @param err
     */
    private static void usage(String err) {
        if (err != null && err.length() > 0) {
            LOGGER.error("==>" + err);
        }
        LOGGER.error("Usage: java -cp %classpath% com.dofable.reporter.PdfRendererDemo html-base-dir output-pdf-dir");
    }
}
