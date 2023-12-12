# Проект Вариант №1

### 1. Данные из CSV-файла можно поделить на три класса:

• Country <br /> 
• Happiness <br />
• Life <br />

Создадим три соответсвующих файла
#### Country.java


    public class Country {
    
        private static int countriesCount = 0;
    
        private final int id;
        private final String country_name;
        private final String region;
        private final Happiness happiness;
        private final Life life;
        private final double standardError;
        private final double economy;
        private final double dystopiaResidual;
    
        // Конструктор класса Country
        public Country(String country_name, String region, int happinessRank, double happinessScore,
                       double standardError, double economy, double family, double health, double freedom,
                       double trust, double generosity, double dystopiaResidual) {
            // Инициализация полей объекта Country
            this.id = countriesCount++;
            this.country_name = country_name;
            this.region = region;
            this.happiness = new Happiness(this, happinessRank, happinessScore);
            this.standardError = standardError;
            this.economy = economy;
            this.life = new Life(this, family, health, freedom, trust, generosity);
            this.dystopiaResidual = dystopiaResidual;
        }
    
        // Методы для получения значений полей объекта Country
        public String getCountryName() {
            return country_name;
        }
    
        public String getRegion() {
            return region;
        }
    
        public Happiness getHappiness() {
            return happiness;
        }
    
        public double getStandardError() {
            return standardError;
        }
    
        public double getEconomy() {
            return economy;
        }
    
        public double getDystopiaResidual() {
            return dystopiaResidual;
        }
    
        public Life getLife() {
            return life;
        }
    
        public int getId() {return id;}
    
    }


#### Happiness.java



    // Представление данных о счастье
    public record Happiness(Country country, int rank, double score) {}



#### Life.java



    // Представление данных о качестве жизни
    public record Life(Country country, double family, double health, double freedom, double trust, double generosity) {}

### 2. Распарсим данные из CSV-файла с помощью библиотеки OpenCSV

Создадим класс для чтения данных из CSV-файла и создания объектов Country на основе этих данных

#### CSV.java


    import com.opencsv.CSVReader;
    import com.opencsv.exceptions.CsvValidationException;
    
    import java.io.IOException;
    import java.io.Reader;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.ArrayList;
    import java.util.List;

    public class CSV {
    
        // Метод для разбора CSV-файла и создания списка объектов Country
        public static List<Country> parse() {
            List<Country> countries = new ArrayList<>();
    
            try (Reader reader = Files.newBufferedReader(Path.of("Показатель счастья по странам 2015.csv"));
                 CSVReader csvReader = new CSVReader(reader)) {
    
                // Пропуск заголовков CSV-файла
                csvReader.readNext();
    
                String[] args;
                while ((args = csvReader.readNext()) != null) {
                    try {
                        // Создание объекта Country из данных строки CSV
                        Country country = createCountryFromArgs(args);
                        countries.add(country);
                    } catch (NumberFormatException e) {
                        handleParsingError(e);
                    }
                }
    
            } catch (IOException | CsvValidationException e) {
                handleFileOperationError(e);
            }
    
            return countries;
        }
    
         // Приватный метод для создания объекта Country из массива аргументов
        private static Country createCountryFromArgs(String[] args) {
            return new Country(
                    args[0],
                    args[1],
                    Integer.parseInt(args[2]),
                    Double.parseDouble(args[3]),
                    Double.parseDouble(args[4]),
                    Double.parseDouble(args[5]),
                    Double.parseDouble(args[6]),
                    Double.parseDouble(args[7]),
                    Double.parseDouble(args[8]),
                    Double.parseDouble(args[9]),
                    Double.parseDouble(args[10]),
                    Double.parseDouble(args[11])
            );
        }
    
        // Методы обработки ошибок
        private static void handleParsingError(NumberFormatException e) {
            System.err.println("Ошибка парсинга: " + e.getMessage());
        }
    
        private static void handleFileOperationError(Exception e) {
            System.err.println("Ошибка в обработке файла: " + e.getMessage());
        }
    }
        
### 3. Создаем класс SQLHandler для работы с базой данных SQLite (библиотека sqlite-jbdc)

#### SQLHandler.java

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

### 4. Создаем класс Main, который инициализирует соединение с базой данных, добавляет страны в базу данных из CSV-файла, а затем вызывает три метода, каждый из которых выполняет SQL-запрос и выводит результаты

#### Main.java


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    private static SQLHandler sql;

    public static void main(String[] args) {
        try {
            var countries = CSV.parse();
            sql = new SQLHandler();

            // Добавление стран в базу данных
            for (var country : countries) {
                sql.addCountry(country);
            }

            // Выполнение заданных SQL-запросов
            sql1();
            sql2();
            sql3();

        } catch (SQLException e) {
            handleException(e);
        }
    }

    // Метод для формирования графика по показателю экономики и сохранения его в файл (библиотека JFreeChart)
    private static void sql1() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            var countryByEconomy = sql.getCountriesEconomy();

            // Сортировка стран по алфавиту
            Map<String, Double> sortedCountryByEconomy = new TreeMap<>(countryByEconomy);

            for (Map.Entry<String, Double> entry : sortedCountryByEconomy.entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), "");
            }

            var graph = ChartFactory.createBarChart("График по показателю экономики", "Страна", "ВВП", dataset);
            ChartUtils.saveChartAsPNG(new File("graph.png"), graph, 1920, 1080);
            System.out.println("\n№1. Графики по показателю экономики сформирован в файл graph.png");

        } catch (SQLException | IOException e) {
            handleException(e);
        }
    }

    // Метод для получения страны с самым высоким показателем экономики
    private static void sql2() {
        try {
            System.out.println("\n№2. Страна с самым высоким показателем экономики среди \"Latin America and Caribbean\" и \"Eastern Asia\": "
                    + sql.getCountryWithHigherEconomy());

        } catch (SQLException e) {
            handleException(e);
        }
    }

    // Метод для получения страны с самыми средними показателями
    private static void sql3() {
        try {
            System.out.println("\n№3. Страна с \"самыми средними показателями\" среди \"Western Europe\" и \"North America\": "
                    + sql.getCountryWithAvg());

        } catch (SQLException e) {
            handleException(e);
        }
    }

    // Обработка исключений
    private static void handleException(Exception e) {
        System.err.println("Ошибка выполнения программы: " + e.getMessage());
        e.printStackTrace();
    }
}


### 5. Итоговый вывод:

![image](https://github.com/eshk3re/JavaProjectN1/assets/105351013/90b6dfae-253f-4705-aa37-508c8d44d97e)
                                                                                                    
