package intrepid.io.rxjavademo;

import intrepid.io.rxjavademo.models.IpModel;
import intrepid.io.rxjavademo.models.WeatherModel;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public class ApiManager {
    private static IpService ipService;
    private static WeatherService weatherService;

    public static IpService getIpService() {
        if (ipService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.ipify.org")
                    .build();
            ipService = restAdapter.create(IpService.class);
        }
        return ipService;
    }

    public interface IpService {
        @GET("/?format=json")
        void getMyIp(Callback<IpModel> callback);

        @GET("/?format=json")
        Observable<IpModel> getMyIpRx();
    }

    public static WeatherService getWeatherService() {
        if (weatherService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.openweathermap.org/data/2.5")
                    .build();
            ipService = restAdapter.create(IpService.class);
        }
        return weatherService;
    }

    public interface WeatherService {
        @GET("/weather")
        Observable<WeatherModel> getWeather(@Query("q") String location);
    }
}
