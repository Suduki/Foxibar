@echo off
cd /d "%~dp0"

call mvn clean

if errorlevel 1 goto error

goto end

:error
pause

:end