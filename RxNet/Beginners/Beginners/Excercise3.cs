using System;
using System.Reactive.Linq;
using System.Reactive.Disposables;
using System.Windows.Forms;

namespace Beginners
{
    class Excercise3
    {
        public static void Test1()
        {
            var frm = new Form();
            Application.Run(frm);
        }

        public static void Test2()
        {
            var lbl = new Label();
            var frm = new Form()
            {
                Controls = {lbl}
            };

            frm.MouseMove += (sender, args) =>
            {
                lbl.Text = args.Location.ToString();
            };

            Application.Run(frm);
        }

        public static void Test3()
        {
            var lbl = new Label();
            var frm = new Form()
            {
                Controls = {lbl}
            };

            var moves = Observable.FromEventPattern<MouseEventArgs>(frm, "MouseMove");
            using (moves.Subscribe(evt =>
            {
                lbl.Text = evt.EventArgs.Location.ToString();
            }))
            {
                Application.Run(frm);
            }
        }

        public static void Test4()
        {
            var lbl = new Label();
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = {lbl}
            };

            frm.Controls.Add(txt);

            Application.Run(frm);
        }

        public static void Test5()
        {
            var txt = new TextBox();

            var frm = new Form()
            {
                Controls = {txt}
            };

            var moves = Observable.FromEventPattern<MouseEventArgs>(frm, "MouseMove");
            var input = Observable.FromEventPattern(txt, "TextChanged");

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
    }
}
