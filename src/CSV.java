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
