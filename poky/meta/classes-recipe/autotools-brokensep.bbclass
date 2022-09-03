#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Autotools class for recipes where separate build dir doesn't work
# Ideally we should fix software so it does work. Standard autotools supports
# this.
inherit autotools
B = "${S}"
