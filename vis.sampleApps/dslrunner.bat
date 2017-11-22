@echo off

java -version 2>&1 | find /i "64-Bit" > NUL && (set ARCH=64bit & set ARCH2=_64) || (set ARCH=32bit & set ARCH2=)

java -Djava.library.path=jni/%ARCH% -cp out/jar/vis.sampleApps-0.9-SNAPSHOT.jar;lib/* de.tudresden.cib.vis.sampleApps.VisDSLRunner %1 %2
