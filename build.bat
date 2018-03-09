@echo off
cd /d "%~dp0"

call mvn package

if errorlevel 1 goto error

goto end

:error
pause

:end