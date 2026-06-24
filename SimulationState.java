import java.util.ArrayList;
import java.util.List;

public class SimulationState {
    private PlantSpecs plant;
    private double temperature;
    private double pH;
    private double speed;
    private double day;
    private double size;
    private double stressDays;
    private boolean alive;

    private final List<Double> dayHistory = new ArrayList<>();
    private final List<Double> massHistory = new ArrayList<>();
    private final List<String> healthHistory = new ArrayList<>();

    private static final double SEED_MASS = 0.1;

    public SimulationState(PlantSpecs initialPlant) {
        this.plant = initialPlant;
        reset();
    }

    public void reset() {
        if (plant != null) {
            temperature = plant.getTempOpt();
            pH = plant.getPHOpt();
        } else {
            temperature = 20.0;
            pH = 6.0;
        }
        speed = 1.0;
        day = 0.0;
        size = SEED_MASS;
        stressDays = 0.0;
        alive = true;
        
        dayHistory.clear();
        massHistory.clear();
        healthHistory.clear();
        
        dayHistory.add(day);
        massHistory.add(size);
        healthHistory.add(getHealthStatus());
    }

    private double computeFactor(double value, double min, double opt, double max) {
        if (value < min || value > max) return 0.0;
        if (value <= opt) {
            return (value - min) / (opt - min);
        } else {
            return (max - value) / (max - opt);
        }
    }

    public double getTempFactor() {
        return computeFactor(temperature, plant.getTempMin(), plant.getTempOpt(), plant.getTempMax());
    }

    public double getPhFactor() {
        return computeFactor(pH, plant.getPHMin(), plant.getPHOpt(), plant.getPHMax());
    }

    /**
     * Implements Liebig’s Law of the Minimum.
     * Growth rate matches the single most scarce bottleneck factor.
     */
    public double getMultiplier() {
        return Math.min(getTempFactor(), getPhFactor());
    }

    public double getEffectiveR() {
        return plant.getRMaxRelative() * getMultiplier();
    }

    public double getGrowthRate() {
        double K = plant.getMaxSize();
        return getEffectiveR() * size * (1 - size / K);
    }

    /**
     * Engine Tick Update.
     * Implements sub-ticking chunks to stabilize Euler numerical integrations at 10x speeds.
     */
    public void tick() {
        if (!alive) return;

        double totalDt = speed; 
        if (totalDt <= 0) return;

        double stepSize = 0.1;
        double accumulatedTime = 0.0;
        double K = plant.getMaxSize();

        while (accumulatedTime < totalDt) {
            double dt = Math.min(stepSize, totalDt - accumulatedTime);

            double tempFactor = getTempFactor();
            double phFactor = getPhFactor();
            boolean lethal = (tempFactor == 0.0 || phFactor == 0.0);

            if (size < K) {
                double r = getEffectiveR();
                double growth = r * size * (1 - size / K) * dt;
                size += growth;
                if (size > K) size = K;
                if (size < 0) size = 0;
            }

            if (lethal) {
                stressDays += dt;
            } else {
                stressDays = Math.max(0, stressDays - 0.5 * dt);
            }

            if (stressDays >= 3.0) {
                alive = false;
                break;
            }

            day += dt;
            accumulatedTime += dt;
        }

        dayHistory.add(day);
        massHistory.add(size);
        healthHistory.add(getHealthStatus());
        
        if (dayHistory.size() > 200) {
            dayHistory.remove(0);
            massHistory.remove(0);
            healthHistory.remove(0);
        }
    }

    public PlantSpecs getPlant() { return plant; }
    public void setPlant(PlantSpecs plant) { this.plant = plant; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getPH() { return pH; }
    public void setPH(double pH) { this.pH = pH; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public double getDay() { return day; }
    public double getSize() { return size; }
    public double getStressDays() { return stressDays; }
    public boolean isAlive() { return alive; }

    public List<Double> getDayHistory() { return dayHistory; }
    public List<Double> getMassHistory() { return massHistory; }
    public List<String> getHealthHistory() { return healthHistory; }

    public String getHealthStatus() {
        if (!alive) return "Dead";
        double t = getTempFactor();
        double p = getPhFactor();
        if (t == 0.0 || p == 0.0) return "Dying";
        if (t >= 0.5 && p >= 0.5) return "Thriving";
        return "Stressed";
    }
}
