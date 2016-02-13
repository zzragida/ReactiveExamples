package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlowControlDebounceBuffer {

    public static void main(String[] args) {

        Observable<Integer> burstStream = intermittentBursts().take(20).publish().refCount();
        Observable<Integer>  debounced = burstStream.debounce(10, TimeUnit.MILLISECONDS);
        Observable<List<Integer>> buffered = burstStream.buffer(debounced);
        buffered.toBlocking().forEach(System.out::println);
    }

    public static Observable<Integer> intermittentBursts() {
        return Observable.create((Subscriber<? super Integer> s) -> {
            while (!s.isUnsubscribed()) {
                for (int i = 0; i < Math.random() * 20; i++) {
                    s.onNext(i);
                }
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (Exception e) {

                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
}
