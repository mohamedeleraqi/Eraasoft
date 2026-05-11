package at.gea.umsatz.config;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import at.gea.umsatz.MainUI;
import at.gea.umsatz.login.LoginEntryPoint;

public class AppConfig implements ApplicationConfiguration {
    @Override
    public void configure(Application application) {
        // الحل الجذري: سجل كل ملف CSS على حدة بنفس الـ Theme ID
        // الترتيب مهم: الملف اللي تحت بيلغي اللي فوق لو فيه تشابه
        application.addStyleSheet(RWT.DEFAULT_THEME_ID, "css/main-theme.css"); 
        application.addStyleSheet(RWT.DEFAULT_THEME_ID, "css/login.css");
        // مستقبلاً هتضيف هنا:
        // application.addStyleSheet(RWT.DEFAULT_THEME_ID, "css/sidebar.css");

        application.addEntryPoint("/login", LoginEntryPoint.class, null);
        application.addEntryPoint("/app", MainUI.class, null);
    }
}