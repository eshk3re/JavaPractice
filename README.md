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


