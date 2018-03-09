@echo off
cd /d "%~dp0"

call clean.bat
if ERRORLEVEL 1 goto end

call build.bat
if ERRORLEVEL 1 goto end

call run.bat
if ERRORLEVEL 1 goto end

:end