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

// Класс для чтения данных из CSV-файла и создания объектов Country

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
        
    
