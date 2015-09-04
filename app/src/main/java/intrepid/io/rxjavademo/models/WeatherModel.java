package intrepid.io.rxjavademo.models;

public class WeatherModel {

    private Weather weather;
    private Temperature temperature;

    public String getCondition() {
        return weather.main;
    }

    public double getTemperatureInCelsius() {
        return temperature.temp - 272.15;
    }

    public static class Weather {
        private String main;
    }

    public static class Temperature {
        private double temp;
    }
}
