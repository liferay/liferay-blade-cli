# How to Release

## Create a stable release

1. Create a BLADE jira ticket for the new release

1. Create a new branch based on ticket

1. Run `gradlew prepareRelease`

1. Commit the changes

1. Generate changelog`./gradlew -b changelogs-sdk.gradle buildChangelogs`

1. Commit the changelog

1. Create PR on `liferay/liferay-blade-cli` repo from this branch

1. If the PR passes all the tests, merge the PR to master.

1. Generate a new tag based on the new version `git tag 4.1.2`

1. Push the tags to upstream `git push --tags upstream`

By merging this PR to master, and since all of the versions of the components are set to stable versions, this will cause the GitHub workflow to release the binaries to nexus which will cause the existing blade CLIs to report a new stable release/version is available and anyone who runs `blade update` will get the new version. See [Create a snapshot release](#create-a-snapshot-release) right after releasing a stable version.

## Create a snapshot release

1. Create a new branch based for updating to new snapshot release

1. Run `gradlew prepareSnapshot`

1. Commit the changes using the release ticket

1. Create a PR

1. If it passes CI, merge the PR

By merging this PR to master with all the version numbers for components that end in `-SNAPSHOT` this will cause the GitHub workflow to release the binaries for a new snapshot release.