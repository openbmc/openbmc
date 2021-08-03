WORKDIR_PKGDATA = "${WORKDIR}/pkgdata-sysroot"

def package_populate_pkgdata_dir(pkgdatadir, d):
    import glob

    postinsts = []
    seendirs = set()
    stagingdir = d.getVar("PKGDATA_DIR")
    pkgarchs = ['${MACHINE_ARCH}']
    pkgarchs = pkgarchs + list(reversed(d.getVar("PACKAGE_EXTRA_ARCHS").split()))
    pkgarchs.append('allarch')

    bb.utils.mkdirhier(pkgdatadir)
    for pkgarch in pkgarchs:
        for manifest in glob.glob(d.expand("${SSTATE_MANIFESTS}/manifest-%s-*.packagedata" % pkgarch)):
            with open(manifest, "r") as f:
                for l in f:
                    l = l.strip()
                    dest = l.replace(stagingdir, "")
                    if l.endswith("/"):
                        staging_copydir(l, pkgdatadir, dest, seendirs)
                        continue
                    try:
                        staging_copyfile(l, pkgdatadir, dest, postinsts, seendirs)
                    except FileExistsError:
                        continue

python package_prepare_pkgdata() {
    import copy
    import glob

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    mytaskname = d.getVar("BB_RUNTASK")
    if mytaskname.endswith("_setscene"):
        mytaskname = mytaskname.replace("_setscene", "")
    workdir = d.getVar("WORKDIR")
    pn = d.getVar("PN")
    stagingdir = d.getVar("PKGDATA_DIR")
    pkgdatadir = d.getVar("WORKDIR_PKGDATA")

    # Detect bitbake -b usage
    nodeps = d.getVar("BB_LIMITEDDEPS") or False
    if nodeps:
        staging_package_populate_pkgdata_dir(pkgdatadir, d)
        return

    start = None
    configuredeps = []
    for dep in taskdepdata:
        data = taskdepdata[dep]
        if data[1] == mytaskname and data[0] == pn:
            start = dep
            break
    if start is None:
        bb.fatal("Couldn't find ourself in BB_TASKDEPDATA?")

    # We need to figure out which sysroot files we need to expose to this task.
    # This needs to match what would get restored from sstate, which is controlled
    # ultimately by calls from bitbake to setscene_depvalid().
    # That function expects a setscene dependency tree. We build a dependency tree
    # condensed to inter-sstate task dependencies, similar to that used by setscene
    # tasks. We can then call into setscene_depvalid() and decide
    # which dependencies we can "see" and should expose in the recipe specific sysroot.
    setscenedeps = copy.deepcopy(taskdepdata)

    start = set([start])

    sstatetasks = d.getVar("SSTATETASKS").split()
    # Add recipe specific tasks referenced by setscene_depvalid()
    sstatetasks.append("do_stash_locale")

    # If start is an sstate task (like do_package) we need to add in its direct dependencies
    # else the code below won't recurse into them.
    for dep in set(start):
        for dep2 in setscenedeps[dep][3]:
            start.add(dep2)
        start.remove(dep)

    # Create collapsed do_populate_sysroot -> do_populate_sysroot tree
    for dep in taskdepdata:
        data = setscenedeps[dep]
        if data[1] not in sstatetasks:
            for dep2 in setscenedeps:
                data2 = setscenedeps[dep2]
                if dep in data2[3]:
                    data2[3].update(setscenedeps[dep][3])
                    data2[3].remove(dep)
            if dep in start:
                start.update(setscenedeps[dep][3])
                start.remove(dep)
            del setscenedeps[dep]

    # Remove circular references
    for dep in setscenedeps:
        if dep in setscenedeps[dep][3]:
            setscenedeps[dep][3].remove(dep)

    # Direct dependencies should be present and can be depended upon
    for dep in set(start):
        if setscenedeps[dep][1] == "do_packagedata":
            if dep not in configuredeps:
                configuredeps.append(dep)

    msgbuf = []
    # Call into setscene_depvalid for each sub-dependency and only copy sysroot files
    # for ones that would be restored from sstate.
    done = list(start)
    next = list(start)
    while next:
        new = []
        for dep in next:
            data = setscenedeps[dep]
            for datadep in data[3]:
                if datadep in done:
                    continue
                taskdeps = {}
                taskdeps[dep] = setscenedeps[dep][:2]
                taskdeps[datadep] = setscenedeps[datadep][:2]
                retval = setscene_depvalid(datadep, taskdeps, [], d, msgbuf)
                done.append(datadep)
                new.append(datadep)
                if retval:
                    msgbuf.append("Skipping setscene dependency %s" % datadep)
                    continue
                if datadep not in configuredeps and setscenedeps[datadep][1] == "do_packagedata":
                    configuredeps.append(datadep)
                    msgbuf.append("Adding dependency on %s" % setscenedeps[datadep][0])
                else:
                    msgbuf.append("Following dependency on %s" % setscenedeps[datadep][0])
        next = new

    # This logging is too verbose for day to day use sadly
    #bb.debug(2, "\n".join(msgbuf))

    seendirs = set()
    postinsts = []
    multilibs = {}
    manifests = {}

    msg_adding = []

    for dep in configuredeps:
        c = setscenedeps[dep][0]
        msg_adding.append(c)

        manifest, d2 = oe.sstatesig.find_sstate_manifest(c, setscenedeps[dep][2], "packagedata", d, multilibs)
        destsysroot = pkgdatadir

        if manifest:
            targetdir = destsysroot
            with open(manifest, "r") as f:
                manifests[dep] = manifest
                for l in f:
                    l = l.strip()
                    dest = targetdir + l.replace(stagingdir, "")
                    if l.endswith("/"):
                        staging_copydir(l, targetdir, dest, seendirs)
                        continue
                    staging_copyfile(l, targetdir, dest, postinsts, seendirs)

    bb.note("Installed into pkgdata-sysroot: %s" % str(msg_adding))

}
package_prepare_pkgdata[cleandirs] = "${WORKDIR_PKGDATA}"
package_prepare_pkgdata[vardepsexclude] += "MACHINE_ARCH PACKAGE_EXTRA_ARCHS SDK_ARCH BUILD_ARCH SDK_OS BB_TASKDEPDATA SSTATETASKS"


