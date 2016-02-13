package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class GroupByLogic {

    public static void main(String[] args) {

        final TestScheduler testScheduler = Schedulers.test();
        final PublishSubject<PlayEvent> testSubject = PublishSubject.create();
        TestSubscriber<StreamState> ts = new TestSubscriber<>();

        testSubject.groupBy(playEvt -> playEvt.getOriginatorId())
                .flatMap(groupedObservable -> {
                    System.out.println("**** New Group: " + groupedObservable.getKey());
                    return groupedObservable
                            .timeout(3, TimeUnit.HOURS, testScheduler)
                            .distinctUntilChanged(PlayEvent::getSession)
                            .onErrorResumeNext(t -> {
                                System.out.println("     ***** complete group: " + groupedObservable.getKey());
                                return Observable.empty();
                            })
                            .reduce(new StreamState(), (state, playEvent) -> {
                                System.out.println("    state: " + state + "  event: " + playEvent.id + "-" + playEvent.session);
                                state.addEvent(playEvent);
                                return state;
                            })
                            .filter(state -> {
                                return true;
                            });
                }).doOnNext(e -> System.out.println(">>> Output State: " + e))
                .subscribe(ts);

        testSubject.onNext(createPlayEvent(1, "a"));
        testSubject.onNext(createPlayEvent(2, "a"));
        testScheduler.advanceTimeBy(2, TimeUnit.HOURS);

        testSubject.onNext(createPlayEvent(1, "b"));
        testScheduler.advanceTimeBy(2, TimeUnit.HOURS);

        testSubject.onNext(createPlayEvent(1, "a"));
        testSubject.onNext(createPlayEvent(2, "b"));

        System.out.println("onNext after 4 hours: " + ts.getOnNextEvents());

        testScheduler.advanceTimeBy(3, TimeUnit.HOURS);

        System.out.println("onNext after 7 hours: " + ts.getOnNextEvents());

        testSubject.onCompleted();
        testSubject.onNext(createPlayEvent(2, "b"));

        System.out.println("onNext after complete: " + ts.getOnNextEvents());
        ts.assertTerminalEvent();
        ts.assertNoErrors();
    }

    public static PlayEvent createPlayEvent(int id, String session) {
        return new PlayEvent(id, session);
    }

    public static class PlayEvent {

        private int id;
        private String session;

        public PlayEvent(int id, String session) {
            this.id = id;
            this.session = session;
        }

        public int getOriginatorId() {
            return id;
        }

        public String getSession() {
            return session;
        }
    }

    public static class StreamState {

        private int id = -1;

        public void addEvent(PlayEvent event) {
            if (id == -1) {
                this.id = event.id;
            }
        }

        @Override
        public String toString() {
            return "StreamState => id: " + id;
        }
    }
}
