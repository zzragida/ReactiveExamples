package io.sweetheart.examples.rxjava;

import rx.Observable;
import java.util.concurrent.TimeUnit;

public class ZipInterval {

    public static void main(String... args) {
        Observable<String> data = Observable.just("one", "two", "three", "four", "five");
        Observable.zip(data, Observable.interval(1, TimeUnit.SECONDS), (d, t) -> {
            return d + " " + (t+1);
        }).toBlocking().forEach(System.out::println);
    }
}
