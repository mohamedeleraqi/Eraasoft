package at.gea.umsatz;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import at.gea.umsatz.security.AuthGuard;

public class MainUI implements EntryPoint {

    @Override
    public int createUI() {
        // الخطوة السحرية: البواب بيفحص هنا
        // لو المستخدم مش مسجل دخول، الميثود دي هتعمل Redirect للـ /login وتوقف تنفيذ الكود فوراً
        AuthGuard.requireLogin();

        // لو الكود كمل هنا، يبقى المستخدم مسجل دخول فعلاً
        Display display = new Display();
        Shell shell = new Shell(display, SWT.NO_TRIM);
        shell.setMaximized(true);
        shell.setLayout(new FillLayout());

        Label welcome = new Label(shell, SWT.CENTER);
        welcome.setText("Willkommen im GEA System!");

        shell.open();
        return 0;
    }
}