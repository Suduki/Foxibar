@echo off
cd /d "%~dp0"

call java -jar target\Foxibar-jar-with-dependencies.jar

if errorlevel 1 goto error

goto end

:error
pause

:end