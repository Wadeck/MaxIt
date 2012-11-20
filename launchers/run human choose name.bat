@echo off
echo Enter player name:
set /p UserName=
java -jar MaxItClientHuman.jar 127.0.0.1 8888 %UserName%