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


    public Country(String country_name, String region, int happinessRank, double happinessScore,
                   double standardError, double economy, double family, double health, double freedom,
                   double trust, double generosity, double dystopiaResidual) {
        this.id = countriesCount++;
        this.country_name = country_name;
        this.region = region;
        this.happiness = new Happiness(this, happinessRank, happinessScore);
        this.standardError = standardError;
        this.economy = economy;
        this.life = new Life(this, family, health, freedom, trust, generosity);
        this.dystopiaResidual = dystopiaResidual;
    }

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
