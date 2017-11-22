if java -version 2>&1 | grep "64-Bit"; then ARCH=64bit; ARCH2=_64; else ARCH=32bit; ARCH2=; fi


java -Djava.library.path=jni/$ARCH -cp out/jar/vis.sampleApps-0.9-SNAPSHOT.jar:lib/* de.tudresden.cib.vis.sampleApps.VisDSLRunner $1 $2
