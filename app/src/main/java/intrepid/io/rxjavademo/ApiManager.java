package intrepid.io.rxjavademo;

import intrepid.io.rxjavademo.models.IpModel;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;

public class ApiManager {
    private static IpService ipService;

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
}
