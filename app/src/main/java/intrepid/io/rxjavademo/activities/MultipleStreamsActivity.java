package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MultipleStreamsActivity extends AppCompatActivity {

    final Random RANDOM = new Random(System.currentTimeMillis());

    @Bind(R.id.first_sequential)
    TextView firstSequential;
    @Bind(R.id.first_parallel)
    TextView firstParallel;
    @Bind(R.id.second_sequential)
    TextView secondSequential;
    @Bind(R.id.second_parallel)
    TextView secondParallel;
    @Bind(R.id.sum_sequential)
    TextView sumSequential;
    @Bind(R.id.sum_parallel)
    TextView sumParallel;

    private int firstSequentialInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_streams);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start_button)
    public void onStartClick() {
        resetText();
        startSequentialRequest();
        startParallelRequest();
    }

    private void startSequentialRequest() {
        Observable<Integer> observable1 = generateIntegerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable1.flatMap(
                new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        // The threading/scheduler here is specified by observable1.observeOn()
                        firstSequential.setText(integer + "");
                        firstSequentialInt = integer;
                        return generateIntegerObservable();
                    }
                })
                // you can specify different schedulers for the returned observable from flatMap()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        // the threading here is specified by flatMap().observerOn()
                        secondSequential.setText(integer + "");
                        int sum = integer + firstSequentialInt;
                        sumSequential.setText(sum + "");
                    }
                });
    }

    private void startParallelRequest() {
        // observeOn() and cache() are only necessary if you want to add subscriber to each observable in addition to the
        // combined observable stream
        // cache saves the item so that the subscribe here and the combineLatest subscriber sees the same item and
        // don't cause the observable to execute twice.
        Observable<Integer> observable1 = generateIntegerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        Observable<Integer> observable2 = generateIntegerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        observable1.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                firstParallel.setText(integer + "");
            }
        });
        observable2.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                secondParallel.setText(integer + "");
            }
        });

        // Can also use Observable.zip() here. These two have the same behavior when each observable only emits one item.
        // Refer to the Rx documentation for their behaviors when observables emit more than one item
        Observable.combineLatest(
                observable1,
                observable2,
                new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                sumParallel.setText(integer + "");
            }
        });
    }

    private void resetText() {
        firstSequential.setText("");
        firstParallel.setText("");
        secondSequential.setText("");
        secondParallel.setText("");
        sumParallel.setText("");
        sumSequential.setText("");
    }

    private Observable<Integer> generateIntegerObservable() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                randomDelay();
                subscriber.onNext(randomNumber());
            }
        });
    }

    // Simulate a operation that takes between 1 and 5 seconds
    private void randomDelay() {
        try {
            long sleepTime = (long) (1000 + RANDOM.nextFloat() * 4000);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {

        }
    }

    private int randomNumber() {
        return RANDOM.nextInt(100);
    }
}
