package at.gea.umsatz.security;

import org.eclipse.rap.rwt.RWT;
import javax.servlet.http.HttpSession;
import at.gea.umsatz.login.UserModel;

public class SessionAuth {
    private static final String USER_KEY = "auth.user";

    public static void login(UserModel user) {
        // الحفظ في الخزنة العمومية (HttpSession)
        RWT.getUISession().getHttpSession().setAttribute(USER_KEY, user);
    }

    public static void logout() {
        HttpSession session = RWT.getUISession().getHttpSession();
        session.removeAttribute(USER_KEY);
        session.invalidate();
    }

    public static UserModel getUser() {
        // الاسترجاع من الخزنة العمومية
        return (UserModel) RWT.getUISession().getHttpSession().getAttribute(USER_KEY);
    }
}