#!/bin/sh

# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

set -ex

# shellcheck disable=SC1091
. /utils.sh

GIT_REPO_PATH="/work"

[ -n "$BASE_REF" ] ||
    error "Target branch is needed. Make sure that is set in BASE_REF."
[ -d "$GIT_REPO_PATH/.git" ] ||
    error "Can't find a git checkout under $GIT_REPO_PATH ."

TEMP_DIR="$(mktemp -d)"
cd "$TEMP_DIR"

REPOS=" \
    git://git.openembedded.org/openembedded-core \
    git://git.openembedded.org/bitbake \
"
for repo in $REPOS; do
    log "Cloning $repo on branch $BASE_REF..."
    git clone --depth 1 --branch "$BASE_REF" "$repo"
done

# shellcheck disable=SC1091,SC2240
. ./openembedded-core/oe-init-build-env build
yocto-check-layer --with-software-layer-signature-check --debug \
    "$GIT_REPO_PATH"
