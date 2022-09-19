# WARNING!
#
# These modifications to os-release disable the bitbake parse
# cache (for the os-release recipe only).  Before copying
# and pasting into another recipe ensure it is understood
# what that means!

OS_RELEASE_ROOTPATH ?= "${COREBASE}"
def run_git(d, cmd):
    try:
        oeroot = d.getVar('OS_RELEASE_ROOTPATH', True)
        return bb.process.run(("export PSEUDO_DISABLED=1; " +
                               "git --work-tree %s --git-dir %s/.git %s")
            % (oeroot, oeroot, cmd))[0].strip('\n')
    except Exception as e:
        bb.warn("Unexpected exception from 'git' call: %s" % e)
        pass
# DISTRO_VERSION can be overridden by a bbappend or config, so it must be a
# weak override.  But, when a variable is weakly overridden the definition
# and not the contents are used in the task-hash (for sstate reuse).  We need
# a strong variable in the vardeps chain for do_compile so that we get the
# contents of the 'git describe --dirty' call.  Create a strong/immediate
# indirection via PHOSPHOR_OS_RELEASE_DISTRO_VERSION.
PHOSPHOR_OS_RELEASE_DISTRO_VERSION := "${@run_git(d, 'describe --dirty')}"
DISTRO_VERSION ??= "${PHOSPHOR_OS_RELEASE_DISTRO_VERSION}"
EXTENDED_VERSION ??= "${PHOSPHOR_OS_RELEASE_DISTRO_VERSION}"
VERSION = "${@'-'.join(d.getVar('VERSION_ID').split('-')[0:2])}"
OPENBMC_TARGET_MACHINE = "${MACHINE}"
OS_RELEASE_FIELDS:append = " BUILD_ID OPENBMC_TARGET_MACHINE EXTENDED_VERSION"
# Ensure the git commands run every time bitbake is invoked.
BB_DONT_CACHE = "1"
# Make os-release available to other recipes.
SYSROOT_DIRS:append = " ${sysconfdir}"
