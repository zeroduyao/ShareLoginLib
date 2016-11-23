package com.liulishuo.share;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class WXEntryActivityProcessor extends AbstractProcessor {

    private Elements elementUtils;

    private static final String CODE_SNIPPET = "package {pack_name}.wxapi;\n"
            + "\n"
            + "import com.liulishuo.share.weixin.WeiXinHandlerActivity;\n"
            + "\n"
            + "/**\n"
            + " * Created by kale on 11/23/16.\n"
            + " */\n"
            + "public class WXEntryActivity extends WeiXinHandlerActivity {}\n";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList((ShareLoginApp.class.getCanonicalName())));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String packageName = null;
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                if (e.getKind() == ElementKind.CLASS) {
                    ShareLoginApp annotation = e.getAnnotation(ShareLoginApp.class);
                    if (annotation != null) {
                        packageName = annotation.packageName();
                    }
                }
            }
        }
        if (packageName != null) {
            createClassFile(packageName, "WXEntryActivity", CODE_SNIPPET.replace("{pack_name}", packageName));
        }
        return true;
    }

    private void createClassFile(String PACKAGE_NAME, String clsName, String content) {
        TypeElement pkgElement = elementUtils.getTypeElement(PACKAGE_NAME);

        OutputStreamWriter osw = null;
        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(PACKAGE_NAME + "." + clsName, pkgElement);
            OutputStream os = fileObject.openOutputStream();
            osw = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            osw.write(content, 0, content.length());

        } catch (IOException e) {
            //e.printStackTrace();
            //fatalError(e.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "failed");
            }
        }
    }
}
