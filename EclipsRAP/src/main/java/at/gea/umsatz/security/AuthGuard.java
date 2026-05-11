package at.gea.umsatz.security;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import javax.servlet.http.HttpSession;

public class AuthGuard {
    public static void requireLogin() {
        // تفتيش الخزنة العمومية
        HttpSession session = RWT.getUISession().getHttpSession();
        
        if (session == null || session.getAttribute("auth.user") == null) {
            JavaScriptExecutor js = RWT.getClient().getService(JavaScriptExecutor.class);
            if (js != null) {
                // توجيه دقيق لصفحة اللوجين
                js.execute("window.location.replace('/login');");
            }
            throw new IllegalStateException("Unauthorized access.");
        }
    }
}