using System;
using System.Configuration;
using System.Drawing;
using System.Windows.Forms;

namespace SimplePendant
{
    public partial class Pendant : Form
    {
        private int startX;
        private int startY;
        private bool moveable;

        public Pendant()
        {
            InitializeComponent();

            Configuration config = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
            string backgroundImage = config.AppSettings.Settings["url"].Value;
            float scale = float.Parse(config.AppSettings.Settings["scale"].Value);
            string[] aligns = config.AppSettings.Settings["align"].Value.Split(',');
            double opacity = double.Parse(config.AppSettings.Settings["opacity"].Value);
            moveable = bool.Parse(config.AppSettings.Settings["moveable"].Value);

            SuspendLayout();
            Image source = Image.FromFile(backgroundImage);
            BackgroundImage = source.GetThumbnailImage((int)(source.Width * scale), (int)(source.Height * scale), () => { return false; }, IntPtr.Zero);
            ClientSize = new Size(BackgroundImage.Width, BackgroundImage.Height);
            Point location = new Point(0, 0);
            foreach (string align in aligns)
            {
                if ("top" == align)
                {
                    location.Y = 0;
                }
                if ("right" == align)
                {
                    location.X = Screen.PrimaryScreen.WorkingArea.Width - Size.Width;
                }
                if ("buttom" == align)
                {
                    location.Y = Screen.PrimaryScreen.WorkingArea.Width - Size.Height;
                }
                if ("left" == align)
                {
                    location.X = 0;
                }
            }
            Location = location;
            Opacity = opacity;
            ResumeLayout(false);
        }

        private void Pendant_MouseDown(object sender, MouseEventArgs e)
        {
            if (moveable && e.Button == MouseButtons.Left)
            {
                startX = e.X;
                startY = e.Y;
            }
        }

        private void Pendant_MouseMove(object sender, MouseEventArgs e)
        {
            if (moveable && e.Button == MouseButtons.Left)
            {
                this.Left += e.X - startX;
                this.Top += e.Y - startY;
            }
        }

        private void Pendant_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == (Keys.Q & Keys.ControlKey))
                this.Close();
        }
    }
}
