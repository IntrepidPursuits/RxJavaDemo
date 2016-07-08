package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MultipleStreamsActivity extends AppCompatActivity {
    private static final int FIRST_NUM = 6000;
    private static final int SECOND_NUM = 3000;

    private static final long FIRST_DELAY = 6000;
    private static final long SECOND_DELAY = 3000;

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
        getFirstNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        firstSequential.setText(integer + "");
                        firstSequentialInt = integer;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return getSecondNum();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        secondSequential.setText(integer + "");
                        int sum = integer + firstSequentialInt;
                        sumSequential.setText(sum + "");
                    }
                });
    }

    private void startParallelRequest() {
        Observable<Integer> observable1 = getFirstNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        firstParallel.setText(integer + "");
                    }
                })
                .observeOn(Schedulers.io());

        Observable<Integer> observable2 = getSecondNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        secondParallel.setText(integer + "");
                    }
                })
                .observeOn(Schedulers.io());

        // Can also use Observable.zip() here. These two have the same behavior when each observable only emits one item.
        // Refer to the Rx documentation for their behaviors when observables emit more than one item
        Observable.combineLatest(observable1,
                                 observable2,
                                 new Func2<Integer, Integer, Integer>() {
                                     @Override
                                     public Integer call(Integer integer, Integer integer2) {
                                         return integer + integer2;
                                     }
                                 })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
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

    private Observable<Integer> getFirstNum() {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                delay(FIRST_DELAY);
                return FIRST_NUM;
            }
        });
    }

    private Observable<Integer> getSecondNum() {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                delay(SECOND_DELAY);
                return SECOND_NUM;
            }
        });
    }

    private void delay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {

        }
    }
}
