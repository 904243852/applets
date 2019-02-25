.PHONY: all run

platform = $(shell uname | cut -c 1-5)

# WALLPAPER_HOME := $(shell cd ~ && pwd)/Pictures/wallpaper
WALLPAPER_HOME_RAW := G:\Pictures\wallpaper
WALLPAPER_HOME := /g/Pictures/wallpaper

# ifneq ($(platform), MINGW)
# endif

all:
	@/c/Windows/Microsoft.NET/Framework/v4.0.30319/csc.exe SetDesktop.cs
	@javac SetDesktop.java -encoding utf8

clean:
	@rm -f *.class *.exe
	@rm -f $(WALLPAPER_HOME)/SetDesktop.class $(WALLPAPER_HOME)/SetDesktop.exe $(WALLPAPER_HOME)/SetDesktop.bat

test: all	
	@java SetDesktop

copy-file: all
	@mkdir -p $(WALLPAPER_HOME) && cp *.exe $(WALLPAPER_HOME) && cp *.class $(WALLPAPER_HOME) && cp SetDesktop.bat $(WALLPAPER_HOME)

run: copy-file
	@$(WALLPAPER_HOME)/SetDesktop.bat

install: copy-file
	@schtasks /create /tn "AutomaticWallpaper" /tr "$(WALLPAPER_HOME_RAW)/SetDesktop.bat" /sc daily /st 13:00:00 /v1

uninstall: clean
	@schtasks /delete /tn "AutomaticWallpaper" /f