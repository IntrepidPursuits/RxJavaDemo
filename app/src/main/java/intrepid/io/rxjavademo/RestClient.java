package intrepid.io.rxjavademo;

import intrepid.io.rxjavademo.models.IpModel;
import intrepid.io.rxjavademo.models.WeatherModel;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RestClient {
    private static IpService ipService;
    private static WeatherService weatherService;

    public static IpService getIpService() {
        if (ipService == null) {
            RxJavaCallAdapterFactory rxAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()); //specifies the default subscribeOn() scheduler
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.ipify.org")
                    .addCallAdapterFactory(rxAdapterFactory)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
            ipService = retrofit.create(IpService.class);
        }
        return ipService;
    }

    public interface IpService {
        @GET("/?format=json")
        Call<IpModel> getMyIp();

        @GET("/?format=json")
        Observable<Response<IpModel>> getMyIpRx(); // Use this version if you want additional info such as status code and headers
        //Observable<IpModel> getMyIpRx(); // Use this version if you only care about the resulting data model

    }

    public static WeatherService getWeatherService() {
        if (weatherService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org/data/2.5")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
            ipService = retrofit.create(IpService.class);
        }
        return weatherService;
    }

    public interface WeatherService {
        @GET("/weather")
        Observable<WeatherModel> getWeather(@Query("q") String location);
    }
}
