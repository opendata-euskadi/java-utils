echo ...setting jpa env
@REM ======================================================================
@REM == JPA
@REM ======================================================================
set ECLIPSELINK_PATH=%LIBS_HOME%/org.eclipse.persistence/org.eclipse.persistence.jpa/jars/org.eclipse.persistence.jpa-2.6.0.jar
set JPAWEAVINGAGENT_VMOPTIONS=-javaagent:%ECLIPSELINK_PATH%
set JPADEBUG_VMOPTIONS=-Dweblogic.debug.DebugJTA2PC=true -Dweblogic.debug.DebugJTAXA=true

set JPA_VMOPTIONS = %JPAWEAVINGAGENT_VMOPTIONS% %JPADEBUG_VMOPTIONS%

@REM activar jpa2.0 en WL 10.3.5 (http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/weblogic#20110515:_JPA_2.0_using_EclipseLink_on_WebLogic_10.3.5.0)
set JPA_PRECLASSPATH=%JPA_PRECLASSPATH%;%MW_HOME%/modules/com.oracle.jpa2support_1.0.0.0_2-1.jar;%MW_HOME%/modules/javax.persistence_1.1.0.0_2-0.jar

