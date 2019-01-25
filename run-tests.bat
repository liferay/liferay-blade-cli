gradlew.bat clean
gradlew.bat -Psnapshots :extensions:maven-profile:publishToMavenLocal
gradlew.bat -Psnapshots -continue check smokeTests