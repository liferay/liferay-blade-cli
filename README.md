# Blade Tools

[![Build Status](https://liferay-test-01.ci.cloudbees.com/job/blade.tools/2/badge/icon)](https://liferay-test-01.ci.cloudbees.com/job/blade.tools/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gamerson/liferay-blade-tools?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Blade Tools is a set of modular developer tools for building Liferay 7.0 modules built with OSGi.

## Install 

Right now the only installable tool is the ```blade``` CLI tool that provides a few commands.  It can be installed using the following:

### Install JPM 
Install JPM (Mac, Linux)
```
$ curl http://www.jpm4j.org/install/script | sh
```

OR

Install JPM (Windows)
```
Visit the JPM4J [Windows installation](https://www.jpm4j.org/#!/md/windows) setup guide.
```

### Download and install blade CLI from CI

If you want to build the blade.jar yourself skip this and go to the next section.

Download the latest CI build of Blade CLI here.

[blade.jar](https://liferay-test-01.ci.cloudbees.com/job/blade.tools/lastSuccessfulBuild/artifact/com.liferay.blade.cli/generated/distributions/executable/blade.jar)

### Build blade cli jar

If you have already downloaded the blade.jar from above you can skip this section.

Clone this repo, and then from the command line execute following command:

```
$ ./gradlew clean build export.blade -x check
```

### Install Blade Tools jar using JPM

Install from downloaded jar from above.

```
$ (sudo) jpm install -fl <downloads_dir>/blade.jar
```

OR Install from newly built jar if you buildt from source.

```
$ (sudo) jpm install -fl com.liferay.blade.cli/generated/distributions/executable/blade.jar
```

Now you should have the ```blade``` executable in your path. Try it by running:

```
blade
```

## Usage

Once you have the blade cli installed you can see the list of commands just type
```
blade
```

Or ```java -jar blade.jar``` if not using JPM.

Current available commands

### Create

The ```Create``` command allows you to create new Liferay 7 module projects based on gradle or maven build.

```
$ blade create helloworld 
```

This will create a new helloworld portlet module project that contains an OSGi component built by a gradle script.
 To see all the options of the create command just run ```$blade create -h``` for all options.

### Deploy

First, start Liferay 7 Portal, once it is running you can build your Liferay 7 module and deploy it

```
helloworld $ gradle build
helloworld $ blade deploy build/libs/helloworld-1.0.jar
```

If you have Liferay 7 running the blade tool should connect to the Liferay Module Framework and install your newly built module jar file.  You can check the OSGi shell of Liferay to verify

```
$ telnet localhost 11311
```
Then issue the command ```lb helloworld```

### Migrate

## OSGi bundles (Eclipse plugins)

The blade libraries are also available in just pure OSGi bundle form which can be run in any OSGi application include an Eclipse runtime.  So we have generated both a R5(OSGi) repository and p2(Eclipse) repository available for third-party builds.

[Latest available CI Build - R5/p2 repository](https://liferay-test-01.ci.cloudbees.com/job/blade.tools/lastSuccessfulBuild/artifact/p2_build/generated/p2/)

## Release
Blade Tools is continuously built and released on [CloudBees](https://https://liferay-test-01.ci.cloudbees.com/job/blade.tools/).

[![Built on DEV@cloud](http://www.cloudbees.com/sites/default/files/Button-Built-on-CB-1.png)](http://www.cloudbees.com/foss/foss-dev.cb)

## License
All source to this project is available under [Apache 2.0 License](/LICENSE.txt)
