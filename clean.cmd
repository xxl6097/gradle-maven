@echo off
setlocal enabledelayedexpansion
echo ----------------------------------------
echo 批量删除当前目录下的所有缓存文件
echo ----------------------------------------

for /f "delims=" %%i in ('dir /s/b/a-d *.iml,local.properties') do (
  if exist %%i (  
  del /q /s %%i
  )
	
)

for /f "delims=" %%i in ('dir /s/b/ad build') do (
    echo 删除 %%i
    rd /s/q "%%~i"
)

rd  /s /q .idea
rd  /s /q .gradle
echo ----------------------------------------
echo 完成
 
pause