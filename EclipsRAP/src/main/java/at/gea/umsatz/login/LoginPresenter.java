package at.gea.umsatz.login;

import java.util.Optional;
import at.gea.umsatz.security.SessionAuth;

public class LoginPresenter {
    private final LoginRepository repo;

    public LoginPresenter(LoginRepository repo) { this.repo = repo; }

    public Result login(String user, String pass) {
        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            return Result.error("Bitte Felder ausfüllen.");
        }
        Long id = repo.authLogin(user.trim().toLowerCase(), pass.trim());
        if (id == null) return Result.error("Ungültige Daten.");

        Optional<UserModel> userOpt = repo.loadFullUser(id);
        if (userOpt.isEmpty() || !userOpt.get().isActive()) return Result.error("Deaktiviert.");

        SessionAuth.login(userOpt.get());
        return Result.success(userOpt.get());
    }

    public static class Result {
        private final boolean ok;
        private final String msg;
        private final UserModel user;
        private Result(boolean ok, String msg, UserModel u) { this.ok = ok; this.msg = msg; this.user = u; }
        public static Result success(UserModel u) { return new Result(true, null, u); }
        public static Result error(String m) { return new Result(false, m, null); }
        public boolean isOk() { return ok; }
        public String getMessage() { return msg; }
        public UserModel getUser() { return user; }
    }
}