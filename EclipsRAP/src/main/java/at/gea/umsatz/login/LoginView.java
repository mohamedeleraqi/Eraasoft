package at.gea.umsatz.login;

import java.io.InputStream;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class LoginView extends Composite {
    private final Text usernameField;
    private final Text passwordField;
    private final Label errorLabel;
    private final LoginPresenter presenter;

    public LoginView(Composite parent) {
        super(parent, SWT.NONE);
        this.presenter = new LoginPresenter(new JdbcLoginRepository());

        // ربط الخلفية الخضراء
        this.setData(RWT.CUSTOM_VARIANT, "login-background");
        this.setLayout(new GridLayout(1, false));

        // الكارد الأبيض
        Composite card = new Composite(this, SWT.NONE);
        card.setData(RWT.CUSTOM_VARIANT, "login-card");
        
        GridData cardGd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        cardGd.widthHint = 380; 
        card.setLayoutData(cardGd);
        
        GridLayout cardLayout = new GridLayout(1, false);
        cardLayout.marginWidth = 40;
        cardLayout.marginHeight = 40;
        cardLayout.verticalSpacing = 15;
        card.setLayout(cardLayout);

        // اللوجو
        Label logoLabel = new Label(card, SWT.CENTER);
        logoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
        try {
            InputStream imgStream = getClass().getClassLoader().getResourceAsStream("img/logo-gea.png");
            if (imgStream != null) {
                Image logo = new Image(getDisplay(), imgStream);
                logoLabel.setImage(logo);
            } else {
                logoLabel.setText("[GEA Logo]");
            }
        } catch (Exception e) {
            logoLabel.setText("GEA");
        }

        // العنوان
        Label title = new Label(card, SWT.CENTER);
        title.setText("GEA Umsatz");
        title.setData(RWT.CUSTOM_VARIANT, "login-title");
        title.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

        new Label(card, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        // حقل المستخدم
        Label userLbl = new Label(card, SWT.NONE);
        userLbl.setText("Benutzername \u2022");
        userLbl.setData(RWT.CUSTOM_VARIANT, "login-label");
        
        usernameField = new Text(card, SWT.BORDER);
        usernameField.setData(RWT.CUSTOM_VARIANT, "login-input");
        usernameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // حقل الباسورد
        Label passLbl = new Label(card, SWT.NONE);
        passLbl.setText("Passwort \u2022");
        passLbl.setData(RWT.CUSTOM_VARIANT, "login-label");
        
        passwordField = new Text(card, SWT.BORDER | SWT.PASSWORD);
        passwordField.setData(RWT.CUSTOM_VARIANT, "login-input");
        passwordField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // رسالة الخطأ
        errorLabel = new Label(card, SWT.WRAP);
        errorLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
        errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // الزرار الأخضر
        Button loginBtn = new Button(card, SWT.PUSH);
        loginBtn.setText("Anmelden");
        loginBtn.setData(RWT.CUSTOM_VARIANT, "login-btn");
        GridData btnGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        btnGd.heightHint = 40;
        loginBtn.setLayoutData(btnGd);

        loginBtn.addListener(SWT.Selection, e -> executeLogin(card));
        passwordField.addListener(SWT.DefaultSelection, e -> executeLogin(card));
    }

    private void executeLogin(Composite card) {
        errorLabel.setText("");
        LoginPresenter.Result result = presenter.login(usernameField.getText(), passwordField.getText());

        if (result.isOk()) {
            // تثبيت السيشن يدوياً لضمان عدم الطرد
            RWT.getUISession().setAttribute("auth.user", result.getUser());
            
            JavaScriptExecutor js = RWT.getClient().getService(JavaScriptExecutor.class);
            if (js != null) {
                // التريك اللي بيحل المشكلة: تأخير التحويل 100 ملي ثانية
                js.execute("setTimeout(function() { window.location.href = './app'; }, 100);");
            }
        } else {
            errorLabel.setText(result.getMessage());
            card.layout(true);
        }
    }
}