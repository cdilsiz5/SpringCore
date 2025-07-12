package com.epam.springcore;


import com.epam.springcore.config.AppConfig;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class MainApp {

    public static void main(String[] args) throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.refresh();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir("temp");

        var contextPath = "";
        var docBase = new File("src/main/webapp");
        if (!docBase.exists()) docBase.mkdirs();

        var tomcatCtx = tomcat.addWebapp(contextPath, docBase.getAbsolutePath());

        var servlet = new DispatcherServlet(context);
        Tomcat.addServlet(tomcatCtx, "dispatcher", servlet).setLoadOnStartup(1);
        tomcatCtx.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        System.out.println("Tomcat started on http://localhost:8080/");
        tomcat.getServer().await();
    }
}


