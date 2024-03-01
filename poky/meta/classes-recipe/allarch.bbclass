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
        # No need for virtual/libc or a cross compiler
        d.setVar("INHIBIT_DEFAULT_DEPS","1")

        # Set these to a common set of values, we shouldn't be using them other that for WORKDIR directory
        # naming anyway
        d.setVar("baselib", "lib")
        d.setVar("TARGET_ARCH", "allarch")
        d.setVar("TARGET_OS", "linux")
        d.setVar("TARGET_CC_ARCH", "none")
        d.setVar("TARGET_LD_ARCH", "none")
        d.setVar("TARGET_AS_ARCH", "none")
        d.setVar("TARGET_FPU", "")
        d.setVar("TARGET_PREFIX", "")
        # Expand PACKAGE_EXTRA_ARCHS since the staging code needs this
        # (this removes any dependencies from the hash perspective)
        d.setVar("PACKAGE_EXTRA_ARCHS", d.getVar("PACKAGE_EXTRA_ARCHS"))
        d.setVar("SDK_ARCH", "none")
        d.setVar("SDK_CC_ARCH", "none")
        d.setVar("TARGET_CPPFLAGS", "none")
        d.setVar("TARGET_CFLAGS", "none")
        d.setVar("TARGET_CXXFLAGS", "none")
        d.setVar("TARGET_LDFLAGS", "none")
        d.setVar("POPULATESYSROOTDEPS", "")

        # Avoid this being unnecessarily different due to nuances of
        # the target machine that aren't important for "all" arch
        # packages.
        d.setVar("LDFLAGS", "")

        # No need to do shared library processing or debug symbol handling
        d.setVar("EXCLUDE_FROM_SHLIBS", "1")
        d.setVar("INHIBIT_PACKAGE_DEBUG_SPLIT", "1")
        d.setVar("INHIBIT_PACKAGE_STRIP", "1")

        # These multilib values shouldn't change allarch packages so exclude them
        d.appendVarFlag("emit_pkgdata", "vardepsexclude", " MULTILIB_VARIANTS")
        d.appendVarFlag("write_specfile", "vardepsexclude", " MULTILIBS")
        d.appendVarFlag("do_package", "vardepsexclude", " package_do_shlibs")

        d.setVar("qemu_wrapper_cmdline", "def qemu_wrapper_cmdline(data, rootfs_path, library_paths):\n    return 'false'")
    elif bb.data.inherits_class('packagegroup', d) and not bb.data.inherits_class('nativesdk', d):
        bb.error("Please ensure recipe %s sets PACKAGE_ARCH before inherit packagegroup" % d.getVar("FILE"))
}

