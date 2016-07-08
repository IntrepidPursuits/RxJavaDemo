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
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class HotObservableActivity extends AppCompatActivity {

    /**
     * By default, observables are "Cold", which means the the code inside the Observable block only get executed when
     * something subscribe to it. Every subscription will cause that code to get triggered again, so each subscriber will
     * actually subscribe to different stream of items/events (See MultipleSubscriptionActivity). On the other hand, A
     * "Hot" observable can execute its code without subscribers, and each of its subscriber will see the same stream of content
     * There are multiple ways to make an observable "Hot", one of which is to call publish() after creating the observable
     * This activity is very similar to MultipleSubscriptionActivity, except that we made the observable Hot by calling publish()
     */

    private final ConnectableObservable<Long> TIMER_OBSERVABLE = Observable.create(new Observable.OnSubscribe<Long>() {
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
    }).subscribeOn(Schedulers.io())
            // Calling publish will turn the observable into a hot "ConnectableObservable". ConnectableObservable will
            // only start emitting when connect() gets called.
            .publish();

    @Bind(R.id.container)
    LinearLayout container;

    // composite subscription holds a list of child subscriptions so that we can unsubscribe them all at once
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_observable);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        compositeSubscription.clear();
    }

    @OnClick(R.id.start_emitting_button)
    public void onStartEmittingClick() {
        Subscription connectionSubscription = TIMER_OBSERVABLE.connect();

        // IMPORTANT: don't forgot to disconnect the ConnectableObservable when you are done
        // here we use compositeSubscription to disconnect when activity stops
        compositeSubscription.add(connectionSubscription);
    }

    @OnClick(R.id.add_subscription_button)
    public void onAddSubscriptionClicked() {
        View row = LayoutInflater.from(this).inflate(R.layout.row_multiple_subscriptions, container, false);
        int index = container.getChildCount() - 1;
        container.addView(row, index);

        TextView rowNumberText = (TextView) row.findViewById(R.id.row_number);
        rowNumberText.setText("Subscription " + (index) + ": ");

        final TextView rowValueText = (TextView) row.findViewById(R.id.row_value);
        // Since the observable is hot, each subscriber will subscribe to the same timer. So they will display the same
        // count
        Subscription subscription = TIMER_OBSERVABLE
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        rowValueText.setText("" + count);
                    }
                });
        compositeSubscription.add(subscription);
    }
}
