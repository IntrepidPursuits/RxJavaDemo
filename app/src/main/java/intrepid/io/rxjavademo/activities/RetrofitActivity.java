package intrepid.io.rxjavademo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.ApiManager;
import intrepid.io.rxjavademo.R;
import intrepid.io.rxjavademo.models.IpModel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class RetrofitActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.retrofit_regular)
    public void onRegularClick() {
        ApiManager.getIpService().getMyIp(new Callback<IpModel>() {
            @Override
            public void success(IpModel ipModel, Response response) {
                Toast.makeText(context, formatIpMessage(ipModel), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @OnClick(R.id.retrofit_rx)
    public void onRxClick() {
        // use this callback if you just want to handle the success condition
        Action1<IpModel> subscriber1 = new Action1<IpModel>() {
            @Override
            public void call(IpModel ipModel) {
                Toast.makeText(context, formatIpMessage(ipModel), Toast.LENGTH_SHORT).show();
            }
        };

        // use this subscriber if you want to handle errors and have a onComplete callback after all items are emitted
        // (onComplete is basically onNext in this case since there's only one item)
        Subscriber<IpModel> subscriber2 = new Subscriber<IpModel>() {
            @Override
            public void onCompleted() {
                Timber.d("Retrofit completed");
            }

            @Override
            public void onError(Throwable e) {
                // The throwable here is usually a RetrofitError
                Timber.w(e, "Retrofit error");
            }

            @Override
            public void onNext(IpModel ipModel) {
                // One of caveats of using RxJava over regular Retrofit is that the callback doesn't have the Response object
                Timber.d("Retrofit success");
                Toast.makeText(context, formatIpMessage(ipModel), Toast.LENGTH_SHORT).show();
            }
        };

        // By default Retrofit subscribes and observes on a background thread, so we need to explictly tell it to
        // observe on main thread if we are doing any UI work.
        ApiManager.getIpService().getMyIpRx().observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber2);
    }

    private String formatIpMessage(IpModel ipModel) {
        return "Your ip address is " + ipModel.ip;
    }
}
