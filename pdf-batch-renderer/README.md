# report-batch-renderer -> pdf-batch-renderer
A utility for batchly rendering PDF

## 项目说明
> 该项目的目的是批量将HTML转换为PDF文件，底层基于[FlyingSaucer](https://github.com/flyingsaucerproject/flyingsaucer)，项目中采用Java多线程框架的Executors创建了一个包含固定数量的线程池来批量生成PDF:
> <code><pre>
> ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
> </pre></code>
> 项目中支持将本地路径中的html文件批量转换为PDF，放入本地文件系统中。

## 使用说明
><pre>
>List<String> htmlsPaths = ...; // 定义需要转换的html文件路径列表
>List<String> outPdfPaths = ...; // 定义生成pdf文件路径列表
>Assert.that(htmlPaths.size() != outPdfPaths.size(),"输入HTML数量和输出PDF数量不一致");
>PdfRenderer.batchRender(htmlPaths, outPdfPaths); // 批量生成PDF
></pre>

## 性能说明
|指标	|简易报表（信封）	|复杂报表 |
| ------------- |:-------------:| -----:|
|PDF生成速度（个/秒）|	4307.25|	2472.67|

注：OS为Redhat 6.6，硬件配置为Intel Xeon CPU E5-2650 2.30GHz 4core、8GB内存

## 字体支持
项目中默认提供了对中文字体（宋体 、黑体、 楷体 、隶书 、幼圆）和英文字体（'Times New Roman', Arial, Tohama, Verdana)的支持，如果需要增加对其他字体的支持，则请将字体文件添加到resources/fonts目录中

## 代码使用说明
All for one, one for all. 请随意使用本项目中的所有代码，只是请加上所有作者信息。
