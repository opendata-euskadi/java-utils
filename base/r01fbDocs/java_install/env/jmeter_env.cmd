@REM ======================================================================
@REM == SET GENERAL ENV
@REM ======================================================================
call d:/java/env/pcienv_jdk8.cmd

@REM == ENVIRONMENT ===================================================================
set JMETER_HOME=D:/test_tools/apache-jmeter-3.3
set JMETER_BIN=%JMETER_HOME%/bin/
set JM_LAUNCH=%JAVA_HOME%/bin/javaw.exe

set JVM_ARGS="-Duser.language=en -Duser.region=EN"
