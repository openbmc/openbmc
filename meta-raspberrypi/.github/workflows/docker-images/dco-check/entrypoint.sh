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
dco-check \
	--verbose \
	--default-branch "origin/$BASE_REF"
