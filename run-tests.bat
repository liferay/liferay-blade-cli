@ECHO OFF
CALL gradlew.bat --no-daemon clean
CALL gradlew.bat --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources
CALL gradlew.bat --no-daemon :extensions:project-templates-js-theme:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources
CALL gradlew.bat --no-daemon :extensions:project-templates-js-widget:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources
CALL gradlew.bat --no-daemon -PmavenLocal --continue clean check smokeTests --scan -x :extensions:maven-profile:test