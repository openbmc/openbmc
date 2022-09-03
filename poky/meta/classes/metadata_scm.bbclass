#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

METADATA_BRANCH := "${@oe.buildcfg.detect_branch(d)}"
METADATA_BRANCH[vardepvalue] = "${METADATA_BRANCH}"
METADATA_REVISION := "${@oe.buildcfg.detect_revision(d)}"
METADATA_REVISION[vardepvalue] = "${METADATA_REVISION}"
