package com.dofable.reporter.pdf;

import com.dofable.reporter.util.FontHelper;
import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PDF渲染工具类，用于批量将HTML转换为PDF
 * 支持的中文字体有：宋体、黑体、楷体、隶书、幼圆
 * 支持的英文字体有：Tohama、Arial、Times New Roman、Verdana
 *
 * @author XWQ
 */
public final class PdfRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfRenderer.class);
    private static final int SPLIT_SCALE_PER_CORE_SIZE = 2;
    private static AtomicInteger nCompletes;

    private PdfRenderer() {
        // util class
    }

    /**
     * 根据html文件生成通知单
     *
     * @param htmlPaths   html的文件位置列表
     * @param outPdfPaths pdf输出路径列表
     */
    public static void batchRender(final List<String> htmlPaths, final List<String> outPdfPaths) {
        if (htmlPaths.size() != outPdfPaths.size()) {
            throw new IllegalArgumentException("html文件列表和输出文件名称列表大小必须一致");
        }

        int nCores = Runtime.getRuntime().availableProcessors();
        if (nCores == 1 || htmlPaths.size() <= SPLIT_SCALE_PER_CORE_SIZE * nCores) {
            singleRender(htmlPaths, outPdfPaths);
        } else {
            multiRender(htmlPaths, outPdfPaths, nCores);
        }
    }

    private synchronized static void multiRender(List<String> htmlPaths, List<String> outPdfPaths, int nCores) {
        int nPdfPerCore = htmlPaths.size() / nCores + (htmlPaths.size() % nCores == 0 ? 0 : 1);
        List<List<String>> htmlPathsList = Lists.partition(htmlPaths, nPdfPerCore);
        List<List<String>> outPdfPathsList = Lists.partition(outPdfPaths, nPdfPerCore);

        ExecutorService executorService = Executors.newFixedThreadPool(nCores);
        for (int coreIndex = 0; coreIndex < nCores; coreIndex++) {
            final List<String> htmlPathSplits = htmlPathsList.get(coreIndex);
            final List<String> outPdfPathSplits = outPdfPathsList.get(coreIndex);
            final ITextRenderer iTextRenderer = ITextRendererHolder.batchRenderer(coreIndex);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    batchSplittingRender(iTextRenderer, htmlPathSplits, outPdfPathSplits);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            throw new IllegalStateException("由于异常中断，结束pdf生成");
        }
    }

    private synchronized static void singleRender(List<String> htmlPaths, List<String> outPdfPaths) {
        ITextRenderer iTextRenderer = ITextRendererHolder.batchRenderer(0);
        batchSplittingRender(iTextRenderer, htmlPaths, outPdfPaths);
    }

    private static void batchSplittingRender(ITextRenderer textRenderer, List<String> htmlPaths, List<String> outPdfPaths) {
        for (int i = 0; i < htmlPaths.size(); i++) {
            batchRender(textRenderer, htmlPaths.get(i), outPdfPaths.get(i));
        }
    }

    /**
     * 根据html文件生成通知单
     *
     * @param htmlPath   html的文件位置
     * @param outPdfPath pdf输出路径
     */
    private static void batchRender(ITextRenderer renderer, String htmlPath, String outPdfPath) {
        try {
            OutputStream out = new FileOutputStream(outPdfPath);
            String htmlURL = new File(htmlPath).toURI().toURL().toString();

            renderer.setDocument(htmlURL);
            renderer.layout();
            renderer.createPDF(out);

            out.close();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // 单例模式
    private static class ITextRendererHolder {
        private static class EmptyITextRenderer extends ITextRenderer {
            private static final EmptyITextRenderer EMPTY_ITEXT_RENDERER = new EmptyITextRenderer();

            static public EmptyITextRenderer instance() {
                return EMPTY_ITEXT_RENDERER;
            }
        }

        private static final List<ITextRenderer> RENDERERS;

        static {
            int nCores = Runtime.getRuntime().availableProcessors();
            RENDERERS = new ArrayList<>(nCores);
            for (int i = 0; i < nCores; i++) {
                RENDERERS.add(EmptyITextRenderer.instance());
            }
        }

        static ITextRenderer batchRenderer(int i) {
            if (EmptyITextRenderer.instance() == RENDERERS.get(i)) {
                RENDERERS.add(i, newTextRenderer());
            }

            return RENDERERS.get(i);
        }

        /**
         * 每个线程各自获取ITextRenderer，避免线程之间共用
         *
         * @return TextRenderer
         */
        private static ITextRenderer newTextRenderer() {
            ITextRenderer renderer = new ITextRenderer();
            addFont(renderer);

            return renderer;
        }

        private static void addFont(ITextRenderer renderer) {
            final List<String> supportFontPaths = FontHelper.supportedFontPaths();
            // 添加字体
            ITextFontResolver fontResolver = renderer.getFontResolver();
            try {
                for (String fontPath : supportFontPaths) {
                    fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                }
            } catch (DocumentException | IOException e) {
                throw new IllegalStateException("字体初始化失败", e);
            }
        }
    }
}
