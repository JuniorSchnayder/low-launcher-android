@echo off
set JAVA_HOME=C:\WS\tools\jdk-17.0.20.1+1
set ANDROID_HOME=C:\WS\tools\android-sdk
set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%PATH%

echo ==============================================
echo  Compilando Low-Launcher APK...
echo ==============================================

call "C:\WS\tools\gradle-8.9\bin\gradle.bat" assembleDebug --console=plain

if %ERRORLEVEL% equ 0 (
    copy /Y "app\build\outputs\apk\debug\app-debug.apk" "LowLauncher.apk"
    echo.
    echo ==============================================
    echo  APK GERADO COM SUCESSO!
    echo  Arquivo: LowLauncher.apk
    echo ==============================================
) else (
    echo.
    echo [ERRO] Falha ao compilar o APK.
)
pause
