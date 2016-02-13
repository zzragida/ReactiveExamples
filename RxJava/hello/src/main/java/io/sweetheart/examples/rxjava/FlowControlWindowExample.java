package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class FlowControlWindowExample {

    public static void main(String[] args) {
        hotStream()
                .window(500, TimeUnit.MILLISECONDS)
                .take(10)
                .flatMap(w -> w.startWith(99999999))
                .toBlocking()
                .forEach(System.out::println);

        hotStream()
                .window(10)
                .take(2)
                .flatMap(w -> w.startWith(99999999))
                .toBlocking()
                .forEach(System.out::println);

        System.out.println("Done");
    }

    public static Observable<Integer> hotStream() {
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
