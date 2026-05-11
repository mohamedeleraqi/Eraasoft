package at.gea.umsatz.login;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class LoginEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
        Display display = new Display();
        Shell shell = new Shell(display, SWT.NO_TRIM);
        shell.setMaximized(true);
        shell.setLayout(new FillLayout());
        
        new LoginView(shell);
        
        shell.open();
        return 0;
    }
}