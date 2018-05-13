@REM ======================================================================
@REM == SET GENERAL ENV
@REM ======================================================================
call d:/java/env/pcienv.cmd

echo ...setting jdk env

@REM ======================================================================
@REM == JVMS
@REM ======================================================================
set JAVA_INSTALL_HOME=d:/java
set SUNJDK_HOME=%JAVA_INSTALL_HOME%/jdk7
set SUNJDK_VENDOR=Sun
set ORACLEJDK_VENDOR=Oracle

@REM == JDK ================================================================
set JAVA_HOME=%SUNJDK_HOME%
set JRE_HOME=%SUNJDK_HOME%/jre
set JAVA_VENDOR=%ORACLEJDK_VENDOR%
set PATH=%PATH%;%JAVA_HOME%/bin


@REM ======================================================================
@REM == VIRTUAL MACHINE OPTIONS
@REM ======================================================================
@REM optimizacion ver http://middlewaremagic.com/weblogic/?page_id=1096
set OTHER_VMOPTIONS=-Dsun.lang.ClassLoader.allowArraySyntax=true -Duser.language=en -Duser.country=US


@REM ======================================================================
@REM == MEMORY
@REM ======================================================================
set USER_MEM_ARGS=-Xms256m -Xmx512m -XX:PermSize=128m  -XX:MaxPermSize=256m 





