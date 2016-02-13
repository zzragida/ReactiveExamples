package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public class FlowControlReactivePullCold {

    public static void main(String[] args) {
        getData(1)
                .observeOn(Schedulers.computation())
                .toBlocking()
                .forEach(System.out::println);
    }

    public static Observable<Integer> getData(int id) {
        final ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            data.add(i + id);
        }
        return fromIterable(data);
    }

    public static Observable<Integer> fromIterable(Iterable<Integer> it) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            final Iterator<Integer> iter = it.iterator();
            final AtomicLong requested = new AtomicLong();
            s.setProducer((long request) -> {
                if (requested.getAndAdd(request) == 0) {
                    do {
                        if (s.isUnsubscribed()) {
                            return;
                        }
                        if (iter.hasNext()) {
                            s.onNext(iter.next());
                        } else {
                            s.onCompleted();
                        }
                    } while (requested.decrementAndGet() > 0);
                }
            });
        });
    }
}
