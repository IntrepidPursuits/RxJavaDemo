package intrepid.io.rxjavademo.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.R;
import rx.Observable;
import rx.Subscriber;
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

    @OnClick(R.id.rx_hello_world)
    public void onClick1() {
        // the subscription and observation takes place on the current thread (i.e. main thread). Resulting in
        // UI freezing for few seconds
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                operationThatTakesFewSeconds();
                subscriber.onNext("Hello world");
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.subscribe_only)
    public void onClick2() {
        // the subscription takes place in background thread. Since we didin't specify the observation thread, it will
        // take place in same thread as the subscription. This will result in crash since Toast must be called in
        // the main thread
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                operationThatTakesFewSeconds();
                subscriber.onNext("Hello world");
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                // crash incoming
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.subscribe_and_observe)
    public void onClick3() {
        // here we specify the subscription to take place in background thread and the observation to take place in the
        // main thread.
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                operationThatTakesFewSeconds();
                subscriber.onNext("Hello world");
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello_world, menu);
        return true;
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