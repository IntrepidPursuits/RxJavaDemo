package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class TimerActivity extends AppCompatActivity {

    private static final int INITIAL_DELAY = 0;
    private static final int PERIOD = 1;

    // By default, the timer loops on a background thread via Schedulers.computation()
    private final Observable<Long> TIMER_OBSERVABLE = Observable.interval(INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);

    @Bind(R.id.counter_text)
    TextView counterText;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscription = TIMER_OBSERVABLE
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        counterText.setText("" + count);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // don't forget to unsubscribe when the activity is backgrounded
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
