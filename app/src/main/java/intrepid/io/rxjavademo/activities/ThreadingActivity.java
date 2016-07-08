package intrepid.io.rxjavademo.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ThreadingActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threading);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.no_threading)
    public void onNoThreadingClick() {
        // the subscription and observation takes place on the current thread (i.e. main thread). Resulting in
        // UI freezing for few seconds
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        operationThatTakesFewSeconds();
                        return "Hello world";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick(R.id.subscribe_only)
    public void onSubscribeOnlyClick() {
        // the subscription takes place in background thread. Since we didin't specify the observation thread, it will
        // take place in same thread as the subscription. This will result in crash since Toast must be called in
        // the main thread
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        operationThatTakesFewSeconds();
                        return "Hello world";
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        // crash incoming
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick(R.id.subscribe_and_observe)
    public void onSubscribeAndObserveClick() {
        // here we specify the subscription to take place in background thread and the observation to take place in the
        // main thread.
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        operationThatTakesFewSeconds();
                        return "Hello world";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.async_task)
    public void onClick4() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                operationThatTakesFewSeconds();
                return "Hello world";
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void operationThatTakesFewSeconds() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
    }
}
