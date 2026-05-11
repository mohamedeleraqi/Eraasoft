package at.gea.umsatz.config;

import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Error: application.properties not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key, int defaultValue) {
        String val = properties.getProperty(key);
        try {
            return (val != null) ? Integer.parseInt(val) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}