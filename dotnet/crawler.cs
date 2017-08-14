using System;
using System.IO;
using System.Net;
using System.Text;

namespace crawler
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var requestUrl = @"http://www.baidu.com";

            // var proxyUrl = @"http://proxy.server.com:8080/";
            // var proxyUsername = "username",
            //     proxyPassword = "password";

            var request = WebRequest.Create(requestUrl) as HttpWebRequest;

            // var proxy = new WebProxy(proxyUrl);
            // proxy.Credentials = new NetworkCredential(proxyUsername, proxyPassword);
            // request.Proxy = proxy;

            using (var response = request.GetResponse())
            {
                using (var reader = new StreamReader(response.GetResponseStream()))
                {
                    var content = reader.ReadToEnd();
                    Console.WriteLine(content);
                }
            }
        }
    }
}