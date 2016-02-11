using System;
using System.Reactive.Linq;
using System.Reactive.Concurrency;
using System.Windows.Forms;

namespace Beginners
{
    class Excercise6
    {
        public static void Test1()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = { txt }
            };

            var input = (from evt in Observable.FromEventPattern(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                        .Throttle(TimeSpan.FromSeconds(1))
                        .DistinctUntilChanged();

            using (input.Subscribe(x => Console.WriteLine("User wrote: " + x)))
            {
                Application.Run(frm);
            }
        }

        public static void Test2()
        {
            var txt = new TextBox();
            var lbl = new Label { Left = txt.Width + 20, Text = "label" };

            var frm = new Form()
            {
                Controls = { txt, lbl }
            };

            var input = (from evt in Observable.FromEventPattern(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                         .Throttle(TimeSpan.FromSeconds(1))
                         .DistinctUntilChanged();

            using (input.Subscribe(inp => lbl.Text = inp))
            {
                Application.Run(frm);
            }
        }

        public static void Test3()
        {
            var txt = new TextBox();
            var lbl = new Label { Left = txt.Width + 20, Text = "label" };

            var frm = new Form()
            {
                Controls = { txt, lbl }
            };

            var input = (from evt in Observable.FromEventPattern(txt, "TextChanged")
                         select ((TextBox)evt.Sender).Text)
                         .Throttle(TimeSpan.FromSeconds(1))
                         .DistinctUntilChanged();

            using (input.ObserveOn((IScheduler)lbl).Subscribe(inp => lbl.Text = inp))
            {
                Application.Run(frm);
            }
        }
    }
}
