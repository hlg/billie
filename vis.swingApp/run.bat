@ECHO OFF

IF EXIST "%JDK_HOME%" SET JDK=%JDK_HOME%
IF NOT "%JDK%" == "" GOTO jdk
IF EXIST "%JAVA_HOME%" SET JDK=%JAVA_HOME%
IF "%JDK%" == "" GOTO error

:jdk
SET JAVA_EXE=%JDK%\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" SET JAVA_EXE=%JDK%\jre\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" GOTO error

SET JRE=%JDK%
IF EXIST "%JRE%\jre" SET JRE=%JDK%\jre
SET BITS=32
IF EXIST "%JRE%\lib\amd64" SET BITS=64

ECHO running with Java %BITS% bit version

java -cp jar\antlr-2.7.7.jar;jar\asm-4.0.jar;jar\bimserver-buildingSMARTLibrary-1.1.0.jar;jar\bimserver-emf-1.1.0.jar;jar\bimserver-ifc-1.1.0.jar;jar\bimserver-ifcengine-1.1.0.jar;jar\bimserver-ifcplugins-1.1.0.jar;jar\bimserver-plugins-1.1.0.jar;jar\bimserver-utils-1.1.0.jar;jar\cib.lib.gaeb.model-1.0.0.201209071948.jar;jar\cib.mf.qto.model-1.0.0.201209071948.jar;jar\cib.mf.risk.model-1.0.0.201209071948.jar;jar\cib.mf.schedule.model-1.1.0.201209071948.jar;jar\commons-codec-1.3.jar;jar\commons-io-2.1.jar;jar\commons-lang-2.4.jar;jar\de.mefisto.container-1.0.0.201209071948.jar;jar\geronimo-javamail_1.4_spec-1.7.1.jar;jar\geronimo-osgi-registry-1.0.jar;jar\groovy-2.0.4.jar;jar\groovy-swing-2.0.4.jar;jar\guava-11.0.1.jar;jar\ifcNamespaces-1.0.jar;jar\java-getopt-1.0.13.jar;jar\javax.xml-1.3.4.v201005080400.jar;jar\jna-3.2.5.jar;jar\joda-convert-1.2.jar;jar\joda-time-2.1.jar;jar\log4j-1.2.16.jar;jar\org.eclipse.core.contenttype-3.4.100.v20110423-0524.jar;jar\org.eclipse.core.expressions-3.4.300.v20110228.jar;jar\org.eclipse.core.filesystem-1.3.100.v20110423-0524.jar;jar\org.eclipse.core.jobs-3.5.100.v20110404.jar;jar\org.eclipse.core.resources-3.7.100.v20110510-0712.jar;jar\org.eclipse.core.runtime-3.7.0.v20110110.jar;jar\org.eclipse.emf.common-2.7.0.v20110605-0747.jar;jar\org.eclipse.emf.ecore-2.7.0.v20110605-0747.jar;jar\org.eclipse.emf.ecore.xmi-2.7.0.v20110520-1406.jar;jar\org.eclipse.emf.edit-2.7.0.v20110606-0949.jar;jar\org.eclipse.equinox.app-1.3.100.v20110321.jar;jar\org.eclipse.equinox.common-3.6.0.v20110523.jar;jar\org.eclipse.equinox.preferences-3.4.0.v20110502.jar;jar\org.eclipse.equinox.registry-3.5.100.v20110502.jar;jar\org.eclipse.osgi-3.7.0.v20110613.jar;jar\org.osgi.compendium-4.2.0.jar;jar\slf4j-api-1.6.2.jar;jar\slf4j-log4j12-1.6.2.jar;jar\vis-0.4.jar;jar\vis.configurations-0.4.jar;jar\vis.data.bimserver-0.4.jar;jar\vis.data.multimodel-0.4.jar;jar\vis.runtime.java3d-0.4.jar;jar\vis.scene.java2d-0.4.jar;jar\vis.scene.java3d-0.4.jar;jar\vis.swingApp-0.4-SNAPSHOT.jar;jar\xmlbeans-2.4.0.jar -Xmx1500m -Djava.library.path=native_%BITS% de.tudresden.cib.vis.swingApp.MefistoDemo

GOTO end

:error
ECHO ERROR: cannot start IntelliJ IDEA.
ECHO No JDK found. Please validate either IDEA_JDK, JDK_HOME or JAVA_HOME points to valid JDK installation.
ECHO
PAUSE

:end

