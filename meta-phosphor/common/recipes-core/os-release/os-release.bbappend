# WARNING!
#
# These modifications to os-release disable multiple bitbake caching
# mechanisms (both the parse cache and the sstate cache).  Before
# copying and pasting into another recipe ensure it is understood
# what this means!

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

    # The default sstate setup gets in the way here.
    # Given the low impact nature of this recipe
    # just don't bother with it at all.
    tasks = filter(lambda k: d.getVarFlag(k, "task", True), d.keys())
    for task in tasks:
        if task.endswith("_setscene"):
            bb.build.deltask(task, d)
}

OS_RELEASE_FIELDS_append = " BUILD_ID"

# Ensure the git commands run every time bitbake is invoked.
BB_DONT_CACHE = "1"

# Make os-release available to other recipes.
SYSROOT_DIRS_append = " ${sysconfdir}"
