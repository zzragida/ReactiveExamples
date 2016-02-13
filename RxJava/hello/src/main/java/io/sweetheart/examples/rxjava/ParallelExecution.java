package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ParallelExecution {

    public static void main(String[] args) {

        mergingAsync();
        mergingSync();
        mergingSyncMadeAsync();
        flatMapExampleAsync();
        flatMapExampleSync();
        flatMapBufferedExampleAsync();
        flatMapWindowedExampleAsync();
    }

    private static void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2))
                .toBlocking()
                .forEach(System.out::println);
    }

    private static void mergingSync() {
        Observable.merge(getDataSync(1), getDataSync(2))
                .toBlocking()
                .forEach(System.out::println);
    }

    private static void mergingSyncMadeAsync() {
        Observable.merge(getDataSync(1).subscribeOn(Schedulers.io()),
                         getDataSync(2).subscribeOn(Schedulers.io()))
                .toBlocking()
                .forEach(System.out::println);

    }

    private static void flatMapExampleAsync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataAsync(i);
        }).toBlocking().forEach(System.out::println);
    }

    private static void flatMapExampleSync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataSync(i);
        }).toBlocking().forEach(System.out::println);
    }

    private static void flatMapBufferedExampleAsync() {
        Observable.range(0, 5000).buffer(500).flatMap(i -> {
            return Observable.from(i).subscribeOn(Schedulers.computation()).map(item -> {
               try {
                   Thread.sleep(1);
               } catch (Exception e) {
                   e.printStackTrace();
               }
                return item + " processed " + Thread.currentThread();
            });
        }).toBlocking().forEach(System.out::println);
    }

    private static void flatMapWindowedExampleAsync() {
        Observable.range(0, 5000).window(500).flatMap(work -> {
            return work.observeOn(Schedulers.computation()).map(item -> {
               try {
                   Thread.sleep(1);
               } catch (Exception e) {
                   e.printStackTrace();
               }
                return item + " processed " + Thread.currentThread();
            });
        }).toBlocking().forEach(System.out::println);
    }

    private static Observable<Integer> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    private static Observable<Integer> getDataSync(int i) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(i);
            s.onCompleted();
        });
    }
}
