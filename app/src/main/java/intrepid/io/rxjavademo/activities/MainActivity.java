package intrepid.io.rxjavademo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.hello_world)
    public void onHellowWorldClick() {
        startActivity(HelloRxActivity.class);
    }

    @OnClick(R.id.threading)
    public void onThreadingClick() {
        startActivity(ThreadingActivity.class);
    }

    @OnClick(R.id.retrofit)
    public void onRetrofitClick() {
        startActivity(RetrofitActivity.class);
    }

    @OnClick(R.id.timer)
    public void onTimerClick() {
        startActivity(TimerActivity.class);
    }

    @OnClick(R.id.multiple_subscriptions)
    public void onMultipleSubscriptionsClick() {
        startActivity(MultipleSubscriptionsActivity.class);
    }

    @OnClick(R.id.hot_observable)
    public void onHotObservableClick() {
        startActivity(HotObservableActivity.class);
    }

    @OnClick(R.id.multiple_streams)
    public void onMultipleStreamClick() {
        startActivity(MultipleStreamsActivity.class);
    }

    @OnClick(R.id.event_bus)
    public void onEventBusClick() {
        startActivity(EventBusActivity.class);
    }

    private void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
