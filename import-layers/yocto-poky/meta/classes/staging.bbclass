# These directories will be staged in the sysroot
SYSROOT_DIRS = " \
    ${includedir} \
    ${libdir} \
    ${base_libdir} \
    ${nonarch_base_libdir} \
    ${datadir} \
"

# These directories are also staged in the sysroot when they contain files that
# are usable on the build system
SYSROOT_DIRS_NATIVE = " \
    ${bindir} \
    ${sbindir} \
    ${base_bindir} \
    ${base_sbindir} \
    ${libexecdir} \
    ${sysconfdir} \
    ${localstatedir} \
"
SYSROOT_DIRS_append_class-native = " ${SYSROOT_DIRS_NATIVE}"
SYSROOT_DIRS_append_class-cross = " ${SYSROOT_DIRS_NATIVE}"
SYSROOT_DIRS_append_class-crosssdk = " ${SYSROOT_DIRS_NATIVE}"

# These directories will not be staged in the sysroot
SYSROOT_DIRS_BLACKLIST = " \
    ${mandir} \
    ${docdir} \
    ${infodir} \
    ${datadir}/locale \
    ${datadir}/applications \
    ${datadir}/fonts \
    ${datadir}/pixmaps \
    ${libdir}/${PN}/ptest \
"

sysroot_stage_dir() {
	src="$1"
	dest="$2"
	# if the src doesn't exist don't do anything
	if [ ! -d "$src" ]; then
		 return
	fi

	mkdir -p "$dest"
	(
		cd $src
		find . -print0 | cpio --null -pdlu $dest
	)
}

sysroot_stage_dirs() {
	from="$1"
	to="$2"

	for dir in ${SYSROOT_DIRS}; do
		sysroot_stage_dir "$from$dir" "$to$dir"
	done

	# Remove directories we do not care about
	for dir in ${SYSROOT_DIRS_BLACKLIST}; do
		rm -rf "$to$dir"
	done
}

sysroot_stage_all() {
	sysroot_stage_dirs ${D} ${SYSROOT_DESTDIR}
}

python sysroot_strip () {
    inhibit_sysroot = d.getVar('INHIBIT_SYSROOT_STRIP')
    if inhibit_sysroot and oe.types.boolean(inhibit_sysroot):
        return 0

    dstdir = d.getVar('SYSROOT_DESTDIR')
    pn = d.getVar('PN')
    libdir = os.path.abspath(dstdir + os.sep + d.getVar("libdir"))
    base_libdir = os.path.abspath(dstdir + os.sep + d.getVar("base_libdir"))
    qa_already_stripped = 'already-stripped' in (d.getVar('INSANE_SKIP_' + pn) or "").split()
    strip_cmd = d.getVar("STRIP")

    oe.package.strip_execs(pn, dstdir, strip_cmd, libdir, base_libdir,
                           qa_already_stripped=qa_already_stripped)
}

do_populate_sysroot[dirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[umask] = "022"

addtask populate_sysroot after do_install

SYSROOT_PREPROCESS_FUNCS ?= ""
SYSROOT_DESTDIR = "${WORKDIR}/sysroot-destdir"

python do_populate_sysroot () {
    bb.build.exec_func("sysroot_stage_all", d)
    bb.build.exec_func("sysroot_strip", d)
    for f in (d.getVar('SYSROOT_PREPROCESS_FUNCS') or '').split():
        bb.build.exec_func(f, d)
    pn = d.getVar("PN")
    multiprov = d.getVar("MULTI_PROVIDER_WHITELIST").split()
    provdir = d.expand("${SYSROOT_DESTDIR}${base_prefix}/sysroot-providers/")
    bb.utils.mkdirhier(provdir)
    for p in d.getVar("PROVIDES").split():
        if p in multiprov:
            continue
        p = p.replace("/", "_")
        with open(provdir + p, "w") as f:
            f.write(pn)
}

do_populate_sysroot[vardeps] += "${SYSROOT_PREPROCESS_FUNCS}"
do_populate_sysroot[vardepsexclude] += "MULTI_PROVIDER_WHITELIST"

POPULATESYSROOTDEPS = ""
POPULATESYSROOTDEPS_class-target = "virtual/${MLPREFIX}${TARGET_PREFIX}binutils:do_populate_sysroot"
POPULATESYSROOTDEPS_class-nativesdk = "virtual/${TARGET_PREFIX}binutils-crosssdk:do_populate_sysroot"
do_populate_sysroot[depends] += "${POPULATESYSROOTDEPS}"

SSTATETASKS += "do_populate_sysroot"
do_populate_sysroot[cleandirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[sstate-inputdirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[sstate-outputdirs] = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/${PN}"
do_populate_sysroot[sstate-fixmedir] = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/${PN}"

python do_populate_sysroot_setscene () {
    sstate_setscene(d)
}
addtask do_populate_sysroot_setscene

def staging_copyfile(c, target, dest, postinsts, seendirs):
    import errno

    destdir = os.path.dirname(dest)
    if destdir not in seendirs:
        bb.utils.mkdirhier(destdir)
        seendirs.add(destdir)
    if "/usr/bin/postinst-" in c:
        postinsts.append(dest)
    if os.path.islink(c):
        linkto = os.readlink(c)
        if os.path.lexists(dest):
            if not os.path.islink(dest):
                raise OSError(errno.EEXIST, "Link %s already exists as a file" % dest, dest)
            if os.readlink(dest) == linkto:
                return dest
            raise OSError(errno.EEXIST, "Link %s already exists to a different location? (%s vs %s)" % (dest, os.readlink(dest), linkto), dest)
        os.symlink(linkto, dest)
        #bb.warn(c)
    else:
        try:
            os.link(c, dest)
        except OSError as err:
            if err.errno == errno.EXDEV:
                bb.utils.copyfile(c, dest)
            else:
                raise
    return dest

def staging_copydir(c, target, dest, seendirs):
    if dest not in seendirs:
        bb.utils.mkdirhier(dest)
        seendirs.add(dest)

def staging_processfixme(fixme, target, recipesysroot, recipesysrootnative, d):
    import subprocess

    if not fixme:
        return
    cmd = "sed -e 's:^[^/]*/:%s/:g' %s | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:%s:g; s:FIXMESTAGINGDIRHOST:%s:g'" % (target, " ".join(fixme), recipesysroot, recipesysrootnative)
    for fixmevar in ['COMPONENTS_DIR', 'HOSTTOOLS_DIR', 'PKGDATA_DIR', 'PSEUDO_LOCALSTATEDIR', 'LOGFIFO']:
        fixme_path = d.getVar(fixmevar)
        cmd += " -e 's:FIXME_%s:%s:g'" % (fixmevar, fixme_path)
    bb.debug(2, cmd)
    subprocess.check_output(cmd, shell=True)


def staging_populate_sysroot_dir(targetsysroot, nativesysroot, native, d):
    import glob
    import subprocess
    import errno

    fixme = []
    postinsts = []
    seendirs = set()
    stagingdir = d.getVar("STAGING_DIR")
    if native:
        pkgarchs = ['${BUILD_ARCH}', '${BUILD_ARCH}_*']
        targetdir = nativesysroot
    else:
        pkgarchs = ['${MACHINE_ARCH}']
        pkgarchs = pkgarchs + list(reversed(d.getVar("PACKAGE_EXTRA_ARCHS").split()))
        pkgarchs.append('allarch')
        targetdir = targetsysroot

    bb.utils.mkdirhier(targetdir)
    for pkgarch in pkgarchs:
        for manifest in glob.glob(d.expand("${SSTATE_MANIFESTS}/manifest-%s-*.populate_sysroot" % pkgarch)):
            if manifest.endswith("-initial.populate_sysroot"):
                # skip glibc-initial and libgcc-initial due to file overlap
                continue
            tmanifest = targetdir + "/" + os.path.basename(manifest)
            if os.path.exists(tmanifest):
                continue
            try:
                os.link(manifest, tmanifest)
            except OSError as err:
                if err.errno == errno.EXDEV:
                    bb.utils.copyfile(manifest, tmanifest)
                else:
                    raise
            with open(manifest, "r") as f:
                for l in f:
                    l = l.strip()
                    if l.endswith("/fixmepath"):
                        fixme.append(l)
                        continue
                    if l.endswith("/fixmepath.cmd"):
                        continue
                    dest = l.replace(stagingdir, "")
                    dest = targetdir + "/" + "/".join(dest.split("/")[3:])
                    if l.endswith("/"):
                        staging_copydir(l, targetdir, dest, seendirs)
                        continue
                    try:
                        staging_copyfile(l, targetdir, dest, postinsts, seendirs)
                    except FileExistsError:
                        continue

    staging_processfixme(fixme, targetdir, targetsysroot, nativesysroot, d)
    for p in postinsts:
        subprocess.check_output(p, shell=True)

#
# Manifests here are complicated. The main sysroot area has the unpacked sstate
# which us unrelocated and tracked by the main sstate manifests. Each recipe
# specific sysroot has manifests for each dependency that is installed there.
# The task hash is used to tell whether the data needs to be reinstalled. We
# use a symlink to point to the currently installed hash. There is also a
# "complete" stamp file which is used to mark if installation completed. If
# something fails (e.g. a postinst), this won't get written and we would
# remove and reinstall the dependency. This also means partially installed
# dependencies should get cleaned up correctly.
#

python extend_recipe_sysroot() {
    import copy
    import subprocess
    import errno
    import collections
    import glob

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    mytaskname = d.getVar("BB_RUNTASK")
    if mytaskname.endswith("_setscene"):
        mytaskname = mytaskname.replace("_setscene", "")
    workdir = d.getVar("WORKDIR")
    #bb.warn(str(taskdepdata))
    pn = d.getVar("PN")

    stagingdir = d.getVar("STAGING_DIR")
    sharedmanifests = d.getVar("COMPONENTS_DIR") + "/manifests"
    recipesysroot = d.getVar("RECIPE_SYSROOT")
    recipesysrootnative = d.getVar("RECIPE_SYSROOT_NATIVE")
    current_variant = d.getVar("BBEXTENDVARIANT")

    # Detect bitbake -b usage
    nodeps = d.getVar("BB_LIMITEDDEPS") or False
    if nodeps:
        lock = bb.utils.lockfile(recipesysroot + "/sysroot.lock")
        staging_populate_sysroot_dir(recipesysroot, recipesysrootnative, True, d)
        staging_populate_sysroot_dir(recipesysroot, recipesysrootnative, False, d)
        bb.utils.unlockfile(lock)
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

    def print_dep_tree(deptree):
        data = ""
        for dep in deptree:
            deps = "    " + "\n    ".join(deptree[dep][3]) + "\n"
            data = "%s:\n  %s\n  %s\n%s  %s\n  %s\n" % (deptree[dep][0], deptree[dep][1], deptree[dep][2], deps, deptree[dep][4], deptree[dep][5])
        return data

    #bb.note("Full dep tree is:\n%s" % print_dep_tree(taskdepdata))

    #bb.note(" start2 is %s" % str(start))

    # If start is an sstate task (like do_package) we need to add in its direct dependencies
    # else the code below won't recurse into them.
    for dep in set(start):
        for dep2 in setscenedeps[dep][3]:
            start.add(dep2)
        start.remove(dep)

    #bb.note(" start3 is %s" % str(start))

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

    #bb.note("Computed dep tree is:\n%s" % print_dep_tree(setscenedeps))
    #bb.note(" start is %s" % str(start))

    # Direct dependencies should be present and can be depended upon
    for dep in set(start):
        if setscenedeps[dep][1] == "do_populate_sysroot":
            if dep not in configuredeps:
                configuredeps.append(dep)
    bb.note("Direct dependencies are %s" % str(configuredeps))
    #bb.note(" or %s" % str(start))

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
                if retval:
                    msgbuf.append("Skipping setscene dependency %s for installation into the sysroot" % datadep)
                    continue
                done.append(datadep)
                new.append(datadep)
                if datadep not in configuredeps and setscenedeps[datadep][1] == "do_populate_sysroot":
                    configuredeps.append(datadep)
                    msgbuf.append("Adding dependency on %s" % setscenedeps[datadep][0])
                else:
                    msgbuf.append("Following dependency on %s" % setscenedeps[datadep][0])
        next = new

    # This logging is too verbose for day to day use sadly
    #bb.debug(2, "\n".join(msgbuf))

    depdir = recipesysrootnative + "/installeddeps"
    bb.utils.mkdirhier(depdir)
    bb.utils.mkdirhier(sharedmanifests)

    lock = bb.utils.lockfile(recipesysroot + "/sysroot.lock")

    fixme = {}
    fixme[''] = []
    fixme['native'] = []
    seendirs = set()
    postinsts = []
    multilibs = {}
    manifests = {}
    # All files that we're going to be installing, to find conflicts.
    fileset = {}

    for f in os.listdir(depdir):
        if not f.endswith(".complete"):
            continue
        f = depdir + "/" + f
        if os.path.islink(f) and not os.path.exists(f):
            bb.note("%s no longer exists, removing from sysroot" % f)
            lnk = os.readlink(f.replace(".complete", ""))
            sstate_clean_manifest(depdir + "/" + lnk, d, workdir)
            os.unlink(f)
            os.unlink(f.replace(".complete", ""))

    installed = []
    for dep in configuredeps:
        c = setscenedeps[dep][0]
        if mytaskname in ["do_sdk_depends", "do_populate_sdk_ext"] and c.endswith("-initial"):
            bb.note("Skipping initial setscene dependency %s for installation into the sysroot" % c)
            continue
        installed.append(c)

    # We want to remove anything which this task previously installed but is no longer a dependency
    taskindex = depdir + "/" + "index." + mytaskname
    if os.path.exists(taskindex):
        potential = []
        with open(taskindex, "r") as f:
            for l in f:
                l = l.strip()
                if l not in installed:
                    fl = depdir + "/" + l
                    if not os.path.exists(fl):
                        # Was likely already uninstalled
                        continue
                    potential.append(l)
        # We need to ensure not other task needs this dependency. We hold the sysroot
        # lock so we ca search the indexes to check
        if potential:
            for i in glob.glob(depdir + "/index.*"):
                if i.endswith("." + mytaskname):
                    continue
                with open(i, "r") as f:
                    for l in f:
                        l = l.strip()
                        if l in potential:
                            potential.remove(l)
        for l in potential:
            fl = depdir + "/" + l
            bb.note("Task %s no longer depends on %s, removing from sysroot" % (mytaskname, l))
            lnk = os.readlink(fl)
            sstate_clean_manifest(depdir + "/" + lnk, d, workdir)
            os.unlink(fl)
            os.unlink(fl + ".complete")

    msg_exists = []
    msg_adding = []
    for dep in configuredeps:
        c = setscenedeps[dep][0]
        if c not in installed:
            continue
        taskhash = setscenedeps[dep][5]
        taskmanifest = depdir + "/" + c + "." + taskhash

        if os.path.exists(depdir + "/" + c):
            lnk = os.readlink(depdir + "/" + c)
            if lnk == c + "." + taskhash and os.path.exists(depdir + "/" + c + ".complete"):
                msg_exists.append(c)
                continue
            else:
                bb.note("%s exists in sysroot, but is stale (%s vs. %s), removing." % (c, lnk, c + "." + taskhash))
                sstate_clean_manifest(depdir + "/" + lnk, d, workdir)
                os.unlink(depdir + "/" + c)
                if os.path.lexists(depdir + "/" + c + ".complete"):
                    os.unlink(depdir + "/" + c + ".complete")
        elif os.path.lexists(depdir + "/" + c):
            os.unlink(depdir + "/" + c)

        msg_adding.append(c)

        os.symlink(c + "." + taskhash, depdir + "/" + c)

        d2 = d
        destsysroot = recipesysroot
        variant = ''
        if setscenedeps[dep][2].startswith("virtual:multilib"):
            variant = setscenedeps[dep][2].split(":")[2]
            if variant != current_variant:
                if variant not in multilibs:
                    multilibs[variant] = get_multilib_datastore(variant, d)
                d2 = multilibs[variant]
                destsysroot = d2.getVar("RECIPE_SYSROOT")

        native = False
        if c.endswith("-native"):
            manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-${BUILD_ARCH}-%s.populate_sysroot" % c)
            native = True
        elif c.startswith("nativesdk-"):
            manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-${SDK_ARCH}_${SDK_OS}-%s.populate_sysroot" % c)
        elif "-cross-" in c:
            manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-${BUILD_ARCH}_${TARGET_ARCH}-%s.populate_sysroot" % c)
            native = True
        elif "-crosssdk" in c:
            manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-${BUILD_ARCH}_${SDK_ARCH}_${SDK_OS}-%s.populate_sysroot" % c)
            native = True
        else:
            pkgarchs = ['${MACHINE_ARCH}']
            pkgarchs = pkgarchs + list(reversed(d2.getVar("PACKAGE_EXTRA_ARCHS").split()))
            pkgarchs.append('allarch')
            for pkgarch in pkgarchs:
                manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-%s-%s.populate_sysroot" % (pkgarch, c))
                if os.path.exists(manifest):
                    break
        if not os.path.exists(manifest):
            bb.warn("Manifest %s not found?" % manifest)
        else:
            newmanifest = collections.OrderedDict()
            if native:
                fm = fixme['native']
                targetdir = recipesysrootnative
            else:
                fm = fixme['']
                targetdir = destsysroot
            with open(manifest, "r") as f:
                manifests[dep] = manifest
                for l in f:
                    l = l.strip()
                    if l.endswith("/fixmepath"):
                        fm.append(l)
                        continue
                    if l.endswith("/fixmepath.cmd"):
                        continue
                    dest = l.replace(stagingdir, "")
                    dest = "/" + "/".join(dest.split("/")[3:])
                    newmanifest[l] = targetdir + dest

                    # Check if files have already been installed by another
                    # recipe and abort if they have, explaining what recipes are
                    # conflicting.
                    hashname = targetdir + dest
                    if not hashname.endswith("/"):
                        if hashname in fileset:
                            bb.fatal("The file %s is installed by both %s and %s, aborting" % (dest, c, fileset[hashname]))
                        else:
                            fileset[hashname] = c

            # Having multiple identical manifests in each sysroot eats diskspace so
            # create a shared pool of them and hardlink if we can.
            # We create the manifest in advance so that if something fails during installation,
            # or the build is interrupted, subsequent exeuction can cleanup.
            sharedm = sharedmanifests + "/" + os.path.basename(taskmanifest)
            if not os.path.exists(sharedm):
                smlock = bb.utils.lockfile(sharedm + ".lock")
                # Can race here. You'd think it just means we may not end up with all copies hardlinked to each other
                # but python can lose file handles so we need to do this under a lock.
                if not os.path.exists(sharedm):
                    with open(sharedm, 'w') as m:
                       for l in newmanifest:
                           dest = newmanifest[l]
                           m.write(dest.replace(workdir + "/", "") + "\n")
                bb.utils.unlockfile(smlock)
            try:
                os.link(sharedm, taskmanifest)
            except OSError as err:
                if err.errno == errno.EXDEV:
                    bb.utils.copyfile(sharedm, taskmanifest)
                else:
                    raise
            # Finally actually install the files
            for l in newmanifest:
                    dest = newmanifest[l]
                    if l.endswith("/"):
                        staging_copydir(l, targetdir, dest, seendirs)
                        continue
                    staging_copyfile(l, targetdir, dest, postinsts, seendirs)

    bb.note("Installed into sysroot: %s" % str(msg_adding))
    bb.note("Skipping as already exists in sysroot: %s" % str(msg_exists))

    for f in fixme:
        if f == '':
            staging_processfixme(fixme[f], recipesysroot, recipesysroot, recipesysrootnative, d)
        elif f == 'native':
            staging_processfixme(fixme[f], recipesysrootnative, recipesysroot, recipesysrootnative, d)
        else:
            staging_processfixme(fixme[f], multilibs[f].getVar("RECIPE_SYSROOT"), recipesysroot, recipesysrootnative, d)

    for p in postinsts:
        subprocess.check_output(p, shell=True)

    for dep in manifests:
        c = setscenedeps[dep][0]
        os.symlink(manifests[dep], depdir + "/" + c + ".complete")

    with open(taskindex, "w") as f:
        for l in sorted(installed):
            f.write(l + "\n")

    bb.utils.unlockfile(lock)
}
extend_recipe_sysroot[vardepsexclude] += "MACHINE_ARCH PACKAGE_EXTRA_ARCHS SDK_ARCH BUILD_ARCH SDK_OS BB_TASKDEPDATA"

python do_prepare_recipe_sysroot () {
    bb.build.exec_func("extend_recipe_sysroot", d)
}
addtask do_prepare_recipe_sysroot before do_configure after do_fetch

# Clean out the recipe specific sysroots before do_fetch
# (use a prefunc so we can order before extend_recipe_sysroot if it gets added)
python clean_recipe_sysroot() {
    # We remove these stamps since we're removing any content they'd have added with
    # cleandirs. This removes the sigdata too, likely not a big deal,
    oe.path.remove(d.getVar("STAMP") + "*addto_recipe_sysroot*")
    return
}
clean_recipe_sysroot[cleandirs] += "${RECIPE_SYSROOT} ${RECIPE_SYSROOT_NATIVE}"
do_fetch[prefuncs] += "clean_recipe_sysroot"

python staging_taskhandler() {
    bbtasks = e.tasklist
    for task in bbtasks:
        deps = d.getVarFlag(task, "depends")
        if deps and "populate_sysroot" in deps:
            d.appendVarFlag(task, "prefuncs", " extend_recipe_sysroot")
}
staging_taskhandler[eventmask] = "bb.event.RecipeTaskPreProcess"
addhandler staging_taskhandler
