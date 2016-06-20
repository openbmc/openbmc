SSTATE_VERSION = "3"

SSTATE_MANIFESTS ?= "${TMPDIR}/sstate-control"
SSTATE_MANFILEPREFIX = "${SSTATE_MANIFESTS}/manifest-${SSTATE_MANMACH}-${PN}"

def generate_sstatefn(spec, hash, d):
    if not hash:
        hash = "INVALID"
    return hash[:2] + "/" + spec + hash

SSTATE_PKGARCH    = "${PACKAGE_ARCH}"
SSTATE_PKGSPEC    = "sstate:${PN}:${PACKAGE_ARCH}${TARGET_VENDOR}-${TARGET_OS}:${PV}:${PR}:${SSTATE_PKGARCH}:${SSTATE_VERSION}:"
SSTATE_SWSPEC     = "sstate:${PN}::${PV}:${PR}::${SSTATE_VERSION}:"
SSTATE_PKGNAME    = "${SSTATE_EXTRAPATH}${@generate_sstatefn(d.getVar('SSTATE_PKGSPEC', True), d.getVar('BB_TASKHASH', True), d)}"
SSTATE_PKG        = "${SSTATE_DIR}/${SSTATE_PKGNAME}"
SSTATE_EXTRAPATH   = ""
SSTATE_EXTRAPATHWILDCARD = ""
SSTATE_PATHSPEC   = "${SSTATE_DIR}/${SSTATE_EXTRAPATHWILDCARD}*/${SSTATE_PKGSPEC}"

# We don't want the sstate to depend on things like the distro string
# of the system, we let the sstate paths take care of this.
SSTATE_EXTRAPATH[vardepvalue] = ""

# For multilib rpm the allarch packagegroup files can overwrite (in theory they're identical)
SSTATE_DUPWHITELIST = "${DEPLOY_DIR_IMAGE}/ ${DEPLOY_DIR}/licenses/ ${DEPLOY_DIR_RPM}/all/"
# Avoid docbook/sgml catalog warnings for now
SSTATE_DUPWHITELIST += "${STAGING_ETCDIR_NATIVE}/sgml ${STAGING_DATADIR_NATIVE}/sgml"
# Archive the sources for many architectures in one deploy folder
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_SRC}"

SSTATE_SCAN_FILES ?= "*.la *-config *_config"
SSTATE_SCAN_CMD ?= 'find ${SSTATE_BUILDDIR} \( -name "${@"\" -o -name \"".join(d.getVar("SSTATE_SCAN_FILES", True).split())}" \) -type f'

BB_HASHFILENAME = "False ${SSTATE_PKGSPEC} ${SSTATE_SWSPEC}"

SSTATE_ARCHS = " \
    ${BUILD_ARCH} \
    ${BUILD_ARCH}_${SDK_ARCH}_${SDK_OS} \
    ${BUILD_ARCH}_${TARGET_ARCH} \
    ${SDK_ARCH}_${SDK_OS} \
    ${SDK_ARCH}_${PACKAGE_ARCH} \
    allarch \
    ${PACKAGE_ARCH} \
    ${MACHINE}"

SSTATE_MANMACH ?= "${SSTATE_PKGARCH}"

SSTATECREATEFUNCS = "sstate_hardcode_path"
SSTATEPOSTCREATEFUNCS = ""
SSTATEPREINSTFUNCS = ""
SSTATEPOSTUNPACKFUNCS = "sstate_hardcode_path_unpack"
SSTATEPOSTINSTFUNCS = ""
EXTRA_STAGING_FIXMES ?= ""
SSTATECLEANFUNCS = ""

# Check whether sstate exists for tasks that support sstate and are in the
# locked signatures file.
SIGGEN_LOCKEDSIGS_SSTATE_EXISTS_CHECK ?= 'error'

# Check whether the task's computed hash matches the task's hash in the
# locked signatures file.
SIGGEN_LOCKEDSIGS_TASKSIG_CHECK ?= "error"

# The GnuPG key ID and passphrase to use to sign sstate archives (or unset to
# not sign)
SSTATE_SIG_KEY ?= ""
SSTATE_SIG_PASSPHRASE ?= ""
# Whether to verify the GnUPG signatures when extracting sstate archives
SSTATE_VERIFY_SIG ?= "0"

python () {
    if bb.data.inherits_class('native', d):
        d.setVar('SSTATE_PKGARCH', d.getVar('BUILD_ARCH', False))
    elif bb.data.inherits_class('crosssdk', d):
        d.setVar('SSTATE_PKGARCH', d.expand("${BUILD_ARCH}_${SDK_ARCH}_${SDK_OS}"))
    elif bb.data.inherits_class('cross', d):
        d.setVar('SSTATE_PKGARCH', d.expand("${BUILD_ARCH}_${TARGET_ARCH}"))
    elif bb.data.inherits_class('nativesdk', d):
        d.setVar('SSTATE_PKGARCH', d.expand("${SDK_ARCH}_${SDK_OS}"))
    elif bb.data.inherits_class('cross-canadian', d):
        d.setVar('SSTATE_PKGARCH', d.expand("${SDK_ARCH}_${PACKAGE_ARCH}"))
    elif bb.data.inherits_class('allarch', d) and d.getVar("PACKAGE_ARCH", True) == "all":
        d.setVar('SSTATE_PKGARCH', "allarch")
    else:
        d.setVar('SSTATE_MANMACH', d.expand("${PACKAGE_ARCH}"))

    if bb.data.inherits_class('native', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross', d):
        d.setVar('SSTATE_EXTRAPATH', "${NATIVELSBSTRING}/")
        d.setVar('BB_HASHFILENAME', "True ${SSTATE_PKGSPEC} ${SSTATE_SWSPEC}")
        d.setVar('SSTATE_EXTRAPATHWILDCARD', "*/")

    # These classes encode staging paths into their scripts data so can only be
    # reused if we manipulate the paths
    if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross', d) or bb.data.inherits_class('sdk', d) or bb.data.inherits_class('crosssdk', d):
        scan_cmd = "grep -Irl ${STAGING_DIR} ${SSTATE_BUILDDIR}"
        d.setVar('SSTATE_SCAN_CMD', scan_cmd)

    unique_tasks = set((d.getVar('SSTATETASKS', True) or "").split())
    d.setVar('SSTATETASKS', " ".join(unique_tasks))
    for task in unique_tasks:
        d.prependVarFlag(task, 'prefuncs', "sstate_task_prefunc ")
        d.appendVarFlag(task, 'postfuncs', " sstate_task_postfunc")
}

def sstate_init(task, d):
    ss = {}
    ss['task'] = task
    ss['dirs'] = []
    ss['plaindirs'] = []
    ss['lockfiles'] = []
    ss['lockfiles-shared'] = []
    return ss

def sstate_state_fromvars(d, task = None):
    if task is None:
        task = d.getVar('BB_CURRENTTASK', True)
        if not task:
            bb.fatal("sstate code running without task context?!")
        task = task.replace("_setscene", "")

    if task.startswith("do_"):
        task = task[3:]
    inputs = (d.getVarFlag("do_" + task, 'sstate-inputdirs', True) or "").split()
    outputs = (d.getVarFlag("do_" + task, 'sstate-outputdirs', True) or "").split()
    plaindirs = (d.getVarFlag("do_" + task, 'sstate-plaindirs', True) or "").split()
    lockfiles = (d.getVarFlag("do_" + task, 'sstate-lockfile', True) or "").split()
    lockfilesshared = (d.getVarFlag("do_" + task, 'sstate-lockfile-shared', True) or "").split()
    interceptfuncs = (d.getVarFlag("do_" + task, 'sstate-interceptfuncs', True) or "").split()
    if not task or len(inputs) != len(outputs):
        bb.fatal("sstate variables not setup correctly?!")

    if task == "populate_lic":
        d.setVar("SSTATE_PKGSPEC", "${SSTATE_SWSPEC}")
        d.setVar("SSTATE_EXTRAPATH", "")
        d.setVar('SSTATE_EXTRAPATHWILDCARD', "")

    ss = sstate_init(task, d)
    for i in range(len(inputs)):
        sstate_add(ss, inputs[i], outputs[i], d)
    ss['lockfiles'] = lockfiles
    ss['lockfiles-shared'] = lockfilesshared
    ss['plaindirs'] = plaindirs
    ss['interceptfuncs'] = interceptfuncs
    return ss

def sstate_add(ss, source, dest, d):
    if not source.endswith("/"):
         source = source + "/"
    if not dest.endswith("/"):
         dest = dest + "/"
    source = os.path.normpath(source)
    dest = os.path.normpath(dest)
    srcbase = os.path.basename(source)
    ss['dirs'].append([srcbase, source, dest])
    return ss

def sstate_install(ss, d):
    import oe.path
    import oe.sstatesig
    import subprocess

    sharedfiles = []
    shareddirs = []
    bb.utils.mkdirhier(d.expand("${SSTATE_MANIFESTS}"))

    sstateinst = d.expand("${WORKDIR}/sstate-install-%s/" % ss['task'])

    manifest, d2 = oe.sstatesig.sstate_get_manifest_filename(ss['task'], d)

    if os.access(manifest, os.R_OK):
        bb.fatal("Package already staged (%s)?!" % manifest)

    locks = []
    for lock in ss['lockfiles-shared']:
        locks.append(bb.utils.lockfile(lock, True))
    for lock in ss['lockfiles']:
        locks.append(bb.utils.lockfile(lock))

    for state in ss['dirs']:
        bb.debug(2, "Staging files from %s to %s" % (state[1], state[2]))
        for walkroot, dirs, files in os.walk(state[1]):
            for file in files:
                srcpath = os.path.join(walkroot, file)
                dstpath = srcpath.replace(state[1], state[2])
                #bb.debug(2, "Staging %s to %s" % (srcpath, dstpath))
                sharedfiles.append(dstpath)
            for dir in dirs:
                srcdir = os.path.join(walkroot, dir)
                dstdir = srcdir.replace(state[1], state[2])
                #bb.debug(2, "Staging %s to %s" % (srcdir, dstdir))
                if not dstdir.endswith("/"):
                    dstdir = dstdir + "/"
                shareddirs.append(dstdir)

    # Check the file list for conflicts against files which already exist
    whitelist = (d.getVar("SSTATE_DUPWHITELIST", True) or "").split()
    match = []
    for f in sharedfiles:
        if os.path.exists(f):
            f = os.path.normpath(f)
            realmatch = True
            for w in whitelist:
                if f.startswith(w):
                    realmatch = False
                    break
            if realmatch:
                match.append(f)
                sstate_search_cmd = "grep -rl '%s' %s --exclude=master.list | sed -e 's:^.*/::' -e 's:\.populate-sysroot::'" % (f, d.expand("${SSTATE_MANIFESTS}"))
                search_output = subprocess.Popen(sstate_search_cmd, shell=True, stdout=subprocess.PIPE).communicate()[0]
                if search_output != "":
                    match.append("Matched in %s" % search_output.rstrip())
    if match:
        bb.error("The recipe %s is trying to install files into a shared " \
          "area when those files already exist. Those files and their manifest " \
          "location are:\n   %s\nPlease verify which recipe should provide the " \
          "above files.\nThe build has stopped as continuing in this scenario WILL " \
          "break things, if not now, possibly in the future (we've seen builds fail " \
          "several months later). If the system knew how to recover from this " \
          "automatically it would however there are several different scenarios " \
          "which can result in this and we don't know which one this is. It may be " \
          "you have switched providers of something like virtual/kernel (e.g. from " \
          "linux-yocto to linux-yocto-dev), in that case you need to execute the " \
          "clean task for both recipes and it will resolve this error. It may be " \
          "you changed DISTRO_FEATURES from systemd to udev or vice versa. Cleaning " \
          "those recipes should again resolve this error however switching " \
          "DISTRO_FEATURES on an existing build directory is not supported, you " \
          "should really clean out tmp and rebuild (reusing sstate should be safe). " \
          "It could be the overlapping files detected are harmless in which case " \
          "adding them to SSTATE_DUPWHITELIST may be the correct solution. It could " \
          "also be your build is including two different conflicting versions of " \
          "things (e.g. bluez 4 and bluez 5 and the correct solution for that would " \
          "be to resolve the conflict. If in doubt, please ask on the mailing list, " \
          "sharing the error and filelist above." % \
          (d.getVar('PN', True), "\n ".join(match)))
        bb.fatal("If the above message is too much, the simpler version is you're advised to wipe out tmp and rebuild (reusing sstate is fine). That will likely fix things in most (but not all) cases.")

    # Write out the manifest
    f = open(manifest, "w")
    for file in sharedfiles:
        f.write(file + "\n")

    # We want to ensure that directories appear at the end of the manifest
    # so that when we test to see if they should be deleted any contents
    # added by the task will have been removed first.
    dirs = sorted(shareddirs, key=len)
    # Must remove children first, which will have a longer path than the parent
    for di in reversed(dirs):
        f.write(di + "\n")
    f.close()

    # Append to the list of manifests for this PACKAGE_ARCH

    i = d2.expand("${SSTATE_MANIFESTS}/index-${SSTATE_MANMACH}")
    l = bb.utils.lockfile(i + ".lock")
    filedata = d.getVar("STAMP", True) + " " + d2.getVar("SSTATE_MANFILEPREFIX", True) + " " + d.getVar("WORKDIR", True) + "\n"
    manifests = []
    if os.path.exists(i):
        with open(i, "r") as f:
            manifests = f.readlines()
    if filedata not in manifests:
        with open(i, "a+") as f:
            f.write(filedata)
    bb.utils.unlockfile(l)

    # Run the actual file install
    for state in ss['dirs']:
        if os.path.exists(state[1]):
            oe.path.copyhardlinktree(state[1], state[2])

    for postinst in (d.getVar('SSTATEPOSTINSTFUNCS', True) or '').split():
        # All hooks should run in the SSTATE_INSTDIR
        bb.build.exec_func(postinst, d, (sstateinst,))

    for lock in locks:
        bb.utils.unlockfile(lock)

sstate_install[vardepsexclude] += "SSTATE_DUPWHITELIST STATE_MANMACH SSTATE_MANFILEPREFIX"
sstate_install[vardeps] += "${SSTATEPOSTINSTFUNCS}"

def sstate_installpkg(ss, d):
    import oe.path
    import subprocess
    from oe.gpg_sign import get_signer

    def prepdir(dir):
        # remove dir if it exists, ensure any parent directories do exist
        if os.path.exists(dir):
            oe.path.remove(dir)
        bb.utils.mkdirhier(dir)
        oe.path.remove(dir)

    sstateinst = d.expand("${WORKDIR}/sstate-install-%s/" % ss['task'])
    sstatefetch = d.getVar('SSTATE_PKGNAME', True) + '_' + ss['task'] + ".tgz"
    sstatepkg = d.getVar('SSTATE_PKG', True) + '_' + ss['task'] + ".tgz"

    if not os.path.exists(sstatepkg):
        pstaging_fetch(sstatefetch, sstatepkg, d)

    if not os.path.isfile(sstatepkg):
        bb.note("Staging package %s does not exist" % sstatepkg)
        return False

    sstate_clean(ss, d)

    d.setVar('SSTATE_INSTDIR', sstateinst)
    d.setVar('SSTATE_PKG', sstatepkg)

    if bb.utils.to_boolean(d.getVar("SSTATE_VERIFY_SIG", True), False):
        signer = get_signer(d, 'local')
        if not signer.verify(sstatepkg + '.sig'):
            bb.warn("Cannot verify signature on sstate package %s" % sstatepkg)

    for f in (d.getVar('SSTATEPREINSTFUNCS', True) or '').split() + ['sstate_unpack_package'] + (d.getVar('SSTATEPOSTUNPACKFUNCS', True) or '').split():
        # All hooks should run in the SSTATE_INSTDIR
        bb.build.exec_func(f, d, (sstateinst,))

    for state in ss['dirs']:
        prepdir(state[1])
        os.rename(sstateinst + state[0], state[1])
    sstate_install(ss, d)

    for plain in ss['plaindirs']:
        workdir = d.getVar('WORKDIR', True)
        src = sstateinst + "/" + plain.replace(workdir, '')
        dest = plain
        bb.utils.mkdirhier(src)
        prepdir(dest)
        os.rename(src, dest)

    return True

python sstate_hardcode_path_unpack () {
    # Fixup hardcoded paths
    #
    # Note: The logic below must match the reverse logic in
    # sstate_hardcode_path(d)
    import subprocess

    sstateinst = d.getVar('SSTATE_INSTDIR', True)
    fixmefn =  sstateinst + "fixmepath"
    if os.path.isfile(fixmefn):
        staging = d.getVar('STAGING_DIR', True)
        staging_target = d.getVar('STAGING_DIR_TARGET', True)
        staging_host = d.getVar('STAGING_DIR_HOST', True)

        if bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross-canadian', d):
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIR:%s:g'" % (staging)
        elif bb.data.inherits_class('cross', d):
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIRTARGET:%s:g; s:FIXMESTAGINGDIR:%s:g'" % (staging_target, staging)
        else:
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIRHOST:%s:g'" % (staging_host)

        extra_staging_fixmes = d.getVar('EXTRA_STAGING_FIXMES', True) or ''
        for fixmevar in extra_staging_fixmes.split():
            fixme_path = d.getVar(fixmevar, True)
            sstate_sed_cmd += " -e 's:FIXME_%s:%s:g'" % (fixmevar, fixme_path)

        # Add sstateinst to each filename in fixmepath, use xargs to efficiently call sed
        sstate_hardcode_cmd = "sed -e 's:^:%s:g' %s | xargs %s" % (sstateinst, fixmefn, sstate_sed_cmd)

        bb.note("Replacing fixme paths in sstate package: %s" % (sstate_hardcode_cmd))
        subprocess.call(sstate_hardcode_cmd, shell=True)

        # Need to remove this or we'd copy it into the target directory and may 
        # conflict with another writer
        os.remove(fixmefn)
}

def sstate_clean_cachefile(ss, d):
    import oe.path

    sstatepkgfile = d.getVar('SSTATE_PATHSPEC', True) + "*_" + ss['task'] + ".tgz*"
    bb.note("Removing %s" % sstatepkgfile)
    oe.path.remove(sstatepkgfile)

def sstate_clean_cachefiles(d):
    for task in (d.getVar('SSTATETASKS', True) or "").split():
        ld = d.createCopy()
        ss = sstate_state_fromvars(ld, task)
        sstate_clean_cachefile(ss, ld)

def sstate_clean_manifest(manifest, d):
    import oe.path

    mfile = open(manifest)
    entries = mfile.readlines()
    mfile.close()

    for entry in entries:
        entry = entry.strip()
        bb.debug(2, "Removing manifest: %s" % entry)
        # We can race against another package populating directories as we're removing them
        # so we ignore errors here.
        try:
            if entry.endswith("/"):
                if os.path.islink(entry[:-1]):
                    os.remove(entry[:-1])
                elif os.path.exists(entry) and len(os.listdir(entry)) == 0:
                    os.rmdir(entry[:-1])
            else:
                oe.path.remove(entry)
        except OSError:
            pass

    oe.path.remove(manifest)

def sstate_clean(ss, d):
    import oe.path
    import glob

    d2 = d.createCopy()
    stamp_clean = d.getVar("STAMPCLEAN", True)
    extrainf = d.getVarFlag("do_" + ss['task'], 'stamp-extra-info', True)
    if extrainf:
        d2.setVar("SSTATE_MANMACH", extrainf)
        wildcard_stfile = "%s.do_%s*.%s" % (stamp_clean, ss['task'], extrainf)
    else:
        wildcard_stfile = "%s.do_%s*" % (stamp_clean, ss['task'])

    manifest = d2.expand("${SSTATE_MANFILEPREFIX}.%s" % ss['task'])

    if os.path.exists(manifest):
        locks = []
        for lock in ss['lockfiles-shared']:
            locks.append(bb.utils.lockfile(lock))
        for lock in ss['lockfiles']:
            locks.append(bb.utils.lockfile(lock))

        sstate_clean_manifest(manifest, d)

        for lock in locks:
            bb.utils.unlockfile(lock)

    # Remove the current and previous stamps, but keep the sigdata.
    #
    # The glob() matches do_task* which may match multiple tasks, for
    # example: do_package and do_package_write_ipk, so we need to
    # exactly match *.do_task.* and *.do_task_setscene.*
    rm_stamp = '.do_%s.' % ss['task']
    rm_setscene = '.do_%s_setscene.' % ss['task']
    # For BB_SIGNATURE_HANDLER = "noop"
    rm_nohash = ".do_%s" % ss['task']
    for stfile in glob.glob(wildcard_stfile):
        # Keep the sigdata
        if ".sigdata." in stfile:
            continue
        # Preserve taint files in the stamps directory
        if stfile.endswith('.taint'):
            continue
        if rm_stamp in stfile or rm_setscene in stfile or \
                stfile.endswith(rm_nohash):
            oe.path.remove(stfile)

    # Removes the users/groups created by the package
    for cleanfunc in (d.getVar('SSTATECLEANFUNCS', True) or '').split():
        bb.build.exec_func(cleanfunc, d)

sstate_clean[vardepsexclude] = "SSTATE_MANFILEPREFIX"

CLEANFUNCS += "sstate_cleanall"

python sstate_cleanall() {
    bb.note("Removing shared state for package %s" % d.getVar('PN', True))

    manifest_dir = d.getVar('SSTATE_MANIFESTS', True)
    if not os.path.exists(manifest_dir):
        return

    tasks = d.getVar('SSTATETASKS', True).split()
    for name in tasks:
        ld = d.createCopy()
        shared_state = sstate_state_fromvars(ld, name)
        sstate_clean(shared_state, ld)
}

python sstate_hardcode_path () {
    import subprocess, platform

    # Need to remove hardcoded paths and fix these when we install the
    # staging packages.
    #
    # Note: the logic in this function needs to match the reverse logic
    # in sstate_installpkg(ss, d)

    staging = d.getVar('STAGING_DIR', True)
    staging_target = d.getVar('STAGING_DIR_TARGET', True)
    staging_host = d.getVar('STAGING_DIR_HOST', True)
    sstate_builddir = d.getVar('SSTATE_BUILDDIR', True)

    if bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross-canadian', d):
        sstate_grep_cmd = "grep -l -e '%s'" % (staging)
        sstate_sed_cmd = "sed -i -e 's:%s:FIXMESTAGINGDIR:g'" % (staging)
    elif bb.data.inherits_class('cross', d):
        sstate_grep_cmd = "grep -l -e '%s' -e '%s'" % (staging_target, staging)
        sstate_sed_cmd = "sed -i -e 's:%s:FIXMESTAGINGDIRTARGET:g; s:%s:FIXMESTAGINGDIR:g'" % (staging_target, staging)
    else:
        sstate_grep_cmd = "grep -l -e '%s'" % (staging_host)
        sstate_sed_cmd = "sed -i -e 's:%s:FIXMESTAGINGDIRHOST:g'" % (staging_host)

    extra_staging_fixmes = d.getVar('EXTRA_STAGING_FIXMES', True) or ''
    for fixmevar in extra_staging_fixmes.split():
        fixme_path = d.getVar(fixmevar, True)
        sstate_sed_cmd += " -e 's:%s:FIXME_%s:g'" % (fixme_path, fixmevar)

    fixmefn =  sstate_builddir + "fixmepath"

    sstate_scan_cmd = d.getVar('SSTATE_SCAN_CMD', True)
    sstate_filelist_cmd = "tee %s" % (fixmefn)

    # fixmepath file needs relative paths, drop sstate_builddir prefix
    sstate_filelist_relative_cmd = "sed -i -e 's:^%s::g' %s" % (sstate_builddir, fixmefn)

    xargs_no_empty_run_cmd = '--no-run-if-empty'
    if platform.system() == 'Darwin':
        xargs_no_empty_run_cmd = ''

    # Limit the fixpaths and sed operations based on the initial grep search
    # This has the side effect of making sure the vfs cache is hot
    sstate_hardcode_cmd = "%s | xargs %s | %s | xargs %s %s" % (sstate_scan_cmd, sstate_grep_cmd, sstate_filelist_cmd, xargs_no_empty_run_cmd, sstate_sed_cmd)

    bb.note("Removing hardcoded paths from sstate package: '%s'" % (sstate_hardcode_cmd))
    subprocess.call(sstate_hardcode_cmd, shell=True)

        # If the fixmefn is empty, remove it..
    if os.stat(fixmefn).st_size == 0:
        os.remove(fixmefn)
    else:
        bb.note("Replacing absolute paths in fixmepath file: '%s'" % (sstate_filelist_relative_cmd))
        subprocess.call(sstate_filelist_relative_cmd, shell=True)
}

def sstate_package(ss, d):
    import oe.path

    def make_relative_symlink(path, outputpath, d):
        # Replace out absolute TMPDIR paths in symlinks with relative ones
        if not os.path.islink(path):
            return
        link = os.readlink(path)
        if not os.path.isabs(link):
            return
        if not link.startswith(tmpdir):
            return

        depth = outputpath.rpartition(tmpdir)[2].count('/')
        base = link.partition(tmpdir)[2].strip()
        while depth > 1:
            base = "/.." + base
            depth -= 1
        base = "." + base

        bb.debug(2, "Replacing absolute path %s with relative path %s for %s" % (link, base, outputpath))
        os.remove(path)
        os.symlink(base, path)

    tmpdir = d.getVar('TMPDIR', True)

    sstatebuild = d.expand("${WORKDIR}/sstate-build-%s/" % ss['task'])
    sstatepkg = d.getVar('SSTATE_PKG', True) + '_'+ ss['task'] + ".tgz"
    bb.utils.remove(sstatebuild, recurse=True)
    bb.utils.mkdirhier(sstatebuild)
    bb.utils.mkdirhier(os.path.dirname(sstatepkg))
    for state in ss['dirs']:
        if not os.path.exists(state[1]):
            continue
        srcbase = state[0].rstrip("/").rsplit('/', 1)[0]
        for walkroot, dirs, files in os.walk(state[1]):
            for file in files:
                srcpath = os.path.join(walkroot, file)
                dstpath = srcpath.replace(state[1], state[2])
                make_relative_symlink(srcpath, dstpath, d)
            for dir in dirs:
                srcpath = os.path.join(walkroot, dir)
                dstpath = srcpath.replace(state[1], state[2])
                make_relative_symlink(srcpath, dstpath, d)
        bb.debug(2, "Preparing tree %s for packaging at %s" % (state[1], sstatebuild + state[0]))
        oe.path.copyhardlinktree(state[1], sstatebuild + state[0])

    workdir = d.getVar('WORKDIR', True)
    for plain in ss['plaindirs']:
        pdir = plain.replace(workdir, sstatebuild)
        bb.utils.mkdirhier(plain)
        bb.utils.mkdirhier(pdir)
        oe.path.copyhardlinktree(plain, pdir)

    d.setVar('SSTATE_BUILDDIR', sstatebuild)
    d.setVar('SSTATE_PKG', sstatepkg)

    for f in (d.getVar('SSTATECREATEFUNCS', True) or '').split() + \
             ['sstate_create_package', 'sstate_sign_package'] + \
             (d.getVar('SSTATEPOSTCREATEFUNCS', True) or '').split():
        # All hooks should run in SSTATE_BUILDDIR.
        bb.build.exec_func(f, d, (sstatebuild,))

    bb.siggen.dump_this_task(sstatepkg + ".siginfo", d)

    return

def pstaging_fetch(sstatefetch, sstatepkg, d):
    import bb.fetch2

    # Only try and fetch if the user has configured a mirror
    mirrors = d.getVar('SSTATE_MIRRORS', True)
    if not mirrors:
        return

    # Copy the data object and override DL_DIR and SRC_URI
    localdata = bb.data.createCopy(d)
    bb.data.update_data(localdata)

    dldir = localdata.expand("${SSTATE_DIR}")
    bb.utils.mkdirhier(dldir)

    localdata.delVar('MIRRORS')
    localdata.setVar('FILESPATH', dldir)
    localdata.setVar('DL_DIR', dldir)
    localdata.setVar('PREMIRRORS', mirrors)

    # if BB_NO_NETWORK is set but we also have SSTATE_MIRROR_ALLOW_NETWORK,
    # we'll want to allow network access for the current set of fetches.
    if localdata.getVar('BB_NO_NETWORK', True) == "1" and localdata.getVar('SSTATE_MIRROR_ALLOW_NETWORK', True) == "1":
        localdata.delVar('BB_NO_NETWORK')

    # Try a fetch from the sstate mirror, if it fails just return and
    # we will build the package
    uris = ['file://{0}'.format(sstatefetch),
            'file://{0}.siginfo'.format(sstatefetch)]
    if bb.utils.to_boolean(d.getVar("SSTATE_VERIFY_SIG", True), False):
        uris += ['file://{0}.sig'.format(sstatefetch)]

    for srcuri in uris:
        localdata.setVar('SRC_URI', srcuri)
        try:
            fetcher = bb.fetch2.Fetch([srcuri], localdata, cache=False)
            fetcher.download()

            # Need to optimise this, if using file:// urls, the fetcher just changes the local path
            # For now work around by symlinking
            localpath = bb.data.expand(fetcher.localpath(srcuri), localdata)
            if localpath != sstatepkg and os.path.exists(localpath) and not os.path.exists(sstatepkg):
                os.symlink(localpath, sstatepkg)

        except bb.fetch2.BBFetchException:
            break

def sstate_setscene(d):
    shared_state = sstate_state_fromvars(d)
    accelerate = sstate_installpkg(shared_state, d)
    if not accelerate:
        raise bb.build.FuncFailed("No suitable staging package found")

python sstate_task_prefunc () {
    shared_state = sstate_state_fromvars(d)
    sstate_clean(shared_state, d)
}
sstate_task_prefunc[dirs] = "${WORKDIR}"

python sstate_task_postfunc () {
    shared_state = sstate_state_fromvars(d)

    sstate_install(shared_state, d)
    for intercept in shared_state['interceptfuncs']:
        bb.build.exec_func(intercept, d, (d.getVar("WORKDIR", True),))
    omask = os.umask(002)
    if omask != 002:
       bb.note("Using umask 002 (not %0o) for sstate packaging" % omask)
    sstate_package(shared_state, d)
    os.umask(omask)
}
sstate_task_postfunc[dirs] = "${WORKDIR}"


#
# Shell function to generate a sstate package from a directory
# set as SSTATE_BUILDDIR. Will be run from within SSTATE_BUILDDIR.
#
sstate_create_package () {
	TFILE=`mktemp ${SSTATE_PKG}.XXXXXXXX`
	# Need to handle empty directories
	if [ "$(ls -A)" ]; then
		set +e
		tar -czf $TFILE *
		ret=$?
		if [ $ret -ne 0 ] && [ $ret -ne 1 ]; then
			exit 1
		fi
		set -e
	else
		tar -cz --file=$TFILE --files-from=/dev/null
	fi
	chmod 0664 $TFILE
	mv -f $TFILE ${SSTATE_PKG}

	cd ${WORKDIR}
	rm -rf ${SSTATE_BUILDDIR}
}

python sstate_sign_package () {
    from oe.gpg_sign import get_signer

    if d.getVar('SSTATE_SIG_KEY', True):
        signer = get_signer(d, 'local')
        sstate_pkg = d.getVar('SSTATE_PKG', True)
        if os.path.exists(sstate_pkg + '.sig'):
            os.unlink(sstate_pkg + '.sig')
        signer.detach_sign(sstate_pkg, d.getVar('SSTATE_SIG_KEY', False), None,
                           d.getVar('SSTATE_SIG_PASSPHRASE', True), armor=False)
}

#
# Shell function to decompress and prepare a package for installation
# Will be run from within SSTATE_INSTDIR.
#
sstate_unpack_package () {
	tar -xvzf ${SSTATE_PKG}
	# Use "! -w ||" to return true for read only files
	[ ! -w ${SSTATE_PKG} ] || touch --no-dereference ${SSTATE_PKG}
	[ ! -w ${SSTATE_PKG}.sig ] || [ ! -e ${SSTATE_PKG}.sig ] || touch --no-dereference ${SSTATE_PKG}.sig
	[ ! -w ${SSTATE_PKG}.siginfo ] || [ ! -e ${SSTATE_PKG}.siginfo ] || touch --no-dereference ${SSTATE_PKG}.siginfo
}

BB_HASHCHECK_FUNCTION = "sstate_checkhashes"

def sstate_checkhashes(sq_fn, sq_task, sq_hash, sq_hashfn, d, siginfo=False):

    ret = []
    missed = []
    extension = ".tgz"
    if siginfo:
        extension = extension + ".siginfo"

    def getpathcomponents(task, d):
        # Magic data from BB_HASHFILENAME
        splithashfn = sq_hashfn[task].split(" ")
        spec = splithashfn[1]
        if splithashfn[0] == "True":
            extrapath = d.getVar("NATIVELSBSTRING", True) + "/"
        else:
            extrapath = ""

        tname = sq_task[task][3:]

        if tname in ["fetch", "unpack", "patch", "populate_lic", "preconfigure"] and splithashfn[2]:
            spec = splithashfn[2]
            extrapath = ""

        return spec, extrapath, tname


    for task in range(len(sq_fn)):

        spec, extrapath, tname = getpathcomponents(task, d)

        sstatefile = d.expand("${SSTATE_DIR}/" + extrapath + generate_sstatefn(spec, sq_hash[task], d) + "_" + tname + extension)

        if os.path.exists(sstatefile):
            bb.debug(2, "SState: Found valid sstate file %s" % sstatefile)
            ret.append(task)
            continue
        else:
            missed.append(task)
            bb.debug(2, "SState: Looked for but didn't find file %s" % sstatefile)

    mirrors = d.getVar("SSTATE_MIRRORS", True)
    if mirrors:
        # Copy the data object and override DL_DIR and SRC_URI
        localdata = bb.data.createCopy(d)
        bb.data.update_data(localdata)

        dldir = localdata.expand("${SSTATE_DIR}")
        localdata.delVar('MIRRORS')
        localdata.setVar('FILESPATH', dldir)
        localdata.setVar('DL_DIR', dldir)
        localdata.setVar('PREMIRRORS', mirrors)

        bb.debug(2, "SState using premirror of: %s" % mirrors)

        # if BB_NO_NETWORK is set but we also have SSTATE_MIRROR_ALLOW_NETWORK,
        # we'll want to allow network access for the current set of fetches.
        if localdata.getVar('BB_NO_NETWORK', True) == "1" and localdata.getVar('SSTATE_MIRROR_ALLOW_NETWORK', True) == "1":
            localdata.delVar('BB_NO_NETWORK')

        from bb.fetch2 import FetchConnectionCache
        def checkstatus_init(thread_worker):
            thread_worker.connection_cache = FetchConnectionCache()

        def checkstatus_end(thread_worker):
            thread_worker.connection_cache.close_connections()

        def checkstatus(thread_worker, arg):
            (task, sstatefile) = arg

            localdata2 = bb.data.createCopy(localdata)
            srcuri = "file://" + sstatefile
            localdata.setVar('SRC_URI', srcuri)
            bb.debug(2, "SState: Attempting to fetch %s" % srcuri)

            try:
                fetcher = bb.fetch2.Fetch(srcuri.split(), localdata2,
                            connection_cache=thread_worker.connection_cache)
                fetcher.checkstatus()
                bb.debug(2, "SState: Successful fetch test for %s" % srcuri)
                ret.append(task)
                if task in missed:
                    missed.remove(task)
            except:
                missed.append(task)
                bb.debug(2, "SState: Unsuccessful fetch test for %s" % srcuri)
                pass     

        tasklist = []
        for task in range(len(sq_fn)):
            if task in ret:
                continue
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, sq_hash[task], d) + "_" + tname + extension)
            tasklist.append((task, sstatefile))

        if tasklist:
            bb.note("Checking sstate mirror object availability (for %s objects)" % len(tasklist))
            import multiprocessing
            nproc = min(multiprocessing.cpu_count(), len(tasklist))

            pool = oe.utils.ThreadedPool(nproc, len(tasklist),
                    worker_init=checkstatus_init, worker_end=checkstatus_end)
            for t in tasklist:
                pool.add_task(checkstatus, t)
            pool.start()
            pool.wait_completion()

    inheritlist = d.getVar("INHERIT", True)
    if "toaster" in inheritlist:
        evdata = {'missed': [], 'found': []};
        for task in missed:
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, sq_hash[task], d) + "_" + tname + ".tgz")
            evdata['missed'].append( (sq_fn[task], sq_task[task], sq_hash[task], sstatefile ) )
        for task in ret:
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, sq_hash[task], d) + "_" + tname + ".tgz")
            evdata['found'].append( (sq_fn[task], sq_task[task], sq_hash[task], sstatefile ) )
        bb.event.fire(bb.event.MetadataEvent("MissedSstate", evdata), d)

    if hasattr(bb.parse.siggen, "checkhashes"):
        bb.parse.siggen.checkhashes(missed, ret, sq_fn, sq_task, sq_hash, sq_hashfn, d)

    return ret

BB_SETSCENE_DEPVALID = "setscene_depvalid"

def setscene_depvalid(task, taskdependees, notneeded, d):
    # taskdependees is a dict of tasks which depend on task, each being a 3 item list of [PN, TASKNAME, FILENAME]
    # task is included in taskdependees too

    bb.debug(2, "Considering setscene task: %s" % (str(taskdependees[task])))

    def isNativeCross(x):
        return x.endswith("-native") or "-cross-" in x or "-crosssdk" in x

    def isPostInstDep(x):
        if x in ["qemu-native", "gdk-pixbuf-native", "qemuwrapper-cross", "depmodwrapper-cross", "systemd-systemctl-native", "gtk-icon-utils-native", "ca-certificates-native"]:
            return True
        return False

    # We only need to trigger populate_lic through direct dependencies
    if taskdependees[task][1] == "do_populate_lic":
        return True

    # We only need to trigger packagedata through direct dependencies
    # but need to preserve packagedata on packagedata links
    if taskdependees[task][1] == "do_packagedata":
        for dep in taskdependees:
            if taskdependees[dep][1] == "do_packagedata":
                return False
        return True

    for dep in taskdependees:
        bb.debug(2, "  considering dependency: %s" % (str(taskdependees[dep])))
        if task == dep:
            continue
        if dep in notneeded:
            continue
        # do_package_write_* and do_package doesn't need do_package
        if taskdependees[task][1] == "do_package" and taskdependees[dep][1] in ['do_package', 'do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package_qa']:
            continue
        # do_package_write_* and do_package doesn't need do_populate_sysroot, unless is a postinstall dependency
        if taskdependees[task][1] == "do_populate_sysroot" and taskdependees[dep][1] in ['do_package', 'do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package_qa']:
            if isPostInstDep(taskdependees[task][0]) and taskdependees[dep][1] in ['do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm']:
                return False
            continue
        # Native/Cross packages don't exist and are noexec anyway
        if isNativeCross(taskdependees[dep][0]) and taskdependees[dep][1] in ['do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package', 'do_package_qa']:
            continue

        # This is due to the [depends] in useradd.bbclass complicating matters
        # The logic *is* reversed here due to the way hard setscene dependencies are injected
        if (taskdependees[task][1] == 'do_package' or taskdependees[task][1] == 'do_populate_sysroot') and taskdependees[dep][0].endswith(('shadow-native', 'shadow-sysroot', 'base-passwd', 'pseudo-native')) and taskdependees[dep][1] == 'do_populate_sysroot':
            continue

        # Consider sysroot depending on sysroot tasks
        if taskdependees[task][1] == 'do_populate_sysroot' and taskdependees[dep][1] == 'do_populate_sysroot':
            # base-passwd/shadow-sysroot don't need their dependencies
            if taskdependees[dep][0].endswith(("base-passwd", "shadow-sysroot")):
                continue
            # Nothing need depend on libc-initial/gcc-cross-initial
            if "-initial" in taskdependees[task][0]:
                continue
            # Native/Cross populate_sysroot need their dependencies
            if isNativeCross(taskdependees[task][0]) and isNativeCross(taskdependees[dep][0]):
                return False
            # Target populate_sysroot depended on by cross tools need to be installed
            if isNativeCross(taskdependees[dep][0]):
                return False
            # Native/cross tools depended upon by target sysroot are not needed
            if isNativeCross(taskdependees[task][0]):
                continue
            # Target populate_sysroot need their dependencies
            return False

        if taskdependees[task][1] == 'do_shared_workdir':
            continue

        if taskdependees[dep][1] == "do_populate_lic":
            continue


        # Safe fallthrough default
        bb.debug(2, " Default setscene dependency fall through due to dependency: %s" % (str(taskdependees[dep])))
        return False
    return True

addhandler sstate_eventhandler
sstate_eventhandler[eventmask] = "bb.build.TaskSucceeded"
python sstate_eventhandler() {
    d = e.data
    # When we write an sstate package we rewrite the SSTATE_PKG
    spkg = d.getVar('SSTATE_PKG', True)
    if not spkg.endswith(".tgz"):
        taskname = d.getVar("BB_RUNTASK", True)[3:]
        spec = d.getVar('SSTATE_PKGSPEC', True)
        swspec = d.getVar('SSTATE_SWSPEC', True)
        if taskname in ["fetch", "unpack", "patch", "populate_lic", "preconfigure"] and swspec:
            d.setVar("SSTATE_PKGSPEC", "${SSTATE_SWSPEC}")
            d.setVar("SSTATE_EXTRAPATH", "")
        sstatepkg = d.getVar('SSTATE_PKG', True)
        bb.siggen.dump_this_task(sstatepkg + '_' + taskname + ".tgz" ".siginfo", d)
}

SSTATE_PRUNE_OBSOLETEWORKDIR = "1"

# Event handler which removes manifests and stamps file for
# recipes which are no longer reachable in a build where they
# once were.
# Also optionally removes the workdir of those tasks/recipes
#
addhandler sstate_eventhandler2
sstate_eventhandler2[eventmask] = "bb.event.ReachableStamps"
python sstate_eventhandler2() {
    import glob
    d = e.data
    stamps = e.stamps.values()
    removeworkdir = (d.getVar("SSTATE_PRUNE_OBSOLETEWORKDIR", False) == "1")
    seen = []
    for a in d.getVar("SSTATE_ARCHS", True).split():
        toremove = []
        i = d.expand("${SSTATE_MANIFESTS}/index-" + a)
        if not os.path.exists(i):
            continue
        with open(i, "r") as f:
            lines = f.readlines()
            for l in lines:
                (stamp, manifest, workdir) = l.split()
                if stamp not in stamps:
                    toremove.append(l)
                    if stamp not in seen:
                        bb.debug(2, "Stamp %s is not reachable, removing related manifests" % stamp)
                        seen.append(stamp)

        if toremove:
            bb.note("There are %d recipes to be removed from sysroot %s, removing..." % (len(toremove), a))

        for r in toremove:
            (stamp, manifest, workdir) = r.split()
            for m in glob.glob(manifest + ".*"):
                sstate_clean_manifest(m, d)
            bb.utils.remove(stamp + "*")
            if removeworkdir:
                bb.utils.remove(workdir, recurse = True)
            lines.remove(r)
        with open(i, "w") as f:
            for l in lines:
                f.write(l)
}
