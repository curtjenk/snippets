import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties = new Properties();

    public ConfigLoader(String propertiesFilePath) {
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader("config.properties");
        String dbUrl = config.getProperty("database.url");
        String dbUser = config.getProperty("database.username");
        String dbPassword = config.getProperty("database.password");
        String apiUrl = config.getProperty("api.base.url");

        System.out.println("Database URL: " + dbUrl);
        System.out.println("Database User: " + dbUser);
        System.out.println("Database Password: " + dbPassword);
        System.out.println("API Base URL: " + apiUrl);
    }
}