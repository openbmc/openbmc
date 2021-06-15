# WARNING!
#
# These modifications to os-release disable the bitbake parse
# cache (for the os-release recipe only).  Before copying
# and pasting into another recipe ensure it is understood
# what that means!

def run_git(d, cmd):
    try:
        oeroot = d.getVar('COREBASE', True)
        return bb.process.run(("export PSEUDO_DISABLED=1; " +
                               "git --work-tree %s --git-dir %s/.git %s")
            % (oeroot, oeroot, cmd))[0].strip('\n')
    except Exception as e:
        bb.warn("Unexpected exception from 'git' call: %s" % e)
        pass

VERSION_ID := "${@run_git(d, 'describe --dirty')}"
VERSION = "${@'-'.join(d.getVar('VERSION_ID').split('-')[0:2])}"

BUILD_ID := "${@run_git(d, 'describe --abbrev=0')}"
OPENBMC_TARGET_MACHINE = "${MACHINE}"

OS_RELEASE_FIELDS_append = " BUILD_ID OPENBMC_TARGET_MACHINE EXTENDED_VERSION"

# Ensure the git commands run every time bitbake is invoked.
BB_DONT_CACHE = "1"

# Make os-release available to other recipes.
SYSROOT_DIRS_append = " ${sysconfdir}"
