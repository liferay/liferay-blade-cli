# Blade CLI

[![Build Status](https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/badge/icon)](https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/)

## Install 

In order to use the blade cli, you must install it with the following:

### Install Blade (Mac, Linux)
```
$ curl https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/installers/global | sudo sh
```

OR

Install JPM (Windows) then Install blade
```
Visit the JPM4J [Windows installation](https://www.jpm4j.org/#!/md/windows) setup guide.
```
```
$ jpm install -f https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/com.liferay.blade.cli/generated/com.liferay.blade.cli.jar
```

### How to build blade cli jar from sources

Clone this repo, and then from the command line execute following command:

```
$ ./gradlew clean build -x check
```

### Install Blade cli jar using JPM

Install from downloaded jar from above.

```
$ (sudo) jpm install -fl <downloads_dir>/com.liferay.blade.cli.jar
```

OR Install from newly built jar if you buildt from source.

```
$ (sudo) jpm install -fl com.liferay.blade.cli/generated/com.liferay.blade.cli.jar
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

Or ```java -jar com.liferay.blade.cli.jar``` if not using JPM.

Current available commands

### Create

The ```Create``` command allows you to create new Liferay 7 module projects based on gradle.

```
$ blade create -t mvcportlet helloworld 
```

This will create a new helloworld portlet module project that contains an OSGi component built by a gradle script.
 To see all the options of the create command just run ```$blade create -h``` for all options.

### Deploy

First, start Liferay 7 Portal, once it is running you can build your Liferay 7 module and deploy it

```
helloworld $ blade deploy build/libs/helloworld-1.0.jar
```

### Shell

Liferay 7 has a built-in gogo shell that can be accessed with telnet client on port 11311.  However, many times you just wish
to be able to run a gogo command remotely from the cmdline and return the results directly to the console.  Use the ```blade sh``` to do just that.

```
$ blade sh <gogo-command>
```

Some examples:

List all bundles running in the framework.
```
$ blade sh lb
```

Search for all services that provide javax.portlet.Portlet
```
$ blade sh services | grep javax.portlet.Portlet
```

## Release
Blade Tools is continuously built and released on [CloudBees](https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/).

[![Built on DEV@cloud](http://www.cloudbees.com/sites/default/files/Button-Built-on-CB-1.png)](http://www.cloudbees.com/foss/foss-dev.cb)

## License
All source to this project is available under [Apache 2.0 License](/LICENSE.txt)
