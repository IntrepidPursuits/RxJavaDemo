package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import intrepid.io.rxjavademo.ApiManager;
import intrepid.io.rxjavademo.R;
import intrepid.io.rxjavademo.RxBus;
import intrepid.io.rxjavademo.events.IpUpdatedEvent;
import intrepid.io.rxjavademo.events.NumberGeneratedEvent;
import intrepid.io.rxjavademo.models.IpModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventBusActivity extends AppCompatActivity implements RxBus.RxEventListener {

    private final Random random = new Random();

    private final RxBus bus = new RxBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @OnClick(R.id.generate_number)
    public void onGenerateNumberClick() {
        int randInt = random.nextInt();
        bus.post(new NumberGeneratedEvent(randInt));
    }

    @OnClick(R.id.get_ip)
    public void onGetIpClick() {
        ApiManager.getIpService().getMyIp().enqueue(new Callback<IpModel>() {
            @Override
            public void onResponse(Call<IpModel> call, Response<IpModel> response) {
                bus.post(new IpUpdatedEvent(response.body()));
            }

            @Override
            public void onFailure(Call<IpModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onEvent(Object event) {
        // boilerplate code to demux the event to specific handlers
        // this is the reason why Otto and EventBus are more useful if we need a pub/sub system
        if (event instanceof NumberGeneratedEvent) {
            onNumberGenerate((NumberGeneratedEvent) event);
        } else if (event instanceof IpUpdatedEvent) {
            onIpUpdated((IpUpdatedEvent) event);
        } //etc
    }

    private void onNumberGenerate(NumberGeneratedEvent event) {
        int number = event.getNumber();
        Toast.makeText(this, "Got number " + number, Toast.LENGTH_SHORT).show();
    }

    private void onIpUpdated(IpUpdatedEvent event) {
        IpModel ipModel = event.getIpModel();
        Toast.makeText(this, "Your ip address is " + ipModel.ip, Toast.LENGTH_SHORT).show();
    }
}
