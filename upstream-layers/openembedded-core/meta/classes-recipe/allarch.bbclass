#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# This class is used for architecture independent recipes/data files (usually scripts)
#

python allarch_package_arch_handler () {
    if bb.data.inherits_class("native", d) or bb.data.inherits_class("nativesdk", d) \
        or bb.data.inherits_class("crosssdk", d):
        return

    variants = d.getVar("MULTILIB_VARIANTS")
    if not variants:
        d.setVar("PACKAGE_ARCH", "all" )
}

addhandler allarch_package_arch_handler
allarch_package_arch_handler[eventmask] = "bb.event.RecipePreFinalise"

python () {
    # Allow this class to be included but overridden - only set
    # the values if we're still "all" package arch.
    if d.getVar("PACKAGE_ARCH") == "all":
        oe.utils.make_arch_independent(d)
    elif bb.data.inherits_class('packagegroup', d) and not bb.data.inherits_class('nativesdk', d):
        bb.error("Please ensure recipe %s sets PACKAGE_ARCH before inherit packagegroup" % d.getVar("FILE"))
}

