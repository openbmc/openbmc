#!/bin/sh

# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

set -e

# shellcheck disable=SC1091
. /utils.sh

GIT_REPO_PATH="/work"

[ -n "$BASE_REF" ] ||
	error "DCO checks needs to know the target branch. Make sure that is set in BASE_REF."
[ -d "$GIT_REPO_PATH/.git" ] ||
	error "Can't find a git checkout under $GIT_REPO_PATH ."
cd "$GIT_REPO_PATH"

# The GitHub runner user and the container user might differ making git error
# out with:
# 	error: fatal: detected dubious ownership in repository at '/work'
# Avoid this as the security risk is minimum here while guarding the git hooks
# via PRs.
git config --global --add safe.directory /work

dco-check \
	--verbose \
	--default-branch "origin/$BASE_REF"
