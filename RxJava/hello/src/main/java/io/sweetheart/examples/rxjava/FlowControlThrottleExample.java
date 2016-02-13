package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class FlowControlThrottleExample {
    public static void main(String[] args) {
        hotStream()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .take(10)
                .toBlocking()
                .forEach(System.out::println);

        hotStream()
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .take(10)
                .toBlocking()
                .forEach(System.out::println);
    }

    public static Observable<Integer> hotStream() {
        return Observable.create((Subscriber<? super Integer> s) -> {
            int i = 0;
            while (!s.isUnsubscribed()) {
                s.onNext(i++);
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (Exception e) {

                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
}
