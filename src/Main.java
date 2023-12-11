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

            for (var country : countries) {
                sql.addCountry(country);
            }

            sql1();
            sql2();
            sql3();

        } catch (SQLException e) {
            handleException(e);
        }
    }

    private static void sql1() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            var countryByEconomy = sql.getCountriesEconomy();

            Map<String, Double> sortedCountryByEconomy = new TreeMap<>(countryByEconomy);

            for (Map.Entry<String, Double> entry : sortedCountryByEconomy.entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), "");
            }

            var graph = ChartFactory.createBarChart("График по показателю экономики", "Страна", "ВВП", dataset);
            ChartUtils.saveChartAsPNG(new File("graph.jpg"), graph, 1920, 1080);
            System.out.println("\n№1. Графики по показателю экономики сформирован в файл graph.jpg");

        } catch (SQLException | IOException e) {
            handleException(e);
        }
    }

    private static void sql2() {
        try {
            System.out.println("\n№2. Страна с самым высоким показателем экономики среди \"Latin America and Caribbean\" и \"Eastern Asia\": "
                    + sql.getCountryWithHigherEconomy());

        } catch (SQLException e) {
            handleException(e);
        }
    }

    private static void sql3() {
        try {
            System.out.println("\n№3. Страна с \"самыми средними показателями\" среди \"Western Europe\" и \"North America\": "
                    + sql.getCountryWithAvg());

        } catch (SQLException e) {
            handleException(e);
        }
    }

    private static void handleException(Exception e) {
        System.err.println("Ошибка выполнения программы: " + e.getMessage());
        e.printStackTrace();
    }
}
