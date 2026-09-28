#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# This bbclass is expected to be inherited by recipes explicitly.
# If a recipe's version is separated by point and we know for sure
# which parts of the version represent the stable part, then the
# recipe could inherit this bbclass.
#

STABLE_VERSION_PARTS ?= "2"
def get_majmin_version_regex(d):
    pv = d.getVar('PV')
    stable_parts = pv.split('.')[:int(d.getVar('STABLE_VERSION_PARTS'))]
    return r'\.'.join(stable_parts)

STABLE_VERSION_REGEX = "${@get_majmin_version_regex(d)}"
UPSTREAM_STABLE_RELEASE_REGEX ?= "^${STABLE_VERSION_REGEX}(\.\d+)*$"
