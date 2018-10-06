# Blade Extensions

There are several ways that you can extend `blade` CLI.  Most simply you can add custom `commands` and `templates`.
But likely if you want to customize a developer's experience with `blade`  to enable a fully-customized liferay workspace
workflow, tailored for your requirements, you want to create custom commands and templates that all are associated
with a `blade` _profile_.

When blade cli starts it will look in the user's home directory ${user.home}/.blade/extensions folder for any jar files.
All jar files will be searched to see if they contain valid blade extensions detailed below.

## Installing new extensions

If you would like to install an existing blade extension you can use a new command. `blade extensions install` to install from one of the following:

1. Local jar file
2. Remote jar file indicated by a URL
3. Github repository that contains an extension.

**Examples of how to install extensions**

```
$ blade extension install /path/to/my_extension.jar
```

```
$ blade extension install https://repository.lifera.com/public/group/com/liferay/blade/com.liferay.blade.extension/1.0.0/com.liferay.blade.extension-1.0.0.jar
```

```
$ blade extension install https://github.com/gamerson/blade-sample-command
```

## Developing new extensions

There are a few use-cases that blade extenders probably want to consider.  If they simply want a new `command` that they
can add to blade's standard set of commands that would apply globally to all types of workspaces, they can simply
add a new custom command.  See Custom Commands section for details.

Most likely you will have a set of custom commands that only apply to a specific workspace environment.  So lets say
that you wanted to build some custom commands that only work when the Liferay workspace is configured to use `docker`
, `wedeploy`, or `openshift`  Then likely what you want is a `profile` that supports the development workflow in that
environment.  See the section on Profiles down below for more details.

### Custom commands

To create a custom command you need to build a Command class using the JCommander framework.  Then you register your
new command with blade by using standard JRE service loader mechanisms.  Blade looks for custom commands using
the this service interfaces: `com.liferay.blade.cli.command.BaseCommand`

Therefore in your extension jar you should have a file like this: `META-INF/services/com.liferay.blade.cli.command.BaseCommand1` and inside would be a list of all of your FQDN classes of your custom commands.

An example of how this should be laid out can be found in the blade repository.

https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-command

Pay close attention to a few things in this project

1. Registering your command with blade: https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-command/src/main/resources/META-INF/services/com.liferay.blade.cli.command.BaseCommand
2. You need to build your extension against the Blade CLI API (which is not yet officially published, but you can use the provionsal repo as shown here)
https://github.com/gamerson/blade-sample-command/blob/master/build.gradle#L18
3. You can author your tests for your custom commands as well following this example: https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-command/src/test/java/com/liferay/extensions/sample/command/CommandsTest.java

Once you have your command, build it with gradle `gradle build` and then you should have your jar file.  Now you can test
it in blade by running `$ blade extension install /path/to/your/blade-command/build/libs/blade-command.jar` or by simply copying
the file to ${user.home}/.blade/extensions

**Give it a try!**
```
$ blade help
```
Now you should see your new custom command listed!.  If not, contact us on our #blade forum on the Liferay community slack channel.  https://community.liferay.com/it/chat
blade channel is here: https://liferay-community.slack.com/messages/C5US8D29Y

### Custom project templates

Blade comes with over 37+ project templates (whew!) right now, but many times you may feel that those are too simple or
don't fit the right need for your development team.  You can create new custom project templates that fit exactly in
your own team's workflow and have blade use it instead.  To do that simply follow the example receipt here for how to
build your own custom project template.

https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-template

Here are a few things to notice from this sample.

1. This template is structured as a Maven archetype.  Read more about Maven Archetypes and their features and capabilities here: https://maven.apache.org/guides/introduction/introduction-to-archetypes.html
2. The BundleSymbolic name of your template jar (see here: https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-template/bnd.bnd#L3) must have the pattern `*.project.templates.<name>.*`  So lets say you have a custom template called "mywebapp", then the BSN should be `org.myorg.project.templates.mywebapp-1.0.0.jar`
3. Testing your custom template can be accomplished by following the example here: https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-template/src/test/java/com/liferay/project/templates/sample/TemplatesTest.java

Once you have your custom template you can install it by just copying to ${user.home}/.blade/extensions or using the
`$ blade extension install /path/to/org.myorg.project.templates.mywebapp-1.0.0.jar`

**Give it a try!**
```
$ blade create -l
```
Now you should see your new custom project template listed!.  If not, contact us on our #blade forum on the Liferay community slack channel.  https://community.liferay.com/it/chat

### Blade Profiles

Lets say that you want to customize the normal blade development workflow.  Normally Liferay developers who use blade will run a series of blade commands that all make sense in the "default" lifeary workspace.

1. blade init
2. blade server init
3. blade server start
4. blade create my-project
5. blade deploy
6. blade customCommand

But if this workspace wants to support `docker` or another _containerized_ Liferay environment, all of those commands
should do something slightly different.  And the case of the last command, it is a *new* command that only makes sense
inside of this development environment.

So a blade profile allows extensions developers to "override" existing blade commands or add "new" commands.
So in the workflow above, blade init for a profile "myprofile" would override the default "init" command do something
before/after the normal init command.  `blade server start` in the case of a profile would override the `ServerStartCommand` in blade and contribute its own way to start the Liferay server (maybe in a container of some sort).

`blade deploy` or `blade watch` would likely mean something completely different in the context of a profile, so that command
would need to be overridden.

The way to "override" existing commands can be see here in the example: https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-profile/src/main/java/com/liferay/extensions/sample/profile

Here we have a "new" command that is only available in this profile and also an "overridden" command that overrides an existing blade command just for this profile.

The way that a "profile" is set, so that blade knows which profile is active depends on the following file in the base
of your Liferay workspace

`${workspaceDir}/.blade/settings.properties`  and in there, there is a property for the profile name that set.

The name of a profile that is set on a workspace has to do with the option that you specify in the `blade init -p <profile-name>` command.

```
$ blade init -p myprofile my-new-custom-workspace
```

After that you will see there is a `my-new-custom-workspace/.blade/settings.properties` file containing the following:
```
liferay.version.default=7.1
profile.name=myprofile
```

## Roadmap

We have some existing plans for blade extensions but are looking for feedback from our community.  Please let
use hear from you over on the blade channel in Liferay Community Slack

https://community.liferay.com/it/chat
https://liferay-community.slack.com/messages/C5US8D29Y

Firstly, we do have plans for a centralized list of extensions that can be installable from blade cli.
```
$ blade extension list
(would print out current list of known extensions)
```
But first we need developers like you to help build their own extensions! :)

Also we will be pulling out some of the features that are inside of blade that some developers don't want or use into
their own extensions thereby reducing the footprint of blade cli for default installs.