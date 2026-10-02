@echo off
set JAVA_EXE="C:\Users\hytal\.jdks\ms-21.0.12.1\bin\java.exe"
set JAVAC_EXE="C:\Users\hytal\.jdks\ms-21.0.12.1\bin\javac.exe"

if not exist %JAVA_EXE% (
    set JAVA_EXE=java
    set JAVAC_EXE=javac
)

echo Compilando o projeto IMC...
%JAVAC_EXE% --module-path lib --add-modules javafx.controls,javafx.fxml -d out src\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao!
    pause
    exit /b %ERRORLEVEL%
)

echo Executando a aplicacao IMC JavaFX...
%JAVA_EXE% "-Djava.library.path=lib" --module-path lib --add-modules javafx.controls,javafx.fxml -cp out Main
