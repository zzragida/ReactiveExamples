using System;
using System.Reactive.Linq;
using System.Reactive.Disposables;
using System.Windows.Forms;

namespace Beginners
{
    class Excercise4
    {
        public static void Test1()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = {txt}
            };

            var moves = Observable.FromEventPattern<MouseEventArgs>(frm, "MouseMove");
            var input = Observable.FromEventPattern<EventArgs>(txt, "TextChanged");

            var moveSubscription = moves.Subscribe(evt =>
            {
                Console.WriteLine("Mouse at: " + evt.EventArgs.Location);
            });
            var inputSubscription = input.Subscribe(evt =>
            {
                Console.WriteLine("User wrote: " + ((TextBox)evt.Sender).Text);
            });

            using (new CompositeDisposable(moveSubscription, inputSubscription))
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

            var moves = from evt in Observable.FromEventPattern<MouseEventArgs>(frm, "MouseMove")
                        select evt.EventArgs.Location;
            var input = from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                        select ((TextBox)evt.Sender).Text;

            var moveSubscription = moves.Subscribe(pos => Console.WriteLine("Mouse at: " + pos));
            var inputSubscription = input.Subscribe(inp => Console.WriteLine("User wrote: " + inp));

            using (new CompositeDisposable(moveSubscription, inputSubscription))
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

            var moves = from evt in Observable.FromEventPattern<MouseEventArgs>(frm, "MouseMove")
                        select evt.EventArgs.Location;
            var input = from evt in Observable.FromEventPattern<EventArgs>(txt, "TextChanged")
                        select ((TextBox)evt.Sender).Text;

            var overFirstBisector = from pos in moves
                                    where pos.X == pos.Y
                                    select pos;
            var moveSubscription = overFirstBisector.Subscribe(pos => Console.WriteLine("Mouse at: " + pos));
            var inputSubscription = input.Subscribe(inp => Console.WriteLine("User wrote: " + inp));

            using (new CompositeDisposable(moveSubscription, inputSubscription))
            {
                Application.Run(frm);
            }
        }
    }
}
