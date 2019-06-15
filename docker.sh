#!/usr/bin/env bash
set -e

# docker hub organization to push the generated image to
ORGANIZATION='gryphonzone'

###############################################################################
#############    Do not modify anything below this line    ####################
###############################################################################

# newline
NL=$'\n'

# list of modified files
MODIFIED=`git status --porcelain`

# the directory the script is defined in
DIRECTORY="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if [ "${DIRECTORY}" != "`pwd`" ]; then
    echo "Script must be launched from root of git repository"
    exit 1
fi

if [ -n "${MODIFIED}" ]; then
    echo "Uncommited files, not building docker image: ${NL}${MODIFIED}"
    exit 1
fi

# git hash
HASH=`git rev-parse --short HEAD`

# git branch
GIT_BRANCH=`git branch --no-color 2> /dev/null | sed -e 's/* \(.*\)/\1/'`

# default to using Jenkins' branch name if available, fall back to git
BRANCH="${BRANCH_NAME:-${GIT_BRANCH}}"

# note: assumption - github repo always matches docker image name
REPOSITORY=`git remote get-url origin | sed -e 's@.*/\(.*\)\.git@\1@'`

# calculate tags
VERSION="1.0-${HASH}"
TAG="${ORGANIZATION}/${REPOSITORY}:${VERSION}"
TAG_LATEST="${ORGANIZATION}/${REPOSITORY}:latest"

# build docker image
docker build --pull --progress 'plain' -t "${TAG}" .

# if we're on master, tag the latest image and deploy
if [ "${BRANCH}" = 'master' ]; then
    echo "Pushing the following tags:"
    echo "    ${TAG}"
    echo "    ${TAG_LATEST}"

    docker tag "${TAG}" "${TAG_LATEST}"

    docker push "${TAG}"
    docker push "${TAG_LATEST}"
else
    echo "Currently on branch ${BRANCH}, not pushing tags to remote"
fi
