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

# The GitHub runner user and the container user might differ making git error
# out with:
# 	error: fatal: detected dubious ownership in repository at '/work'
# Avoid this as the security risk is minimum here while guarding the git hooks
# via PRs.
git config --global --add safe.directory /work

TEMP_DIR="$(mktemp -d)"
cd "$TEMP_DIR"

REPOS=" \
    https://git.openembedded.org/openembedded-core \
    https://git.openembedded.org/bitbake \
"
for repo in $REPOS; do
    log "Cloning $repo on branch $BASE_REF..."
    git clone --depth 1 --branch "$BASE_REF" "$repo"
done

# shellcheck disable=SC1091,SC2240
. ./openembedded-core/oe-init-build-env build

# Build configuration
printf "\n# ------ ci ------\n" >> conf/local.conf
cat <<EOCONF >>conf/local.conf
LICENSE_FLAGS_ACCEPTED = "synaptics-killswitch"
EOCONF

MACHINES="$(find "$GIT_REPO_PATH/conf/machine" -maxdepth 1 -name '*.conf' \
    -exec basename {} .conf \; | sort)"
[ -n "$MACHINES" ] ||
    error "No machines found under $GIT_REPO_PATH/conf/machine ."

# shellcheck disable=SC2086
yocto-check-layer --with-software-layer-signature-check --debug \
    "$GIT_REPO_PATH" --machines $MACHINES
