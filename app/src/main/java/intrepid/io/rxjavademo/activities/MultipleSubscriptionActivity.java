package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class MultipleSubscriptionActivity extends AppCompatActivity {

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
    }).subscribeOn(Schedulers.computation());

    @Bind(R.id.container)
    LinearLayout container;

    // composite subscription holds a list of child subscriptions so that we can unsubscribe them all at once
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_subscription);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        compositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // IMPORTANT: once a composite subscription has unsubscribed, any child subscriptions added afterwards will
        // also start out as unsubscribed, which means they will not be executed. So we need to create a new
        // compositeSubscription after the old one unsubscribes
        // (ex. here we would create a new composite subscription during onStart, and unsubscribe it during onStop)
        compositeSubscription.unsubscribe();
    }

    @OnClick(R.id.add_subscription_button)
    public void onAddSubscriptionClicked() {
        View row = LayoutInflater.from(this).inflate(R.layout.row_multiple_subscription, container, false);
        int index = container.getChildCount() - 1;
        container.addView(row, index);

        TextView rowNumberText = (TextView) row.findViewById(R.id.row_number);
        rowNumberText.setText("Subscription " + (index + 1) + ": ");

        final TextView rowValueText = (TextView) row.findViewById(R.id.row_value);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
