@ECHO OFF
CALL rmdir /s/q "%userprofile%\.m2\repository\com\liferay\blade
CALL gradlew.bat --no-daemon clean
CALL gradlew.bat --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources
CALL gradlew.bat --no-daemon -PmavenLocal --continue clean check smokeTests --scan