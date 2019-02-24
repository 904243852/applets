.PHONY: all run

platform = $(shell uname | cut -c 1-5)

# WALLPAPER_HOME := $(shell cd ~ && pwd)/Pictures/wallpaper
WALLPAPER_HOME := /g/Pictures/wallpaper

# ifneq ($(platform), MINGW)
# endif

all:
	@/c/Windows/Microsoft.NET/Framework/v4.0.30319/csc.exe SetDesktop.cs
	@javac ImageDownloader.java -encoding utf8

clean:
	@rm -f *.class *.exe
	@rm -f $(WALLPAPER_HOME)/ImageDownloader.class $(WALLPAPER_HOME)/SetDesktop.exe $(WALLPAPER_HOME)/run.bat

test: all	
	@java ImageDownloader

copy-file: all
	@mkdir -p $(WALLPAPER_HOME) && cp *.exe $(WALLPAPER_HOME) && cp *.class $(WALLPAPER_HOME) && cp run.bat $(WALLPAPER_HOME)

run: copy-file
	@$(WALLPAPER_HOME)/run.bat

install: copy-file
	@schtasks /create /tn "AutomaticWallpaper" /tr java $(WALLPAPER_HOME)/run.bat /sc daily /st 13:00:00

uninstall: clean
	@schtasks /delete /tn "AutomaticWallpaper" /f