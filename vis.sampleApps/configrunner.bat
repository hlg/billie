@echo off

java -version 2>&1 | find /i "64-Bit" > NUL && (set ARCH=64bit & set ARCH2=_64) || (set ARCH=32bit & set ARCH2=)


java -Djava.library.path=jni/%ARCH% -cp out/jar/vis.sampleApps-0.9-SNAPSHOT.jar;lib/antlr-2.7.7.jar;lib/antlr-3.1.1.jar;lib/antlr-runtime-3.1.1.jar;lib/bimserver-buildingSMARTLibrary-1.1.0.jar;lib/bimserver-emf-1.1.0.jar;lib/bimserver-ifc-1.1.0.jar;lib/bimserver-ifcengine-1.1.0.jar;lib/bimserver-ifcplugins-1.1.0.jar;lib/bimserver-plugins-1.1.0.jar;lib/bimserver-utils-1.1.0.jar;lib/cib.lib.gaeb.model-1.0.0.201303251840.jar;lib/cib.mf.qto.model-1.0.0.201303251840.jar;lib/cib.mf.risk.model-1.0.0.201303251840.jar;lib/cib.mf.schedule.model-1.1.0.201303251840.jar;lib/cib.mmaa.language.mmql.transfer-1.0.0.201303251840.jar;lib/com.ibm.icu-4.4.2.v20110208.jar;lib/commons-codec-1.3.jar;lib/commons-io-2.1.jar;lib/commons-lang-2.4.jar;lib/de.mefisto.container-1.0.0.201303251840.jar;lib/geronimo-activation_1.1_spec-1.1.jar;lib/geronimo-javamail_1.4_spec-1.7.1.jar;lib/geronimo-osgi-locator-1.0.jar;lib/geronimo-osgi-registry-1.0.jar;lib/guava-11.0.1.jar;lib/guava-bootstrap-11.0.1.jar;lib/hamcrest-core-1.1.jar;lib/ifcNamespaces-1.0.jar;lib/j3dcore-1.5.2.jar;lib/j3dutils-1.5.2.jar;lib/java-getopt-1.0.13.jar;lib/javax.xml-1.3.4.v201005080400.jar;lib/jna-3.2.5.jar;lib/joda-convert-1.2.jar;lib/joda-time-2.1.jar;lib/jsr305-1.3.9.jar;lib/junit-4.10.jar;lib/log4j-1.2.16.jar;lib/org.eclipse.ant.core-3.2.300.v20110511.jar;lib/org.eclipse.core.contenttype-3.4.100.v20110423-0524.jar;lib/org.eclipse.core.expressions-3.4.300.v20110228.jar;lib/org.eclipse.core.filesystem-1.3.100.v20110423-0524.jar;lib/org.eclipse.core.jobs-3.5.100.v20110404.jar;lib/org.eclipse.core.resources-3.7.100.v20110510-0712.jar;lib/org.eclipse.core.runtime-3.7.0.v20110110.jar;lib/org.eclipse.core.variables-3.2.500.v20110511.jar;lib/org.eclipse.draw2d-3.7.0.v20110425-2050.jar;lib/org.eclipse.emf.common-2.7.0.v20110605-0747.jar;lib/org.eclipse.emf.ecore-2.7.0.v20110605-0747.jar;lib/org.eclipse.emf.ecore.xmi-2.7.0.v20110520-1406.jar;lib/org.eclipse.emf.edit-2.7.0.v20110606-0949.jar;lib/org.eclipse.equinox.app-1.3.100.v20110321.jar;lib/org.eclipse.equinox.common-3.6.0.v20110523.jar;lib/org.eclipse.equinox.preferences-3.4.0.v20110502.jar;lib/org.eclipse.equinox.registry-3.5.100.v20110502.jar;lib/org.eclipse.osgi-3.7.0.v20110613.jar;lib/org.eclipse.swt-3.7.0.v3735b.jar;lib/org.eclipse.swt.win32.win32.x86%ARCH2%-3.7.0.v3735b.jar;lib/org.osgi.compendium-4.2.0.jar;lib/org.osgi.core-4.2.0.jar;lib/slf4j-api-1.6.2.jar;lib/slf4j-log4j12-1.6.2.jar;lib/stax-api-1.0.1.jar;lib/stringtemplate-3.2.jar;lib/vecmath-1.5.2.jar;lib/vis-0.9.jar;lib/vis.configurations-0.9.jar;lib/vis.data.bimserver-0.9.jar;lib/vis.data.mmqlserver-0.9.jar;lib/vis.data.multimodel-0.9.jar;lib/vis.runtime.draw2d-0.9.jar;lib/vis.runtime.java3d-0.9.jar;lib/vis.scene.draw2d-0.9.jar;lib/vis.scene.java2d-0.9.jar;lib/vis.scene.java3d-0.9.jar;lib/vis.scene.text-0.9.jar;lib/xmlbeans-2.4.0.jar -Xmx1500m de.tudresden.cib.vis.sampleApps.ConfigurationRunner %1 %2
