package intrepid.io.rxjavademo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import intrepid.io.rxjavademo.RestClient;
import intrepid.io.rxjavademo.models.IpModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class RetrofitActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.retrofit_regular)
    public void onRegularClick() {
        RestClient.getIpService().getMyIp().enqueue(new Callback<IpModel>() {
            @Override
            public void onResponse(Call<IpModel> call, Response<IpModel> response) {
                Toast.makeText(context, formatIpMessage(response.body()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<IpModel> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.retrofit_rx)
    public void onRxClick() {
        RestClient.getIpService().getMyIpRx()
                // By default Retrofit subscribes and observes on a background thread, so we need to explicitly
                // tell it to observe on main thread if we are doing any UI work.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<IpModel>>() {
                               @Override
                               public void call(Response<IpModel> ipModelResponse) {
                                   Timber.d("Retrofit success");
                                   Toast.makeText(context, formatIpMessage(ipModelResponse.body()), Toast.LENGTH_SHORT).show();
                               }
                           },
                           new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   Timber.w(throwable, "Retrofit error");
                               }
                           }
                );
    }

    private String formatIpMessage(IpModel ipModel) {
        return "Your ip address is " + ipModel.ip;
    }
}
