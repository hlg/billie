OS=$(getconf LONG_BIT)bit

java -Djava.library.path=jni/$OS -cp lib/ant-1.8.4.jar:lib/ant-antlr-1.8.4.jar:lib/ant-junit-1.8.4.jar:lib/ant-launcher-1.8.4.jar:lib/antlr-2.7.7.jar:lib/antlr-3.1.1.jar:lib/antlr-runtime-3.1.1.jar:lib/aopalliance-1.0.jar:lib/backport-util-concurrent-3.1.jar:lib/bimfit-1.5.2.jar:lib/bimserver-buildingSMARTLibrary-1.1.0.jar:lib/bimserver-emf-1.1.0.jar:lib/bimserver-ifc-1.1.0.jar:lib/bimserver-ifcengine-1.1.0.jar:lib/bimserver-ifcplugins-1.1.0.jar:lib/bimserver-plugins-1.1.0.jar:lib/bimserver-utils-1.1.0.jar:lib/bsf-2.4.0.jar:lib/cib.lib.gaeb.model-1.0.0.201207261034.jar:lib/cib.mf.qto.model-1.0.0.201207261034.jar:lib/cib.mf.risk.model-1.0.0.201209071948.jar:lib/cib.mf.schedule.model-1.1.0.201209071948.jar:lib/cib.mm.multimodel-1.0.0.201403121638.jar:lib/cib.mmaa.language.mmql.transfer-1.0.0.201303251840.jar:lib/com.ibm.icu-4.4.2.v20110208.jar:lib/commons-codec-1.5.jar:lib/commons-io-2.1.jar:lib/commons-lang-2.6.jar:lib/commons-logging-1.1.1.jar:lib/de.mefisto.container-1.0.0.201207261034.jar:lib/geronimo-activation_1.1_spec-1.1.jar:lib/geronimo-javamail_1.4_spec-1.7.1.jar:lib/geronimo-osgi-locator-1.0.jar:lib/geronimo-osgi-registry-1.0.jar:lib/gpars-1.0-beta-3.jar:lib/groovy-all-2.0.4.jar:lib/guava-11.0.1.jar:lib/guava-bootstrap-11.0.1.jar:lib/guice-2.0.jar:lib/hamcrest-core-1.1.jar:lib/ical4j-1.0.4.jar:lib/ifcNamespaces-1.0.jar:lib/ivy-2.2.0.jar:lib/j3dcore-1.5.2.jar:lib/j3dutils-1.5.2.jar:lib/jansi-1.6.jar:lib/java-getopt-1.0.13.jar:lib/javax.xml-1.3.4.v201005080400.jar:lib/jcommander-1.12.jar:lib/jline-1.0.jar:lib/jna-3.2.5.jar:lib/joda-convert-1.2.jar:lib/joda-time-2.1.jar:lib/jsp-api-2.0.jar:lib/jsr166y-1.7.0.jar:lib/jsr305-1.3.9.jar:lib/junit-4.10.jar:lib/log4j-1.2.16.jar:lib/org.eclipse.ant.core-3.2.300.v20110511.jar:lib/org.eclipse.core.contenttype-3.4.100.v20110423-0524.jar:lib/org.eclipse.core.expressions-3.4.300.v20110228.jar:lib/org.eclipse.core.filesystem-1.3.100.v20110423-0524.jar:lib/org.eclipse.core.jobs-3.5.100.v20110404.jar:lib/org.eclipse.core.resources-3.7.100.v20110510-0712.jar:lib/org.eclipse.core.runtime-3.7.0.v20110110.jar:lib/org.eclipse.core.variables-3.2.500.v20110511.jar:lib/org.eclipse.draw2d-3.7.0.v20110425-2050.jar:lib/org.eclipse.emf.common-2.7.0.v20110605-0747.jar:lib/org.eclipse.emf.ecore-2.7.0.v20110605-0747.jar:lib/org.eclipse.emf.ecore.edit-2.7.0.v20110606-0949.jar:lib/org.eclipse.emf.ecore.xmi-2.7.0.v20110520-1406.jar:lib/org.eclipse.emf.edit-2.7.0.v20110606-0949.jar:lib/org.eclipse.emf.validation-1.4.0.v20100428-2315.jar:lib/org.eclipse.equinox.app-1.3.100.v20110321.jar:lib/org.eclipse.equinox.common-3.6.0.v20110523.jar:lib/org.eclipse.equinox.preferences-3.4.0.v20110502.jar:lib/org.eclipse.equinox.registry-3.5.100.v20110502.jar:lib/org.eclipse.osgi-3.7.0.v20110613.jar:lib/org.eclipse.swt-3.7.0.v3735b.jar:lib/org.eclipse.swt.gtk.linux.x86-3.7.0.v3735b.jar:lib/org.eclipse.swt.win32.win32.x86-3.7.0.v3735b.jar:lib/org.osgi.compendium-4.2.0.jar:lib/org.osgi.core-4.2.0.jar:lib/qdox-1.12.jar:lib/servlet-api-2.4.jar:lib/slf4j-api-1.6.2.jar:lib/slf4j-log4j12-1.6.2.jar:lib/stax-api-1.0.1.jar:lib/stringtemplate-3.2.jar:lib/testng-6.5.2.jar:lib/vecmath-1.5.2.jar:lib/vis-0.8.jar:lib/vis.configurations-0.8.jar:lib/vis.data.bimserver-0.8.jar:lib/vis.data.jsdai-0.8.jar:lib/vis.data.mmqlserver-0.8.jar:lib/vis.data.multimodel-0.8.jar:lib/vis.runtime.draw2d-0.8.jar:lib/vis.runtime.java3d-0.8.jar:lib/vis.sampleApps-0.8.jar:lib/vis.scene.draw2d-0.8.jar:lib/vis.scene.java2d-0.8.jar:lib/vis.scene.java3d-0.8.jar:lib/vis.scene.text-0.8.jar:lib/xmlbeans-2.4.0.jar:lib/xmlpull-1.1.3.1.jar:lib/xstream-1.4.2.jar:out/jar/vis.DSL-0.8-SNAPSHOT.jar de.tudresden.cib.vis.DSL.VisDSLRunner $1 $2
