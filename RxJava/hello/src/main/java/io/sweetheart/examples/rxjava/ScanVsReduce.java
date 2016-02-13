package io.sweetheart.examples.rxjava;

import rx.Observable;

import java.util.ArrayList;

public class ScanVsReduce {

    public static void main(String... args) {
        Observable.range(0, 10).reduce(new ArrayList<>(), (list, i) -> {
            list.add(i);
            return list;
        }).forEach(System.out::println);

        System.out.println("---- vs ----");

        Observable.range(0, 10).scan(new ArrayList<>(), (list, i) -> {
            list.add(i);
            return list;
        }).forEach(System.out::println);
    }
}
