package at.gea.umsatz.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;
import at.gea.umsatz.db.DatabaseManager;

public class JdbcLoginRepository implements LoginRepository {
    private static final String SQL_AUTH = "SELECT gea.auth_login(?::text, ?::text)";
    private static final String SQL_FULL_USER = """
            SELECT retailer_id, user_name, is_admin, active, staat, ort, plz, 
                   street, email, phone, filiale, ispartner, uid, creationdate
            FROM gea.retailer WHERE retailer_id = ?
            """;

    @Override
    public Long authLogin(String username, String rawPassword) {
        try (Connection con = DatabaseManager.getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_AUTH)) {
            ps.setString(1, username);
            ps.setString(2, rawPassword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    if (rs.wasNull()) return null;
                    return id;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<UserModel> loadFullUser(Long id) {
        try (Connection con = DatabaseManager.getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FULL_USER)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                UserModel u = new UserModel();
                u.setId(rs.getLong("retailer_id"));
                u.setUsername(rs.getString("user_name"));
                u.setActive(rs.getBoolean("active"));
                // كمل باقي الحقول...
                return Optional.of(u);
            }
        } catch (Exception ex) { return Optional.empty(); }
    }
}