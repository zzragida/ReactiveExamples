package io.sweetheart.examples.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.List;

public class ParallelExecutionExample {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        Observable<Tile> searchTile = getSearchResults("search term")
                .doOnSubscribe(() -> logTime("Search started ", startTime))
                .doOnCompleted(() -> logTime("Search completed ", startTime));

        Observable<TileResponse> populatedTiles = searchTile.flatMap(t -> {
            Observable<Reviews> reviews = getSellerReviews(t.getSellerId())
                    .doOnCompleted(() -> logTime("getSellerReviews[" + t.id + "] completed ", startTime));
            Observable<String> imageUrl = getProductImage(t.getProductId())
                    .doOnCompleted(() -> logTime("getProductImage[" + t.id + "] completed ", startTime));

            return Observable.zip(reviews, imageUrl, (r, u) -> {
                return new TileResponse(t, r, u);
            }).doOnCompleted(() -> logTime("zip[" + t.id + "] completed ", startTime));
        });

        List<TileResponse> allTiles = populatedTiles.toList()
                .doOnCompleted(() -> logTime("All Tiles Completed ", startTime))
                .toBlocking().single();
    }

    public static void run() {
        Observable<Tile> searchTile = getSearchResults("search term");

        Observable<TileResponse> populatedTiles = searchTile.flatMap(t -> {
            Observable<Reviews> reviews = getSellerReviews(t.getSellerId());
            Observable<String> imageUrl = getProductImage(t.getProductId());

            return Observable.zip(reviews, imageUrl, (r, u) -> {
                return new TileResponse(t, r, u);
            });
        });

        List<TileResponse> allTiles = populatedTiles.toList()
                .toBlocking().single();
    }

    private static Observable<Tile> getSearchResults(String string) {
        return mockClient(new Tile(1), new Tile(2), new Tile(3));
    }

    private static Observable<Reviews> getSellerReviews(int id) {
        return mockClient(new Reviews());
    }

    private static Observable<String> getProductImage(int id) {
        return mockClient("image_" + id);
    }

    private static void logTime(String message, long startTime) {
        System.out.println(message + " => " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static <T> Observable<T> mockClient(T... ts) {
        return Observable.create((Subscriber<? super T> s) -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (T t : ts) {
                s.onNext(t);
            }
            s.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    public static class TileResponse {

        private final Tile tile;
        private final Reviews reviews;
        private final String name;

        public TileResponse(Tile t, Reviews r, String s) {
            this.tile = t;
            this.reviews = r;
            this.name = s;
        }
    }

    public static class Tile {

        private final int id;

        public Tile(int i) {
            this.id = i;
        }

        public int getSellerId() {
            return this.id;
        }

        public int getProductId() {
            return this.id;
        }
    }

    public static class Reviews {

    }
}
