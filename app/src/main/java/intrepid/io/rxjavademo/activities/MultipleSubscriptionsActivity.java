package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MultipleSubscriptionsActivity extends AppCompatActivity {

    private final Observable<Long> TIMER_OBSERVABLE = Observable.create(new Observable.OnSubscribe<Long>() {
        @Override
        public void call(Subscriber<? super Long> subscriber) {
            long count = 0;
            while (!subscriber.isUnsubscribed()) {
                Timber.d(this + ": emitting " + count);
                subscriber.onNext(count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
                count++;
            }
        }
    }).subscribeOn(Schedulers.io());

    @Bind(R.id.container)
    LinearLayout container;

    // composite subscription holds a list of child subscriptions so that we can unsubscribe them all at once
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_subscriptions);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // IMPORTANT: there's a distinction between `compositeSubscription.clear()` and `compositeSubscription.unsubscribe()`
        // both clear() and unsubscribe() will unsubscribe and remove all the child subscriptions. However, calling
        // unsubscribe will cause compositeSubscription to unsubscribe itself, which means any additional child
        // subscription added will start off and remain in unsubscribed state, which means they won't be executed
        // Calling clear() will still allow compositeSubscription to accept additional child subscriptions normally.
        compositeSubscription.clear();
    }

    @OnClick(R.id.add_subscription_button)
    public void onAddSubscriptionClicked() {
        View row = LayoutInflater.from(this).inflate(R.layout.row_multiple_subscriptions, container, false);
        int index = container.getChildCount() - 1;
        container.addView(row, index);

        TextView rowNumberText = (TextView) row.findViewById(R.id.row_number);
        rowNumberText.setText("Subscription " + (index + 1) + ": ");

        final TextView rowValueText = (TextView) row.findViewById(R.id.row_value);
        // The code inside TIMER_OBSERVABLE's call gets executed every time something subscribes to it. So each
        // subscriber will have it's own timer.
        // This is the default behavior observables, also known as "Cold observables"
        Subscription subscription = TIMER_OBSERVABLE
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        rowValueText.setText("" + count);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.w(throwable, "error");
                    }
                });
        compositeSubscription.add(subscription);
    }
}
