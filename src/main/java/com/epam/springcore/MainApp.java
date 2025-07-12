package com.epam.springcore;

import com.epam.springcore.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class MainApp {

    public static void main(String[] args) throws Exception {
        // 1. Tomcat başlat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir("temp");

        // 2. Webapp dizini
        File docBase = new File("src/main/webapp");
        if (!docBase.exists()) {
            docBase.mkdirs();
        }

        // 3. Tomcat context oluştur
        Context tomcatContext = tomcat.addContext("", docBase.getAbsolutePath());

        // 4. Spring WebApplicationContext oluştur
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.setServletContext(tomcatContext.getServletContext()); // BU ŞART!
        springContext.register(AppConfig.class);
        springContext.refresh();

        // 5. DispatcherServlet ayarla
        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);
        Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet).setLoadOnStartup(1);
        tomcatContext.addServletMappingDecoded("/", "dispatcher");

        // 6. Tomcat başlat
        tomcat.start();
        System.out.println("Tomcat started at http://localhost:8080/");
        tomcat.getServer().await();
    }
}
