using System;
using System.Linq;
using System.Reactive.Linq;

namespace Beginners
{
    public class Excercise2
    {        
        public static void Test1()
        {
            IObservable<int> source = null;

            executeSource(source);
        }

        public static void Test2()
        {
            IObservable<int> source = Observable.Empty<int>();

            executeSource(source);
        }

        public static void Test3()
        {
            IObservable<int> source = Observable.Throw<int>(new Exception("Oops"));

            executeSource(source);
        }

        public static void Test4()
        {
            IObservable<int> source = Observable.Return(42);

            executeSource(source);
        }

        public static void Test5()
        {
            IObservable<int> source = Observable.Range(5, 3);

            executeSource(source);
        }

        public static void Test6()
        {
            IObservable<int> source = Observable.Generate(0, i => i < 5, i => i + 1, i => i * i);

            executeSource(source);
        }

        public static void Test7()
        {
            IObservable<int> source = Observable.Never<int>();

            executeSource(source);
        }

        public static void Test8()
        {
            IObservable<int> source = Observable.Range(0, 10);

            source.ForEach(
                x => Console.WriteLine("OnNext:   {0}", x)                
            );

            Console.WriteLine("Press ENTER to unsubscribe...");
            Console.ReadLine();
        }

        public static void Test9()
        {
            IObservable<int> source = Observable.Generate(
                0, i => i < 5,
                i => i + 1,
                i => i * i,
                i => TimeSpan.FromSeconds(i/2)
            );

            using (source.Subscribe(
                x => Console.WriteLine("OnNext:   {0}", x),
                ex => Console.WriteLine("OnError: {0}", ex.Message),
                () => Console.WriteLine("OnCompleted")
            ))
            {
                Console.WriteLine("Press ENTER to unsubscribe...");
                Console.ReadLine();
            }
        }

        private static void executeSource(IObservable<int> source)
        {
            IDisposable subscription = source.Subscribe(
                x => Console.WriteLine("OnNext:   {0}", x),
                ex => Console.WriteLine("OnError: {0}", ex.Message),
                () => Console.WriteLine("OnCompleted")
            );

            Console.WriteLine("Press ENTER to unsubscribe...");
            Console.ReadLine();

            subscription.Dispose();
        }
    }
}
