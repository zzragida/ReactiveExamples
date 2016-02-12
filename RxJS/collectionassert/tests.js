var collectionAssert = require('./collectionassert');
var Rx = require('rx');

var TestScheduler = Rx.TestScheduler
  , Observable = Rx.Observable
  , onNext = Rx.ReactiveTest.onNext
  , onError = Rx.ReactiveTest.onError
  , onCompleted = Rx.ReactiveTest.onCompleted
  , subscribe = Rx.ReactiveTest.subscribe;

describe('Observable', function () {

  // Observable.return tests
  describe('.return', function () {

    it('should return single value and complete', function () {

      var scheduler = new TestScheduler();

      var results = scheduler.startScheduler(function () {
        return Observable.return(42, scheduler);
      });

      var expectedMessages = [
        onNext(201, 42),
        onCompleted(201)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);
    });
  });

  // Observable#map tests
  describe('#map', function () {
     
    it('should project single item and complete', function () {
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onNext(210, 2),
        onCompleted(220)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { return x + x; } );
      });

      var expectedMessages = [
        onNext(210, 4),
        onCompleted(220)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 220)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should project multiple items and then complete', function () {
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onNext(210, 2),
        onNext(215, 3),
        onCompleted(220)
      );
  
      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { return x + x; });
      });

      var expectedMessages = [
        onNext(210, 4),
        onNext(215, 6),
        onCompleted(220)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 220)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should project complete if empty', function () {
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onCompleted(220)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { return x + x; });
      });

      var expectedMessages = [
        onCompleted(220)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 220)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should never project if no messages', function () {
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { return x + x; });
      });

      var expectedMessages = [
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 1000)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should project an error if sequence has an error', function () {
      var error = new Error('woops');
      var scheduler = new TestScheduler();

      // Project forward one onError after subscribe
      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onError(210, error)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { return x + x; });
      });

      // Should expect only one message with an error at 210
      var expectedMessages = [
        onError(210, error)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      // Should subscribe at 200 and unsubscribe at 210 at point of error
      var expectedSubscriptions = [
        subscribe(200, 210)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should project an error if the selector throws', function () {
      var error = new Error('woops');
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onNext(210, 2)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x) { throw error; });
      });

      var expectedMessages = [
        onError(210, error)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 210)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });

    it('should project a single value with index and then complete', function () {
      var scheduler = new TestScheduler();

      var xs = scheduler.createHotObservable(
        onNext(150, 1),
        onNext(210, 2),
        onCompleted(220)
      );

      var results = scheduler.startScheduler(function () {
        return xs.map(function (x, i) { return (x + x) * i; });
      });

      var expectedMessages = [
        onNext(210, 0),
        onCompleted(220)
      ];

      collectionAssert.assertEqual(expectedMessages, results.messages);

      var expectedSubscriptions = [
        subscribe(200, 220)
      ];

      collectionAssert.assertEqual(expectedSubscriptions, xs.subscriptions);
    });
  });

});
