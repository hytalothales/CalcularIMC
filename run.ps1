$java = "C:\Users\hytal\.jdks\ms-21.0.12.1\bin\java.exe"
$javac = "C:\Users\hytal\.jdks\ms-21.0.12.1\bin\javac.exe"

if (-not (Test-Path $java)) {
    $java = "java"
    $javac = "javac"
}

Write-Host "Compilando o projeto IMC..." -ForegroundColor Cyan
& $javac --module-path lib --add-modules javafx.controls,javafx.fxml -d out (Get-Item src/*.java)

if ($LASTEXITCODE -eq 0) {
    Write-Host "Executando a aplicação IMC JavaFX..." -ForegroundColor Green
    & $java "-Djava.library.path=lib" --module-path lib --add-modules javafx.controls,javafx.fxml -cp out Main
} else {
    Write-Host "Erro de compilação!" -ForegroundColor Red
}
