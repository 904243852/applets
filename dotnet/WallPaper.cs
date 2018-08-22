using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Net;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;

public class WallPaper
{
    public static string ImageHrefHost = "https://cn.bing.com";

    public static string ImageHref = ImageHrefHost + "/HPImageArchive.aspx?format=xml&idx=0&n=1";

    public static string TargetDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyPictures) + @"\wallpaper\";

    public string request(string url, Encoding encoding)
    {
        HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
        request.Method = "GET";
        HttpWebResponse response = request.GetResponse() as HttpWebResponse;
        StreamReader reader = new StreamReader(response.GetResponseStream(), encoding);
        return reader.ReadToEnd();
    }

    public void Download(string url, string filepath)
    {
        FileStream fs = new FileStream(filepath, FileMode.CreateNew);

        HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
        request.Proxy = WebProxy.GetDefaultProxy();

        HttpWebResponse response = request.GetResponse() as HttpWebResponse;
        
        Stream stream = response.GetResponseStream();
        byte[] bytes = new byte[1024];
        int size = 0;
        while ((size = stream.Read(bytes, 0, (int) bytes.Length)) > 0)
        {
            fs.Write(bytes, 0, size);
        }
        
        fs.Close();
        stream.Close();

        System.Media.SystemSounds.Beep.Play();
    }

    static public void Main(string[] args)
    {
        WallPaper paper = new WallPaper();
        string content = paper.request(ImageHref, Encoding.UTF8);

        Match match = Regex.Match(content, @"<url>([^<]*)</url>");
        if (match.Success)
        {
            string url = ImageHrefHost + match.Groups[1].Value;

            string target = TargetDirectory + DateTime.Now.ToString("yyyyMMddHHmmss") + ".jpg";

            if (!Directory.Exists(target))
            {
                Directory.CreateDirectory(TargetDirectory);
            }

            paper.Download(url, target);
            
            Console.WriteLine("Download image from \"{0}\" to \"{1}\".", url, target);

            Image image = Image.FromFile(target);
            string bmppath = target.Substring(0, target.Length - 3) + "bmp";
            image.Save(bmppath, System.Drawing.Imaging.ImageFormat.Bmp);
            SystemParametersInfo(20, 0, bmppath, 0x2);
        }
    }

    [DllImport("user32.dll", EntryPoint = "SystemParametersInfo")]
    public static extern int SystemParametersInfo(int uAction, int uParam, string lpvParam, int fuWinIni);
}
