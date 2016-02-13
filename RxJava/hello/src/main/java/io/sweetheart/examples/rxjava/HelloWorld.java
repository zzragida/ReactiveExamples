package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class HelloWorld {

    public static void main(String[] args) {

        // Hello World
        Observable.create(subscriber -> {
            subscriber.onNext("Hello World");
            subscriber.onCompleted();
        }).subscribe(System.out::println);

        // shorten by using helper
        Observable.just("Hello", "World")
                .subscribe(System.out::println);

        // add onError and onComplete listeners
        Observable.just("Hello World")
                .subscribe(System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("Done"));

        // expand to show full classes
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello World");
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Done");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        });

        // add error propagation
        Observable.create(subscriber -> {
            try {
                subscriber.onNext("Hello World");
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribe(System.out::println);

        // add concurrency using Thread
        Observable.create(subscriber -> {
            new Thread(() -> {
                try {
                    subscriber.onNext(getData());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }).start();
        }).subscribe(System.out::println);

        // add concurrency using Scheduler
        Observable.create(subscriber -> {
            try {
                subscriber.onNext(getData());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(System.out::println);

        // infinite
        Observable.create(subscriber -> {
            int i = 0;
            while (!subscriber.isUnsubscribed()) {
                subscriber.onNext(i++);
            }
        }).take(10).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed");
        }).onErrorResumeNext(throwable -> {
            return Observable.just("fallback value");
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed");
        }).onErrorResumeNext(Observable.just("fallback value"))
                .subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed");
        }).onErrorReturn(throwable -> {
            return "fallback value";
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed");
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (throwable, i) -> i)
                    .flatMap(i -> {
                        System.out.println("delay retry by " + i + " seconds");
                        return Observable.timer(i, TimeUnit.SECONDS);
                    }).concatWith(Observable.error(new RuntimeException("Exceeded 3 retries")));
        }).subscribe(System.out::println, t -> t.printStackTrace());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getData() {
        return "Got Data";
    }
}
