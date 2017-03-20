@ECHO OFF
"C:\Windows\Microsoft.NET\Framework\v4.0.30319\MSBuild.exe" "%~dp0SimplePendant.csproj" /t:Rebuild /p:Configuration=Release /p:WarningLevel=0
PAUSE