#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Check types of bitbake configuration variables
#
# See oe.types for details.

python check_types() {
    import oe.types
    for key in e.data.keys():
        if e.data.getVarFlag(key, "type"):
            oe.data.typed_value(key, e.data)
}
addhandler check_types
check_types[eventmask] = "bb.event.ConfigParsed"
