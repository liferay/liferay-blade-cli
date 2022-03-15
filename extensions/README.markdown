# Blade Extensions

There are several ways you can extend Blade CLI. In simple cases, you can add
custom *commands* and *templates*. For cases where you want to create a
fully-customized Liferay workspace workflow tailored for your requirements in
Blade CLI, you can create custom commands and templates that are all associated
with a Blade *profile*.

When Blade CLI starts, it looks in the user's `${user.home}/.blade/extensions`
folder for any JAR files. All JAR files are searched to see if they contain
valid Blade extensions. You'll learn how to install new extensions next.

## Installing New Extensions

You can execute the `blade extensions install` command to install an existing
Blade extension from a

1. Local JAR file

        blade extension install /path/to/my_extension.JAR

1. Remote JAR file indicated by a URL

        blade extension install https://repository.lifera.com/public/group/com/liferay/blade/com.liferay.blade.extension/1.0.0/com.liferay.blade.extension-1.0.0.JAR

1. Github repository that contains an extension

        $ blade extension install https://github.com/gamerson/blade-sample-command

You can customize the above commands to fit your specific scenario.

## Developing New Extensions

There are a few use cases to consider when extending Blade CLI. If you only want
to add a new command that adds to Blade CLI's standard set of commands and also
applies globally to all types of workspaces, you can add a new custom command.
See the [Custom Commands](#custom-commands) section for details.

In most cases, you'll want a set of custom commands that only apply to a
specific workspace environment. For example, suppose you want to build some
custom commands that only work when the Liferay workspace is configured to use
`docker`, `wedeploy`, or `openshift`. Then you should create a profile that
supports the development workflow in that environment. See the
[Blade Profiles](#blade-profiles) section for more details.

### Custom Commands

To create a custom command, you must build a Command class using the
[JCommander](http://jcommander.org/) framework. Then register your new command
with Blade by using standard JRE service loader mechanisms. Blade looks for
custom commands using this service interface:
`com.liferay.blade.cli.command.BaseCommand`.

Therefore, in your extension JAR, you should have a file like this:
`META-INF/services/com.liferay.blade.cli.command.BaseCommand1`. This class
should list all of your custom commands' FQDN classes.

An example of how this should be laid out can be found in the Blade CLI
repository:
https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-command

Pay close attention to a few things in this project:

1. Registering your command with Blade:
   https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-command/src/main/resources/META-INF/services/com.liferay.blade.cli.command.BaseCommand

1. Building your extension against the Blade CLI API (which is not yet
   officially published, but you can use the provisional repo as shown here):
   https://github.com/gamerson/blade-sample-command/blob/master/build.gradle#L18.

1. Authoring your tests for your custom commands:
   https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-command/src/test/java/com/liferay/extensions/sample/command/SampleCommandsTest.java.

Once you have your command, build it with Gradle (`gradle build`) to generate
your JAR file. Now you can test it in Blade CLI by running `blade extension
install /path/to/your/blade-command/build/libs/blade-command.JAR` or by simply
copying the file to `${user.home}/.blade/extensions`.

Test to see if your command is available by running

    blade help

Now you should see your new custom command listed! If not, contact us in the
Liferay Community
[`#blade`](https://liferay-community.slack.com/messages/C5US8D29Y) Slack
channel. Register here:
[https://community.liferay.com/en/chat](https://community.liferay.com/en/chat).

### Custom Project Templates

Blade comes with over 37+ project templates (whew!) right now, but many times
you may feel that those are too simple or don't fit the need for your
development team. You can create new custom project templates that fit your
team's workflow and have Blade use it instead. To do this, follow this example:
https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-template

Here are a few things to notice from the sample:

1. This template is structured as a Maven archetype. Read more about Maven
   archetypes and their features and capabilities
   [here](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html).

1. The `BundleSymbolic` name of your template JAR (see
   [here](https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-template/bnd.bnd#L3))
   must have the pattern `*.project.templates.<name>.*`. For example, suppose
   you have a custom template called *mywebapp*. Then the BSN should be
   `org.myorg.project.templates.mywebapp-1.0.0.JAR`.

1. Testing your custom template can be accomplished by following the example
   [here](https://github.com/liferay/liferay-blade-cli/blob/master/extensions/sample-template/src/test/java/com/liferay/project/templates/sample/TemplatesTest.java).

Once you have your custom template, you can install it by copying it to
`${user.home}/.blade/extensions` or using the `blade extension install
/path/to/org.myorg.project.templates.mywebapp-1.0.0.JAR` command.

Test to see if your project template is available by running

    blade create -l

Now you should see your new custom project template listed! If not, contact us
in the Liferay Community
[`#blade`](https://liferay-community.slack.com/messages/C5US8D29Y) Slack
channel. Register here:
[https://community.liferay.com/en/chat](https://community.liferay.com/en/chat).

### Blade Profiles

Let's say that you want to customize the normal Blade development workflow.
Normally, Liferay developers who use Blade CLI run a series of Blade commands
that all make sense in the *default* Liferay workspace. For example,

1. `blade init`

1. `blade server init`

1. `blade server start`

1. `blade create my-project`

1. `blade deploy`

1. `blade customCommand`

But if this workspace wants to support `docker` or another *containerized*
Liferay environment, all of those commands should do something slightly
different. Also, the new command would only make sense inside of this specific
development environment.

To customize Blade CLI's development workflow, create a Blade *profile*. Blade
profiles let you *override* existing Blade commands or add *new* commands. So in
the workflow above, `blade init` for a profile `myprofile` would override the
default `init` command to do something before/after the normal `init` command.
The command `blade server start`, in the case of a profile, would override the
`ServerStartCommand` in Blade and contribute its own way to start the Liferay
server (e.g., in a container of some sort).

Commands like `blade deploy` or `blade watch` would likely mean something
completely different in the context of a profile, so they would need to be
overridden too.

The way to override existing commands can be seen in this example:
https://github.com/liferay/liferay-blade-cli/tree/master/extensions/sample-profile/src/main/java/com/liferay/extensions/sample/profile

Here we have a new command that is only available in this profile and also an
overridden command that overrides an existing Blade command just for this
profile.

The way that a profile is set, so that Blade knows which profile is active,
depends on the `${workspaceDir}/.blade/settings.properties` file.

Blade knows which profile is active by reading the `profile.name` property set
in the `${workspaceDir}/.blade/settings.properties` file. This is initially set
by the option you specify when initially creating your workspace:
`blade init -p <profile-name>`. It's set to the `gradle` profile by default.

For example, if you execute the following command:

    blade init -p myprofile my-new-custom-workspace

Your `my-new-custom-workspace` will have the following properties set in its
`.blade/settings.properties` file:

    liferay.version.default=7.1
    profile.name=myprofile

## Roadmap

We have some existing plans for Blade extensions but are looking for feedback
from our community. Please let use hear from you on the `#blade` channel in
Liferay Community Slack (register [here](https://community.liferay.com/en/chat)):

https://liferay-community.slack.com/messages/C5US8D29Y

Firstly, we do have plans for a centralized list of extensions that can be
installable from Blade CLI.

    blade extension list

This would print out a current list of known extensions. It's not yet available;
first we need developers like you to help build their own extensions!

Also we will be pulling out some of the features that are inside of Blade that
some developers don't want or use into their own extensions, thereby reducing
the footprint of Blade CLI for default installs.

We welcome all feedback and suggestions! Please give these features a try and
let us know what you think!