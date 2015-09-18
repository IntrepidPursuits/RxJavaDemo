package intrepid.io.rxjavademo;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class RxBus {
    private PublishSubject<Object> subject = PublishSubject.create();

    private Map<Object, Subscription> subscriptionMap = new HashMap<>();

    public void post(Object event) {
        subject.onNext(event);
    }

    public void register(final RxEventListener subscriber) {
        Subscription subscription = subject
                .observeOn(AndroidSchedulers.mainThread()) // can remove/change this depending on use case
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        subscriber.onEvent(o);
                    }
                });
        subscriptionMap.put(subscriber, subscription);
    }

    public void unregister(final RxEventListener subscriber) {
        Subscription subscription = subscriptionMap.get(subscriber);
        if (subscription == null) {
            throw new RuntimeException("Attempting to unregister a subscriber that hasn't been registered");
        }
        subscription.unsubscribe();
        subscriptionMap.remove(subscriber);
    }

    public interface RxEventListener {
        void onEvent(Object event);
    }
}