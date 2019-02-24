using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Net;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;

public class SetDesktop
{
    static public void Main(string[] args)
    {
        if (args.Length > 0)
        {
            SystemParametersInfo(20, 0, args[0], 0x2);
        }
    }

    [DllImport("user32.dll", EntryPoint = "SystemParametersInfo")]
    public static extern int SystemParametersInfo(int uAction, int uParam, string lpvParam, int fuWinIni);
}
