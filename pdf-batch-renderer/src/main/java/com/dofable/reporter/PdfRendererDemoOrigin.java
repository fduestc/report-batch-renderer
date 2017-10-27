package com.dofable.reporter;

import com.dofable.reporter.pdf.PdfRenderer;
import com.dofable.reporter.util.FontHelper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * PDFRenderer supports headless rendering of XHTML documents, outputting
 * to PDF format. There are two static utility methods, one for rendering
 * a {@link java.net.URL}, {@link #renderToPDF(String, String)} and one
 * for rendering a {@link File}, {@link #renderToPDF(File, String)}</p>
 * <p>You can use this utility from the command line by passing in
 * the URL or file location as first parameter, and PDF path as second
 * parameter:
 * <pre>
 * java -cp %classpath% org.xhtmlrenderer.simple.PDFRenderer <url> <pdf>
 * </pre>
 *
 * @author Pete Brant
 * @author Patrick Wright
 */
public class PdfRendererDemoOrigin {
    private static final Map<String, Character> versionMap = new HashMap();

    static {
        versionMap.put("1.2", new Character(PdfWriter.VERSION_1_2));
        versionMap.put("1.3", new Character(PdfWriter.VERSION_1_3));
        versionMap.put("1.4", new Character(PdfWriter.VERSION_1_4));
        versionMap.put("1.5", new Character(PdfWriter.VERSION_1_5));
        versionMap.put("1.6", new Character(PdfWriter.VERSION_1_6));
        versionMap.put("1.7", new Character(PdfWriter.VERSION_1_7));
    }

    /**
     * Renders the XML file at the given URL as a PDF file
     * at the target location.
     *
     * @param url url for the XML file to render
     * @param pdf path to the PDF file to create
     * @throws IOException       if the URL or PDF location is
     *                           invalid
     * @throws DocumentException if an error occurred
     *                           while building the Document.
     */
    public static void renderToPDF(String url, String pdf)
            throws IOException, DocumentException {

        renderToPDF(url, pdf, null);
    }

    /**
     * Renders the XML file at the given URL as a PDF file
     * at the target location.
     *
     * @param url        url for the XML file to render
     * @param pdf        path to the PDF file to create
     * @param pdfVersion version of PDF to output; null uses default version
     * @throws IOException       if the URL or PDF location is
     *                           invalid
     * @throws DocumentException if an error occurred
     *                           while building the Document.
     */
    public static void renderToPDF(String url, String pdf, Character pdfVersion)
            throws IOException, DocumentException {

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        if (pdfVersion != null) renderer.setPDFVersion(pdfVersion.charValue());
        doRenderToPDF(renderer, pdf);
    }

    /**
     * Renders the XML file as a PDF file at the target location.
     *
     * @param file XML file to render
     * @param pdf  path to the PDF file to create
     * @throws IOException       if the file or PDF location is
     *                           invalid
     * @throws DocumentException if an error occurred
     *                           while building the Document.
     */
    public static void renderToPDF(File file, String pdf)
            throws IOException, DocumentException {

        renderToPDF(file, pdf, null);
    }

    /**
     * Renders the XML file as a PDF file at the target location.
     *
     * @param file       XML file to render
     * @param pdf        path to the PDF file to create
     * @param pdfVersion version of PDF to output; null uses default version
     * @throws IOException       if the file or PDF location is
     *                           invalid
     * @throws DocumentException if an error occurred
     *                           while building the Document.
     */
    public static void renderToPDF(File file, String pdf, Character pdfVersion)
            throws IOException, DocumentException {

        ITextRenderer renderer = new ITextRenderer();

        final List<String> fontPathList = FontHelper.supportedFontPaths();
        // 添加字体
        ITextFontResolver fontResolver = renderer.getFontResolver();
        try {
            for (String fontPath : fontPathList) {
                fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }
        } catch (DocumentException | IOException e) {
            System.err.println(e.getMessage());
        }

        renderer.setDocument(file);
        if (pdfVersion != null) renderer.setPDFVersion(pdfVersion.charValue());
        doRenderToPDF(renderer, pdf);
    }

    /**
     * Internal use, runs the render process
     *
     * @param renderer
     * @param pdf
     * @throws com.lowagie.text.DocumentException
     * @throws java.io.IOException
     */
    private static void doRenderToPDF(ITextRenderer renderer, String pdf)
            throws IOException, DocumentException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(pdf);
            renderer.layout();
            renderer.createPDF(os);

            os.close();
            os = null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Renders a file or URL to a PDF. Command line use: first
     * argument is URL or file path, second
     * argument is path to PDF file to generate.
     *
     * @param args see desc
     * @throws IOException       if source could not be read, or if
     *                           PDF path is invalid
     * @throws DocumentException if an error occurs while building
     *                           the document
     */
    public static void main(String[] args) throws IOException, DocumentException {
//        String[] htmls = {"testBearArial.html",
//                "testBearTahoma.html",
//                "testBearTimes.html",
//                "testBearVerdana.html"};
//        if (args.length != 1) {
//            System.err.println("java -jar **.jar [scale]");
//            return;
//        }
//        int scale = Integer.valueOf(args[0]);

        int scale = 2;

        List<String> htmls = new ArrayList<>();
        for (int i = 0; i < scale; i++) {
            htmls.add("testBear.html");
        }

        System.out.println("begin ==== ");

        renderMyselfBatch(htmls);
//        renderMyselfBatch(htmls);
//        renderNative(htmls);
//        renderMyselfBatch(htmls);
//        renderMyselfBatch(htmls);

    }

    private static void renderMyselfBatch(List<String> htmls) {
        long startTime = System.currentTimeMillis();

        int i = 0;
        List<String> htmlPaths = new ArrayList<>();
        List<String> outPdfPaths = new ArrayList<>();
        for (String html :
                htmls) {
            i++;
            URL url = PdfRendererDemoOrigin.class.getClassLoader().getResource(html);
            String outPdf = PdfRendererDemoOrigin.class.getClassLoader().getResource("").getFile() + "out/" + i + "-" + html.replace("html", "pdf");
            htmlPaths.add(url.getFile());
            outPdfPaths.add(outPdf);
        }

        PdfRenderer.batchRender(htmlPaths, outPdfPaths);

        System.out.println("renderMyself: " + (System.currentTimeMillis() - startTime));
    }

    private static void renderNative(List<String> htmls) throws IOException, DocumentException {

        long startTime = System.currentTimeMillis();
        for (String html :
                htmls) {
            render(html, html.replace("html", "pdf"));
        }
        System.out.println("renderNative: " + (System.currentTimeMillis() - startTime));
    }

    private static void render(String html, String pdfName) throws IOException, DocumentException {
        URL url = PdfRendererDemoOrigin.class.getClassLoader().getResource(html);
        String outPdf = PdfRendererDemoOrigin.class.getClassLoader().getResource("").getFile() + pdfName;

        File xlsFile = new File(url.getFile());
        PdfRendererDemoOrigin.renderToPDF(xlsFile, outPdf, versionMap.get("1.6"));
    }

    private static Character checkVersion(String version) {
        final Character val = (Character) versionMap.get(version.trim());
        if (val == null) {
            usage("Invalid PDF version number; use 1.2 through 1.7");
        }
        return val;
    }

    /**
     * prints out usage information, with optional error message
     *
     * @param err
     */
    private static void usage(String err) {
        if (err != null && err.length() > 0) {
            System.err.println("==>" + err);
        }
        System.err.println("Usage: ... url pdf [version]");
        System.err.println("   where version (optional) is between 1.2 and 1.7");
        System.exit(1);
    }
}
