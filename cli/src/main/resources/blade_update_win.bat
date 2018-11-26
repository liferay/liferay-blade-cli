@echo off
jpm install -f "%s"
if NOT ["%errorlevel%"]==["0"] (
	echo Error encountered, please run the following command manually
	echo jpm install -f %s
	(goto) 2>nul & del "%~f0"
    pause
    exit
) else (
	(goto) 2>nul & del "%~f0"
	exit
)