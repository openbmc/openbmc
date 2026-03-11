#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

python mcextend_virtclass_handler () {
    cls = e.data.getVar("BBEXTENDCURR")
    variant = e.data.getVar("BBEXTENDVARIANT")
    if cls != "mcextend" or not variant:
        return

    override = ":virtclass-mcextend-" + variant

    e.data.setVar("PN", e.data.getVar("PN", False) + "-" + variant)
    e.data.setVar("MCNAME", variant)
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)
}

addhandler mcextend_virtclass_handler
mcextend_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

