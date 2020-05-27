set THISDIR=%~dp0
set PATH=%THISDIR%\libs\soar;%PATH%

cd %THISDIR%
java -jar out\artifacts\MAI_MASD_jar\MAI-MASD.jar

PAUSE