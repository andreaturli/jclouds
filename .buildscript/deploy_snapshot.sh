#!/bin/bash
#
# Deploy a jar, source jar, and javadoc jar to Sonatype's snapshot repo.
#
# Adapted from https://github.com/square/retrofit/commit/a38b6bed07e162765854f1ea3a1a8f195c491031
#
SLUG="andreaturli/jclouds"
JDK="oraclejdk7"
BRANCH="1.8.x"

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying snapshot..."
  cd project
  echo $PWD
  mvn versions:set versions:update-child-modules -DnewVersion=1.8.2-$\{sha1\}-SNAPSHOT -DgenerateBackupPoms=false --quiet
  cd ..
  echo $PWD
  mvn clean source:jar javadoc:jar deploy --settings=".buildscript/settings.xml" -Dmaven.test.skip=true -Dsha1=`git rev-parse HEAD` --quiet
  echo "Snapshot deployed!"
fi
