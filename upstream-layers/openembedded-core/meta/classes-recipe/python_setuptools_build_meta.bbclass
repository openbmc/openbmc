#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python_pep517

DEPENDS += "python3-setuptools-native"

# This isn't nice, but is the best solutions to ensure clean builds for now.
# https://github.com/pypa/setuptools/issues/4732
do_configure[cleandirs] = "${PEP517_SOURCE_PATH}/build"
