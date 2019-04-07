package me.liqiu.mybatisgeneratetools.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.nio.charset.Charset;

import static java.lang.String.format;

public final class FreemarkerTemplate {

    private final Template template;

    public FreemarkerTemplate(String classPathFile) {
        this(Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathFile));
    }

    public FreemarkerTemplate(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public FreemarkerTemplate(InputStream inputSource) {
        this(new InputStreamReader(inputSource, Charset.forName("UTF-8")));
    }

    public FreemarkerTemplate(InputStream inputSource, Charset encoding, Configuration config) {
        this(new InputStreamReader(inputSource, encoding), config);
    }

    public FreemarkerTemplate(Reader inputSource) {
        this(inputSource, null);
    }

    public FreemarkerTemplate(Reader inputSource, Configuration config) {
        try {
            template = new Template(format("template-%d", inputSource.hashCode()), inputSource, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void compile(Object dataModel, Writer output) throws IOException, TemplateException {
        template.process(dataModel, output);
    }

    public String compileToString(Object dataModel) {
        StringWriter stringWriter = new StringWriter();
        try {
            compile(dataModel, stringWriter);
            return stringWriter.toString();
        } catch (Exception ex) {
            return null;
        }
    }

}
