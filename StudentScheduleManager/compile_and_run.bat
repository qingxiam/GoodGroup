@echo off
REM 设置变量
set SRC=src\main\java
set BIN=bin
set LIB=lib\sqlite-jdbc-3.36.0.3.jar

REM 编译所有java文件
echo 正在编译...
javac -encoding utf-8 -cp "%LIB%" -d %BIN% %SRC%\com\schedule\*.java %SRC%\com\schedule\model\*.java %SRC%\com\schedule\util\*.java %SRC%\com\schedule\dao\*.java %SRC%\com\schedule\controller\*.java %SRC%\com\schedule\view\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo 编译失败!
    pause
    exit /b %ERRORLEVEL%
)

REM 运行主程序
echo 编译成功，正在运行...
cd %BIN%
java --enable-native-access=ALL-UNNAMED -cp ".;..\%LIB%" com.schedule.Main
cd ..
pause
