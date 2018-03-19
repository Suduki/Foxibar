@echo off
cd /d "%~dp0"

call mvn verify %*

pause