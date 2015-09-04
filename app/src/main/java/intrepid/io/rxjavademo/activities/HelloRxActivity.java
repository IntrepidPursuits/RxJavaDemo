package intrepid.io.rxjavademo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class HelloRxActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.hello_world)
    public void onNormalClick() {
        String s = "Hello world";
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.rx_hello_world)
    public void onRxClick() {
        // this does the same thing as onNormalClick()
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello world Rx");
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
