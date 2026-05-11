package at.gea.umsatz.login;

import java.util.Optional;

public interface LoginRepository {
    Long authLogin(String username, String rawPassword);
    Optional<UserModel> loadFullUser(Long id);
}