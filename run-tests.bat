@ECHO OFF
CALL rmdir /s/q "%userprofile%\.m2\repository\com\liferay\blade\com.liferay.blade.extensions.maven.profile"
CALL gradlew.bat clean
CALL gradlew.bat :extensions:maven-profile:publishToMavenLocal
CALL gradlew.bat -PmavenLocal -continue check smokeTests --scan