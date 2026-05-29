@echo off
echo Пробрасываю порт 8080 на эмулятор через ADB...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 reverse tcp:8080 tcp:8080
echo Готово. Теперь эмулятор может обращаться к серверу через http://127.0.0.1:8080
pause
