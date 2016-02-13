package io.sweetheart.examples.rxjava;

import rx.Observable;

import java.util.concurrent.TimeUnit;

public class ErrorHandlingRetryWithBackoff {

    public static void main(String... args) {

        // retry(n) can be used to immediately retry n times
        Observable.create(s -> {
            System.out.println("1) subscribing");
            s.onError(new RuntimeException("1) always fails"));
        }).retry(3).subscribe(System.out::println, t -> System.out.println("1) Error: " + t));

        // retryWhen allows custom behavior on when and if a retry should be done
        Observable.create(s -> {
            System.out.println("2) subscribing");
            s.onError(new RuntimeException("2) always fails"));
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
                System.out.println("2) delay retry by " + i + " second(s)");
                return Observable.timer(i, TimeUnit.SECONDS);
            }).concatWith(Observable.error(new RuntimeException("Failed after 3 retries")));
        }).toBlocking().forEach(System.out::println);
    }
}
