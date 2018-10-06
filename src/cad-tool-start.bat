@echo off
cd /d %~dp0
set CAD_CLASSPATH=.;lib\cad-tool-bean.jar;lib\cad-tool-client.jar;lib\commons-io-2.6.jar;lib\commons-logging-1.2.jar;lib\commons-net-3.6.jar;lib\commons-priv-1.0.1.jar;lib\jackson-annotations-2.9.6.jar;lib\jackson-core-2.9.6.jar;lib\jackson-databind-2.9.6.jar;lib\jackson-dataformat-xml-2.9.6.jar;lib\jackson-module-jaxb-annotations-2.9.6.jar;lib\log4j-1.2.17.jar;lib\stax2-api-4.1.jar;lib\wnc-client-11.0.3.0.jar
@start /MIN javaw -classpath %CAD_CLASSPATH% com.bplead.cad.Main "cad"