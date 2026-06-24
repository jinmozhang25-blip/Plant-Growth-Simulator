public class PlantSpecs {
    private final String name;
    private final double tempMin, tempOpt, tempMax;
    private final double pHMin, pHOpt, pHMax;
    private final double rMaxRelative;
    private final double maxSize;

    public PlantSpecs(String name, double tempMin, double tempOpt, double tempMax,
                      double pHMin, double pHOpt, double pHMax,
                      double rMaxRelative, double maxSize) {
        this.name = name;
        this.tempMin = tempMin;
        this.tempOpt = tempOpt;
        this.tempMax = tempMax;
        this.pHMin = pHMin;
        this.pHOpt = pHOpt;
        this.pHMax = pHMax;
        this.rMaxRelative = rMaxRelative;
        this.maxSize = maxSize;
    }

    public String getName() { return name; }
    public double getTempMin() { return tempMin; }
    public double getTempOpt() { return tempOpt; }
    public double getTempMax() { return tempMax; }
    public double getPHMin() { return pHMin; }
    public double getPHOpt() { return pHOpt; }
    public double getPHMax() { return pHMax; }
    public double getRMaxRelative() { return rMaxRelative; }
    public double getMaxSize() { return maxSize; }
}
