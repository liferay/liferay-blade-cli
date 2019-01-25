@ECHO OFF
CALL gradlew.bat clean
CALL gradlew.bat -Psnapshots :extensions:maven-profile:publishToMavenLocal
CALL gradlew.bat -Psnapshots -continue check smokeTests --scan