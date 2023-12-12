import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sqlite.SQLiteConfig;

public class SQLHandler {

    private final Connection connection;
    private final Statement statement;

    // Конструктор класса для установления соединения с базой данных SQLite и создания таблиц
    public SQLHandler() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\haka_\\IdeaProjects\\JavaProject\\DATABASE.db", config.toProperties());
        statement = connection.createStatement();
        createTables();
    }

    // Методы для создания таблиц в базе данных
    private void createTables() throws SQLException {
        createTable("country", """
            id INTEGER PRIMARY KEY,
            country_name TEXT,
            region TEXT,
            happiness_id INTEGER,
            life_id INTEGER,
            standard_error REAL,
            economy REAL,
            dystopia_residual REAL,
            FOREIGN KEY (happiness_id) REFERENCES happiness(id),
            FOREIGN KEY (life_id) REFERENCES life(id)
        """);

        createTable("happiness", """
            id INTEGER PRIMARY KEY,
            rank INTEGER,
            score REAL
        """);

        createTable("life", """
            id INTEGER PRIMARY KEY,
            family REAL,
            health REAL,
            freedom REAL,
            trust REAL,
            generosity REAL
        """);
    }

    private void createTable(String tableName, String columns) throws SQLException {
        var sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s(%s);
            """, tableName, columns);

        statement.execute(sql);
    }

    // Метод для добавления страны в базу данных
    public void addCountry(Country country) throws SQLException {

        addEntity("happiness", country.getHappiness());
        addEntity("life", country.getLife());

        var query = """
            INSERT OR IGNORE INTO country(
                id, country_name, region, happiness_id, life_id,
                standard_error, economy, dystopia_residual
            ) VALUES (?,?,?,?,?,?,?,?)
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, country.getId());
            preparedStatement.setString(2, country.getCountryName());
            preparedStatement.setString(3, country.getRegion());
            preparedStatement.setInt(4, country.getId());
            preparedStatement.setInt(5, country.getId());
            preparedStatement.setDouble(6, country.getStandardError());
            preparedStatement.setDouble(7, country.getEconomy());
            preparedStatement.setDouble(8, country.getDystopiaResidual());
            preparedStatement.executeUpdate();
        }
    }

    // Метод для добавления записи с показателями счастья в базу данных
    private void addEntity(String tableName, Happiness happiness) throws SQLException {
        var query = String.format("""
            INSERT OR IGNORE INTO %s(
                id, rank, score
            ) VALUES (?,?,?)
            """, tableName);

        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, happiness.country().getId());
            preparedStatement.setInt(2, happiness.rank());
            preparedStatement.setDouble(3, happiness.score());
            preparedStatement.executeUpdate();
        }
    }

    // Метод для добавления записи с показателями качества жизни в базу данных
    private void addEntity(String tableName, Life life) throws SQLException {
        var query = String.format("""
            INSERT OR IGNORE INTO %s(
                id, family, health, freedom, trust, generosity
            ) VALUES (?,?,?,?,?,?)
            """, tableName);

        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, life.country().getId());
            preparedStatement.setDouble(2, life.family());
            preparedStatement.setDouble(3, life.health());
            preparedStatement.setDouble(4, life.freedom());
            preparedStatement.setDouble(5, life.trust());
            preparedStatement.setDouble(6, life.generosity());
            preparedStatement.executeUpdate();
        }
    }

    // Определение страны с самым высоким показателем экономики
    public String getCountryWithHigherEconomy() throws SQLException {
        var query = """
            SELECT country_name, economy FROM country
            WHERE region = 'Latin America and Caribbean' or region = 'Eastern Asia'
            ORDER BY economy DESC
            """;

        try (var rs = statement.executeQuery(query)) {
            return rs.getString(1);
        }
    }

    // Определение страны с самыми средними показателями
    public String getCountryWithAvg() throws SQLException {
        List<String> countryByAvg = new ArrayList<>();
        var query = """
            SELECT c.country_name, h.rank + h.score + c.standard_error + c.economy + l.family + l.health + l.freedom + l.trust + l.generosity + c.dystopia_residual FROM country c
            INNER JOIN life l ON l.id = c.id and (c.region = 'North America' or c.region = 'Western Europe')
            INNER JOIN happiness h ON h.id = c.id and (c.region = 'North America' or c.region = 'Western Europe')
            ORDER BY h.rank + h.score + c.standard_error + c.economy + l.family + l.health + l.freedom + l.trust + l.generosity + c.dystopia_residual
            """;

        try (var s = statement.executeQuery(query)) {
            var i = 0;
            while (s.next()) {
                countryByAvg.add(s.getString(1));
                i++;
            }
            return countryByAvg.get(i / 2);
        }
    }

    // Получение карты стран и их показателей экономики
    public Map<String, Double> getCountriesEconomy() throws SQLException {
        Map<String, Double> economyByCountry = new HashMap<>();
        var query = """
            SELECT country_name, economy FROM country
            ORDER BY economy DESC
            """;

        try (var rs = statement.executeQuery(query)) {
            while (rs.next()) {
                economyByCountry.put(rs.getString(1), rs.getDouble(2));
            }
        }

        return economyByCountry;
    }

}
