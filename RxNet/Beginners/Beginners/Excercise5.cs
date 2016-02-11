using System;
using System.Reactive.Linq;
using System.Reactive;
using System.Windows.Forms;

namespace Beginners
{
    class Excercise5
    {
        public static void Test1()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = {txt}
            };

            var input = from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                        select ((TextBox)evt.Sender).Text;

            using (input.Subscribe(inp => Console.WriteLine("User wrote: " + inp)))
            {
                Application.Run(frm);
            }
        }

        public static void Test2()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = { txt }
            };

            var input = (from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                         .Do(inp => Console.WriteLine("Before DistinctUntilChanged: " + inp))
                         .DistinctUntilChanged();

            using (input.Subscribe(inp => Console.WriteLine("User wrote: " + inp)))
            {
                Application.Run(frm);
            }
        }

        public static void Test3()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = { txt }
            };

            var input = (from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                         .Timestamp()
                         .Do(inp => Console.WriteLine("I: " + inp.Timestamp.Millisecond + " - " + inp.Value))
                         .Select(x => x.Value)
                         .Throttle(TimeSpan.FromSeconds(1))
                         .Timestamp()
                         .Do(inp => Console.WriteLine("T: " + inp.Timestamp.Millisecond + " - " + inp.Value))
                         .Select(x => x.Value)
                         .DistinctUntilChanged();

            using (input.Subscribe(inp => Console.WriteLine("User wrote: " + inp)))
            {
                Application.Run(frm);
            }
        }

        public static void Test4()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = { txt }
            };

            var input = (from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                         .LogTimestampedValues(x => Console.WriteLine("I: " + x.Timestamp.Millisecond + " - " + x.Value))
                         .Throttle(TimeSpan.FromSeconds(1))
                         .LogTimestampedValues(x => Console.WriteLine("T: " + x.Timestamp.Millisecond + " - " + x.Value))
                         .DistinctUntilChanged();

            using (input.Subscribe(inp => Console.WriteLine("User wrote: " + inp)))
            {
                Application.Run(frm);
            }
        }
    }

    public static class MyExtensions
    {
        public static IObservable<T> LogTimestampedValues<T>(this IObservable<T> source, Action<Timestamped<T>> onNext)
        {
            return source.Timestamp().Do(onNext).Select(x => x.Value);
        }
    }
}
