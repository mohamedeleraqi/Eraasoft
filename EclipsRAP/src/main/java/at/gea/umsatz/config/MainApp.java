package at.gea.umsatz.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.engine.RWTServletContextListener;

public class MainApp {
    public static void main(String[] args) throws Exception {
        int port = AppProperties.getInt("server.port", 4000);
        Server server = new Server(port);

        // تعريف الـ Context وتفعيل السيشن
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // ==========================================
        // --- إعدادات الحماية وفترة السيشن ---
        // ==========================================
        // تحديد مدة الجلسة (30 دقيقة = 1800 ثانية)
        context.getSessionHandler().setMaxInactiveInterval(30 * 60);
        
        // حماية الكوكي من هجمات السكربتات الخبيثة (XSS)
        context.getSessionHandler().getSessionCookieConfig().setHttpOnly(true);
        // ==========================================

        // إعداد المجلد المؤقت لملفات RAP والـ CSS
        File tempDir = Files.createTempDirectory("rap-resources").toFile();
        tempDir.deleteOnExit(); // مسح المجلد عند إغلاق السيرفر
        context.setBaseResource(Resource.newResource(tempDir));
        context.setAttribute("org.eclipse.rap.rwt.contextDirectory", tempDir.getAbsolutePath());
        
        // فلتر التوجيه التلقائي من الروت (/) إلى اللوجين (/login)
        FilterHolder redirectFilter = new FilterHolder(new Filter() {
            @Override
            public void init(FilterConfig filterConfig) {}

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                    throws IOException, ServletException {
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;
                
                if ("/".equals(req.getRequestURI())) {
                    res.sendRedirect("/login");
                    return;
                }
                chain.doFilter(request, response);
            }

            @Override
            public void destroy() {}
        });
        context.addFilter(redirectFilter, "/*", EnumSet.of(DispatcherType.REQUEST));

        // تسجيل الـ Servlets الأساسية
        context.addServlet(new ServletHolder(new DefaultServlet()), "/"); 
        context.addServlet(new ServletHolder(new RWTServlet()), "/login");
        context.addServlet(new ServletHolder(new RWTServlet()), "/app");

        // ربط الـ AppConfig
        context.addEventListener(new RWTServletContextListener());
        context.setInitParameter(ApplicationConfiguration.CONFIGURATION_PARAM, AppConfig.class.getName());

        server.setHandler(context);
        
        System.out.println("=====================================================");
        System.out.println("✅ Server is starting...");
        System.out.println("✅ Security: Session timeout set to 30 minutes.");
        System.out.println("✅ Open your browser at: http://localhost:" + port + "/");
        System.out.println("=====================================================");
        
        server.start();
        server.join();
    }
}