# WARNING!
#
# These modifications to os-release disable the bitbake parse
# cache (for the os-release recipe only).  Before copying
# and pasting into another recipe ensure it is understood
# what that means!

def run_git(d, cmd):
    try:
        oeroot = d.getVar('COREBASE', True)
        return bb.process.run("git --work-tree %s --git-dir %s/.git %s"
            % (oeroot, oeroot, cmd))[0].strip('\n')
    except:
        pass

python() {
    version_id = run_git(d, 'describe --dirty --long')
    if version_id:
        d.setVar('VERSION_ID', version_id)
        versionList = version_id.split('-')
        version = versionList[0] + "-" + versionList[1]
        d.setVar('VERSION', version)

    build_id = run_git(d, 'describe --abbrev=0')
    if build_id:
        d.setVar('BUILD_ID', build_id)

    target_machine = d.getVar('MACHINE', True)
    if target_machine:
        d.setVar('OPENBMC_TARGET_MACHINE', target_machine)
}

OS_RELEASE_FIELDS_append = " BUILD_ID OPENBMC_TARGET_MACHINE"

# Ensure the git commands run every time bitbake is invoked.
BB_DONT_CACHE = "1"

# Make os-release available to other recipes.
SYSROOT_DIRS_append = " ${sysconfdir}"
