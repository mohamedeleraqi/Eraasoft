package at.gea.umsatz.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import at.gea.umsatz.config.AppProperties;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    private DatabaseManager() {}

    public static synchronized HikariDataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(AppProperties.get("db.url"));
            config.setUsername(AppProperties.get("db.user"));
            config.setPassword(AppProperties.get("db.password"));
            config.setSchema(AppProperties.get("db.schema"));

            config.setMaximumPoolSize(AppProperties.getInt("db.pool.maximumPoolSize", 10));
            config.setMinimumIdle(AppProperties.getInt("db.pool.minimumIdle", 2));
            config.setConnectionTimeout(AppProperties.getInt("db.pool.connectionTimeoutMs", 10000));
            config.setIdleTimeout(AppProperties.getInt("db.pool.idleTimeoutMs", 600000));
            config.setMaxLifetime(AppProperties.getInt("db.pool.maxLifetimeMs", 1800000));

            dataSource = new HikariDataSource(config);
            System.out.println("✅ تم تهيئة HikariCP للاتصال بقاعدة البيانات.");
        }
        return dataSource;
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🛑 تم إغلاق الاتصال بقاعدة البيانات.");
        }
    }
}