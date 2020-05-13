set THISDIR=%~dp0
set PATH=%THISDIR%\lib\soar;%PATH%
echo %THISDIR%

copy %THISDIR%\libs\soar\java\swt-win64.jar %THISDIR%\libs\soar\java\swt.jar

cd %THISDIR%
javac -classpath lib*.jar;src *.java
java -cp %THISDIR%\bin;%THISDIR%ibs\stdlib-package.jar;%THISDIR%\libs\soar\java\sml.jar;%THISDIR%\src; edu.upc.fib.masd.jav.AoE.java