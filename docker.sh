#!/usr/bin/env bash
set -e -x

nl=$'\n'
modified=`git status --porcelain`

if [ -n "${modified}" ]; then
    echo "Uncommited files, not building docker image: ${nl}${modified}"
    exit 1
fi

HASH=`git rev-parse --short HEAD`

ORGANIZATION='gryphonzone'
REPOSITORY='jenkins'
VERSION="1.0-${HASH}"

TAG="${ORGANIZATION}/${REPOSITORY}:${VERSION}"
TAG_LATEST="${ORGANIZATION}/${REPOSITORY}:latest"

docker build -t "${TAG}" .
docker tag "${TAG}" "${TAG_LATEST}"

docker push "${TAG}"
docker push "${TAG_LATEST}"
