#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
#
# ${PN}-bin is defined in bitbake.conf
#
# We need to allow the other packages to be greedy with what they
# want out of /usr/bin and /usr/sbin before ${PN}-bin gets greedy.
# 
PACKAGE_BEFORE_PN = "${PN}-bin"
