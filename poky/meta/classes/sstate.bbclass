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
SSTATE_PKGNAME    = "${SSTATE_EXTRAPATH}${@generate_sstatefn(d.getVar('SSTATE_PKGSPEC'), d.getVar('BB_UNIHASH'), d)}"
SSTATE_PKG        = "${SSTATE_DIR}/${SSTATE_PKGNAME}"
SSTATE_EXTRAPATH   = ""
SSTATE_EXTRAPATHWILDCARD = ""
SSTATE_PATHSPEC   = "${SSTATE_DIR}/${SSTATE_EXTRAPATHWILDCARD}*/${SSTATE_PKGSPEC}"

# explicitly make PV to depend on evaluated value of PV variable
PV[vardepvalue] = "${PV}"

# We don't want the sstate to depend on things like the distro string
# of the system, we let the sstate paths take care of this.
SSTATE_EXTRAPATH[vardepvalue] = ""
SSTATE_EXTRAPATHWILDCARD[vardepvalue] = ""

# For multilib rpm the allarch packagegroup files can overwrite (in theory they're identical)
SSTATE_DUPWHITELIST = "${DEPLOY_DIR}/licenses/"
# Avoid docbook/sgml catalog warnings for now
SSTATE_DUPWHITELIST += "${STAGING_ETCDIR_NATIVE}/sgml ${STAGING_DATADIR_NATIVE}/sgml"
# sdk-provides-dummy-nativesdk and nativesdk-buildtools-perl-dummy overlap for different SDKMACHINE
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_RPM}/sdk_provides_dummy_nativesdk/ ${DEPLOY_DIR_IPK}/sdk-provides-dummy-nativesdk/"
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_RPM}/buildtools_dummy_nativesdk/ ${DEPLOY_DIR_IPK}/buildtools-dummy-nativesdk/"
# target-sdk-provides-dummy overlaps that allarch is disabled when multilib is used
SSTATE_DUPWHITELIST += "${COMPONENTS_DIR}/sdk-provides-dummy-target/ ${DEPLOY_DIR_RPM}/sdk_provides_dummy_target/ ${DEPLOY_DIR_IPK}/sdk-provides-dummy-target/"
# Archive the sources for many architectures in one deploy folder
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_SRC}"
# ovmf/grub-efi/systemd-boot/intel-microcode multilib recipes can generate identical overlapping files
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_IMAGE}/ovmf"
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_IMAGE}/grub-efi"
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_IMAGE}/systemd-boot"
SSTATE_DUPWHITELIST += "${DEPLOY_DIR_IMAGE}/microcode"

SSTATE_SCAN_FILES ?= "*.la *-config *_config postinst-*"
SSTATE_SCAN_CMD ??= 'find ${SSTATE_BUILDDIR} \( -name "${@"\" -o -name \"".join(d.getVar("SSTATE_SCAN_FILES").split())}" \) -type f'
SSTATE_SCAN_CMD_NATIVE ??= 'grep -Irl -e ${RECIPE_SYSROOT} -e ${RECIPE_SYSROOT_NATIVE} -e ${HOSTTOOLS_DIR} ${SSTATE_BUILDDIR}'

BB_HASHFILENAME = "False ${SSTATE_PKGSPEC} ${SSTATE_SWSPEC}"

SSTATE_ARCHS = " \
    ${BUILD_ARCH} \
    ${BUILD_ARCH}_${SDK_ARCH}_${SDK_OS} \
    ${BUILD_ARCH}_${TARGET_ARCH} \
    ${SDK_ARCH}_${SDK_OS} \
    ${SDK_ARCH}_${PACKAGE_ARCH} \
    allarch \
    ${PACKAGE_ARCH} \
    ${PACKAGE_EXTRA_ARCHS} \
    ${MACHINE_ARCH}"

SSTATE_MANMACH ?= "${SSTATE_PKGARCH}"

SSTATECREATEFUNCS = "sstate_hardcode_path"
SSTATECREATEFUNCS[vardeps] = "SSTATE_SCAN_FILES"
SSTATEPOSTCREATEFUNCS = ""
SSTATEPREINSTFUNCS = ""
SSTATEPOSTUNPACKFUNCS = "sstate_hardcode_path_unpack"
SSTATEPOSTINSTFUNCS = ""
EXTRA_STAGING_FIXMES ?= "HOSTTOOLS_DIR"

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

SSTATE_HASHEQUIV_METHOD ?= "oe.sstatesig.OEOuthashBasic"
SSTATE_HASHEQUIV_METHOD[doc] = "The fully-qualified function used to calculate \
    the output hash for a task, which in turn is used to determine equivalency. \
    "

SSTATE_HASHEQUIV_SERVER ?= ""
SSTATE_HASHEQUIV_SERVER[doc] = "The hash equivalence sever. For example, \
    'http://192.168.0.1:5000'. Do not include a trailing slash \
    "

SSTATE_HASHEQUIV_REPORT_TASKDATA ?= "0"
SSTATE_HASHEQUIV_REPORT_TASKDATA[doc] = "Report additional useful data to the \
    hash equivalency server, such as PN, PV, taskname, etc. This information \
    is very useful for developers looking at task data, but may leak sensitive \
    data if the equivalence server is public. \
    "

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
    elif bb.data.inherits_class('allarch', d) and d.getVar("PACKAGE_ARCH") == "all":
        d.setVar('SSTATE_PKGARCH', "allarch")
    else:
        d.setVar('SSTATE_MANMACH', d.expand("${PACKAGE_ARCH}"))

    if bb.data.inherits_class('native', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross', d):
        d.setVar('SSTATE_EXTRAPATH', "${NATIVELSBSTRING}/")
        d.setVar('BB_HASHFILENAME', "True ${SSTATE_PKGSPEC} ${SSTATE_SWSPEC}")
        d.setVar('SSTATE_EXTRAPATHWILDCARD', "${NATIVELSBSTRING}/")

    unique_tasks = sorted(set((d.getVar('SSTATETASKS') or "").split()))
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
        task = d.getVar('BB_CURRENTTASK')
        if not task:
            bb.fatal("sstate code running without task context?!")
        task = task.replace("_setscene", "")

    if task.startswith("do_"):
        task = task[3:]
    inputs = (d.getVarFlag("do_" + task, 'sstate-inputdirs') or "").split()
    outputs = (d.getVarFlag("do_" + task, 'sstate-outputdirs') or "").split()
    plaindirs = (d.getVarFlag("do_" + task, 'sstate-plaindirs') or "").split()
    lockfiles = (d.getVarFlag("do_" + task, 'sstate-lockfile') or "").split()
    lockfilesshared = (d.getVarFlag("do_" + task, 'sstate-lockfile-shared') or "").split()
    interceptfuncs = (d.getVarFlag("do_" + task, 'sstate-interceptfuncs') or "").split()
    fixmedir = d.getVarFlag("do_" + task, 'sstate-fixmedir') or ""
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
    ss['fixmedir'] = fixmedir
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

    d.setVar("SSTATE_INST_POSTRM", manifest + ".postrm")

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
                if os.path.islink(srcdir):
                    sharedfiles.append(dstdir)
                    continue
                if not dstdir.endswith("/"):
                    dstdir = dstdir + "/"
                shareddirs.append(dstdir)

    # Check the file list for conflicts against files which already exist
    whitelist = (d.getVar("SSTATE_DUPWHITELIST") or "").split()
    match = []
    for f in sharedfiles:
        if os.path.exists(f) and not os.path.islink(f):
            f = os.path.normpath(f)
            realmatch = True
            for w in whitelist:
                w = os.path.normpath(w)
                if f.startswith(w):
                    realmatch = False
                    break
            if realmatch:
                match.append(f)
                sstate_search_cmd = "grep -rlF '%s' %s --exclude=master.list | sed -e 's:^.*/::'" % (f, d.expand("${SSTATE_MANIFESTS}"))
                search_output = subprocess.Popen(sstate_search_cmd, shell=True, stdout=subprocess.PIPE).communicate()[0]
                if search_output:
                    match.append("  (matched in %s)" % search_output.decode('utf-8').rstrip())
                else:
                    match.append("  (not matched to any task)")
    if match:
        bb.error("The recipe %s is trying to install files into a shared " \
          "area when those files already exist. Those files and their manifest " \
          "location are:\n  %s\nPlease verify which recipe should provide the " \
          "above files.\n\nThe build has stopped, as continuing in this scenario WILL " \
          "break things - if not now, possibly in the future (we've seen builds fail " \
          "several months later). If the system knew how to recover from this " \
          "automatically it would, however there are several different scenarios " \
          "which can result in this and we don't know which one this is. It may be " \
          "you have switched providers of something like virtual/kernel (e.g. from " \
          "linux-yocto to linux-yocto-dev), in that case you need to execute the " \
          "clean task for both recipes and it will resolve this error. It may be " \
          "you changed DISTRO_FEATURES from systemd to udev or vice versa. Cleaning " \
          "those recipes should again resolve this error, however switching " \
          "DISTRO_FEATURES on an existing build directory is not supported - you " \
          "should really clean out tmp and rebuild (reusing sstate should be safe). " \
          "It could be the overlapping files detected are harmless in which case " \
          "adding them to SSTATE_DUPWHITELIST may be the correct solution. It could " \
          "also be your build is including two different conflicting versions of " \
          "things (e.g. bluez 4 and bluez 5 and the correct solution for that would " \
          "be to resolve the conflict. If in doubt, please ask on the mailing list, " \
          "sharing the error and filelist above." % \
          (d.getVar('PN'), "\n  ".join(match)))
        bb.fatal("If the above message is too much, the simpler version is you're advised to wipe out tmp and rebuild (reusing sstate is fine). That will likely fix things in most (but not all) cases.")

    if ss['fixmedir'] and os.path.exists(ss['fixmedir'] + "/fixmepath.cmd"):
        sharedfiles.append(ss['fixmedir'] + "/fixmepath.cmd")
        sharedfiles.append(ss['fixmedir'] + "/fixmepath")

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
    filedata = d.getVar("STAMP") + " " + d2.getVar("SSTATE_MANFILEPREFIX") + " " + d.getVar("WORKDIR") + "\n"
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

    for postinst in (d.getVar('SSTATEPOSTINSTFUNCS') or '').split():
        # All hooks should run in the SSTATE_INSTDIR
        bb.build.exec_func(postinst, d, (sstateinst,))

    for lock in locks:
        bb.utils.unlockfile(lock)

sstate_install[vardepsexclude] += "SSTATE_DUPWHITELIST STATE_MANMACH SSTATE_MANFILEPREFIX"
sstate_install[vardeps] += "${SSTATEPOSTINSTFUNCS}"

def sstate_installpkg(ss, d):
    from oe.gpg_sign import get_signer

    sstateinst = d.expand("${WORKDIR}/sstate-install-%s/" % ss['task'])
    sstatefetch = d.getVar('SSTATE_PKGNAME') + '_' + ss['task'] + ".tgz"
    sstatepkg = d.getVar('SSTATE_PKG') + '_' + ss['task'] + ".tgz"

    if not os.path.exists(sstatepkg):
        pstaging_fetch(sstatefetch, d)

    if not os.path.isfile(sstatepkg):
        bb.note("Staging package %s does not exist" % sstatepkg)
        return False

    sstate_clean(ss, d)

    d.setVar('SSTATE_INSTDIR', sstateinst)
    d.setVar('SSTATE_PKG', sstatepkg)

    if bb.utils.to_boolean(d.getVar("SSTATE_VERIFY_SIG"), False):
        signer = get_signer(d, 'local')
        if not signer.verify(sstatepkg + '.sig'):
            bb.warn("Cannot verify signature on sstate package %s" % sstatepkg)

    # Empty sstateinst directory, ensure its clean
    if os.path.exists(sstateinst):
        oe.path.remove(sstateinst)
    bb.utils.mkdirhier(sstateinst)

    sstateinst = d.getVar("SSTATE_INSTDIR")
    d.setVar('SSTATE_FIXMEDIR', ss['fixmedir'])

    for f in (d.getVar('SSTATEPREINSTFUNCS') or '').split() + ['sstate_unpack_package']:
        # All hooks should run in the SSTATE_INSTDIR
        bb.build.exec_func(f, d, (sstateinst,))

    return sstate_installpkgdir(ss, d)

def sstate_installpkgdir(ss, d):
    import oe.path
    import subprocess

    sstateinst = d.getVar("SSTATE_INSTDIR")
    d.setVar('SSTATE_FIXMEDIR', ss['fixmedir'])

    for f in (d.getVar('SSTATEPOSTUNPACKFUNCS') or '').split():
        # All hooks should run in the SSTATE_INSTDIR
        bb.build.exec_func(f, d, (sstateinst,))

    def prepdir(dir):
        # remove dir if it exists, ensure any parent directories do exist
        if os.path.exists(dir):
            oe.path.remove(dir)
        bb.utils.mkdirhier(dir)
        oe.path.remove(dir)

    for state in ss['dirs']:
        prepdir(state[1])
        os.rename(sstateinst + state[0], state[1])
    sstate_install(ss, d)

    for plain in ss['plaindirs']:
        workdir = d.getVar('WORKDIR')
        sharedworkdir = os.path.join(d.getVar('TMPDIR'), "work-shared")
        src = sstateinst + "/" + plain.replace(workdir, '')
        if sharedworkdir in plain:
            src = sstateinst + "/" + plain.replace(sharedworkdir, '')
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

    sstateinst = d.getVar('SSTATE_INSTDIR')
    sstatefixmedir = d.getVar('SSTATE_FIXMEDIR')
    fixmefn = sstateinst + "fixmepath"
    if os.path.isfile(fixmefn):
        staging_target = d.getVar('RECIPE_SYSROOT')
        staging_host = d.getVar('RECIPE_SYSROOT_NATIVE')

        if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross-canadian', d):
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIRHOST:%s:g'" % (staging_host)
        elif bb.data.inherits_class('cross', d) or bb.data.inherits_class('crosssdk', d):
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIRTARGET:%s:g; s:FIXMESTAGINGDIRHOST:%s:g'" % (staging_target, staging_host)
        else:
            sstate_sed_cmd = "sed -i -e 's:FIXMESTAGINGDIRTARGET:%s:g'" % (staging_target)

        extra_staging_fixmes = d.getVar('EXTRA_STAGING_FIXMES') or ''
        for fixmevar in extra_staging_fixmes.split():
            fixme_path = d.getVar(fixmevar)
            sstate_sed_cmd += " -e 's:FIXME_%s:%s:g'" % (fixmevar, fixme_path)

        # Add sstateinst to each filename in fixmepath, use xargs to efficiently call sed
        sstate_hardcode_cmd = "sed -e 's:^:%s:g' %s | xargs %s" % (sstateinst, fixmefn, sstate_sed_cmd)

        # Defer do_populate_sysroot relocation command
        if sstatefixmedir:
            bb.utils.mkdirhier(sstatefixmedir)
            with open(sstatefixmedir + "/fixmepath.cmd", "w") as f:
                sstate_hardcode_cmd = sstate_hardcode_cmd.replace(fixmefn, sstatefixmedir + "/fixmepath")
                sstate_hardcode_cmd = sstate_hardcode_cmd.replace(sstateinst, "FIXMEFINALSSTATEINST")
                sstate_hardcode_cmd = sstate_hardcode_cmd.replace(staging_host, "FIXMEFINALSSTATEHOST")
                sstate_hardcode_cmd = sstate_hardcode_cmd.replace(staging_target, "FIXMEFINALSSTATETARGET")
                f.write(sstate_hardcode_cmd)
            bb.utils.copyfile(fixmefn, sstatefixmedir + "/fixmepath")
            return

        bb.note("Replacing fixme paths in sstate package: %s" % (sstate_hardcode_cmd))
        subprocess.check_call(sstate_hardcode_cmd, shell=True)

        # Need to remove this or we'd copy it into the target directory and may
        # conflict with another writer
        os.remove(fixmefn)
}

def sstate_clean_cachefile(ss, d):
    import oe.path

    sstatepkgfile = d.getVar('SSTATE_PATHSPEC') + "*_" + ss['task'] + ".tgz*"
    if d.getVarFlag('do_%s' % ss['task'], 'task'):
        bb.note("Removing %s" % sstatepkgfile)
        oe.path.remove(sstatepkgfile)

def sstate_clean_cachefiles(d):
    for task in (d.getVar('SSTATETASKS') or "").split():
        ld = d.createCopy()
        ss = sstate_state_fromvars(ld, task)
        sstate_clean_cachefile(ss, ld)

def sstate_clean_manifest(manifest, d, prefix=None):
    import oe.path

    mfile = open(manifest)
    entries = mfile.readlines()
    mfile.close()

    for entry in entries:
        entry = entry.strip()
        if prefix and not entry.startswith("/"):
            entry = prefix + "/" + entry
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
                os.remove(entry)
        except OSError:
            pass

    postrm = manifest + ".postrm"
    if os.path.exists(manifest + ".postrm"):
        import subprocess
        os.chmod(postrm, 0o755)
        subprocess.check_call(postrm, shell=True)
        oe.path.remove(postrm)

    oe.path.remove(manifest)

def sstate_clean(ss, d):
    import oe.path
    import glob

    d2 = d.createCopy()
    stamp_clean = d.getVar("STAMPCLEAN")
    extrainf = d.getVarFlag("do_" + ss['task'], 'stamp-extra-info')
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
        if ".sigdata." in stfile or ".sigbasedata." in stfile:
            continue
        # Preserve taint files in the stamps directory
        if stfile.endswith('.taint'):
            continue
        if rm_stamp in stfile or rm_setscene in stfile or \
                stfile.endswith(rm_nohash):
            oe.path.remove(stfile)

sstate_clean[vardepsexclude] = "SSTATE_MANFILEPREFIX"

CLEANFUNCS += "sstate_cleanall"

python sstate_cleanall() {
    bb.note("Removing shared state for package %s" % d.getVar('PN'))

    manifest_dir = d.getVar('SSTATE_MANIFESTS')
    if not os.path.exists(manifest_dir):
        return

    tasks = d.getVar('SSTATETASKS').split()
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

    staging_target = d.getVar('RECIPE_SYSROOT')
    staging_host = d.getVar('RECIPE_SYSROOT_NATIVE')
    sstate_builddir = d.getVar('SSTATE_BUILDDIR')

    sstate_sed_cmd = "sed -i -e 's:%s:FIXMESTAGINGDIRHOST:g'" % staging_host
    if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross-canadian', d):
        sstate_grep_cmd = "grep -l -e '%s'" % (staging_host)
    elif bb.data.inherits_class('cross', d) or bb.data.inherits_class('crosssdk', d):
        sstate_grep_cmd = "grep -l -e '%s' -e '%s'" % (staging_target, staging_host)
        sstate_sed_cmd += " -e 's:%s:FIXMESTAGINGDIRTARGET:g'" % staging_target
    else:
        sstate_grep_cmd = "grep -l -e '%s' -e '%s'" % (staging_target, staging_host)
        sstate_sed_cmd += " -e 's:%s:FIXMESTAGINGDIRTARGET:g'" % staging_target

    extra_staging_fixmes = d.getVar('EXTRA_STAGING_FIXMES') or ''
    for fixmevar in extra_staging_fixmes.split():
        fixme_path = d.getVar(fixmevar)
        sstate_sed_cmd += " -e 's:%s:FIXME_%s:g'" % (fixme_path, fixmevar)
        sstate_grep_cmd += " -e '%s'" % (fixme_path)

    fixmefn =  sstate_builddir + "fixmepath"

    sstate_scan_cmd = d.getVar('SSTATE_SCAN_CMD')
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
    subprocess.check_output(sstate_hardcode_cmd, shell=True, cwd=sstate_builddir)

        # If the fixmefn is empty, remove it..
    if os.stat(fixmefn).st_size == 0:
        os.remove(fixmefn)
    else:
        bb.note("Replacing absolute paths in fixmepath file: '%s'" % (sstate_filelist_relative_cmd))
        subprocess.check_output(sstate_filelist_relative_cmd, shell=True)
}

def sstate_package(ss, d):
    import oe.path

    tmpdir = d.getVar('TMPDIR')

    sstatebuild = d.expand("${WORKDIR}/sstate-build-%s/" % ss['task'])
    sstatepkg = d.getVar('SSTATE_PKG') + '_'+ ss['task'] + ".tgz"
    bb.utils.remove(sstatebuild, recurse=True)
    bb.utils.mkdirhier(sstatebuild)
    bb.utils.mkdirhier(os.path.dirname(sstatepkg))
    for state in ss['dirs']:
        if not os.path.exists(state[1]):
            continue
        srcbase = state[0].rstrip("/").rsplit('/', 1)[0]
        # Find and error for absolute symlinks. We could attempt to relocate but its not
        # clear where the symlink is relative to in this context. We could add that markup
        # to sstate tasks but there aren't many of these so better just avoid them entirely.
        for walkroot, dirs, files in os.walk(state[1]):
            for file in files + dirs:
                srcpath = os.path.join(walkroot, file)
                if not os.path.islink(srcpath):
                    continue
                link = os.readlink(srcpath)
                if not os.path.isabs(link):
                    continue
                if not link.startswith(tmpdir):
                    continue
                bb.error("sstate found an absolute path symlink %s pointing at %s. Please replace this with a relative link." % (srcpath, link))
        bb.debug(2, "Preparing tree %s for packaging at %s" % (state[1], sstatebuild + state[0]))
        os.rename(state[1], sstatebuild + state[0])

    workdir = d.getVar('WORKDIR')
    sharedworkdir = os.path.join(d.getVar('TMPDIR'), "work-shared")
    for plain in ss['plaindirs']:
        pdir = plain.replace(workdir, sstatebuild)
        if sharedworkdir in plain:
            pdir = plain.replace(sharedworkdir, sstatebuild)
        bb.utils.mkdirhier(plain)
        bb.utils.mkdirhier(pdir)
        os.rename(plain, pdir)

    d.setVar('SSTATE_BUILDDIR', sstatebuild)
    d.setVar('SSTATE_PKG', sstatepkg)
    d.setVar('SSTATE_INSTDIR', sstatebuild)

    if d.getVar('SSTATE_SKIP_CREATION') == '1':
        return

    for f in (d.getVar('SSTATECREATEFUNCS') or '').split() + \
             ['sstate_report_unihash', 'sstate_create_package', 'sstate_sign_package'] + \
             (d.getVar('SSTATEPOSTCREATEFUNCS') or '').split():
        # All hooks should run in SSTATE_BUILDDIR.
        bb.build.exec_func(f, d, (sstatebuild,))

    bb.siggen.dump_this_task(sstatepkg + ".siginfo", d)

    return

def pstaging_fetch(sstatefetch, d):
    import bb.fetch2

    # Only try and fetch if the user has configured a mirror
    mirrors = d.getVar('SSTATE_MIRRORS')
    if not mirrors:
        return

    # Copy the data object and override DL_DIR and SRC_URI
    localdata = bb.data.createCopy(d)

    dldir = localdata.expand("${SSTATE_DIR}")
    bb.utils.mkdirhier(dldir)

    localdata.delVar('MIRRORS')
    localdata.setVar('FILESPATH', dldir)
    localdata.setVar('DL_DIR', dldir)
    localdata.setVar('PREMIRRORS', mirrors)

    # if BB_NO_NETWORK is set but we also have SSTATE_MIRROR_ALLOW_NETWORK,
    # we'll want to allow network access for the current set of fetches.
    if bb.utils.to_boolean(localdata.getVar('BB_NO_NETWORK')) and \
            bb.utils.to_boolean(localdata.getVar('SSTATE_MIRROR_ALLOW_NETWORK')):
        localdata.delVar('BB_NO_NETWORK')

    # Try a fetch from the sstate mirror, if it fails just return and
    # we will build the package
    uris = ['file://{0};downloadfilename={0}'.format(sstatefetch),
            'file://{0}.siginfo;downloadfilename={0}.siginfo'.format(sstatefetch)]
    if bb.utils.to_boolean(d.getVar("SSTATE_VERIFY_SIG"), False):
        uris += ['file://{0}.sig;downloadfilename={0}.sig'.format(sstatefetch)]

    for srcuri in uris:
        localdata.setVar('SRC_URI', srcuri)
        try:
            fetcher = bb.fetch2.Fetch([srcuri], localdata, cache=False)
            fetcher.download()

        except bb.fetch2.BBFetchException:
            break

def sstate_setscene(d):
    shared_state = sstate_state_fromvars(d)
    accelerate = sstate_installpkg(shared_state, d)
    if not accelerate:
        bb.fatal("No suitable staging package found")

python sstate_task_prefunc () {
    shared_state = sstate_state_fromvars(d)
    sstate_clean(shared_state, d)
}
sstate_task_prefunc[dirs] = "${WORKDIR}"

python sstate_task_postfunc () {
    shared_state = sstate_state_fromvars(d)

    for intercept in shared_state['interceptfuncs']:
        bb.build.exec_func(intercept, d, (d.getVar("WORKDIR"),))

    omask = os.umask(0o002)
    if omask != 0o002:
       bb.note("Using umask 0o002 (not %0o) for sstate packaging" % omask)
    sstate_package(shared_state, d)
    os.umask(omask)

    sstateinst = d.getVar("SSTATE_INSTDIR")
    d.setVar('SSTATE_FIXMEDIR', shared_state['fixmedir'])

    sstate_installpkgdir(shared_state, d)

    bb.utils.remove(d.getVar("SSTATE_BUILDDIR"), recurse=True)
}
sstate_task_postfunc[dirs] = "${WORKDIR}"


#
# Shell function to generate a sstate package from a directory
# set as SSTATE_BUILDDIR. Will be run from within SSTATE_BUILDDIR.
#
sstate_create_package () {
	TFILE=`mktemp ${SSTATE_PKG}.XXXXXXXX`

        # Use pigz if available
        OPT="-czS"
        if [ -x "$(command -v pigz)" ]; then
            OPT="-I pigz -cS"
        fi

	# Need to handle empty directories
	if [ "$(ls -A)" ]; then
		set +e
		tar $OPT -f $TFILE *
		ret=$?
		if [ $ret -ne 0 ] && [ $ret -ne 1 ]; then
			exit 1
		fi
		set -e
	else
		tar $OPT --file=$TFILE --files-from=/dev/null
	fi
	chmod 0664 $TFILE
	mv -f $TFILE ${SSTATE_PKG}
}

python sstate_sign_package () {
    from oe.gpg_sign import get_signer

    if d.getVar('SSTATE_SIG_KEY'):
        signer = get_signer(d, 'local')
        sstate_pkg = d.getVar('SSTATE_PKG')
        if os.path.exists(sstate_pkg + '.sig'):
            os.unlink(sstate_pkg + '.sig')
        signer.detach_sign(sstate_pkg, d.getVar('SSTATE_SIG_KEY', False), None,
                           d.getVar('SSTATE_SIG_PASSPHRASE'), armor=False)
}

python sstate_report_unihash() {
    report_unihash = getattr(bb.parse.siggen, 'report_unihash', None)

    if report_unihash:
        ss = sstate_state_fromvars(d)
        report_unihash(os.getcwd(), ss['task'], d)
}

#
# Shell function to decompress and prepare a package for installation
# Will be run from within SSTATE_INSTDIR.
#
sstate_unpack_package () {
	tar -xvzf ${SSTATE_PKG}
	# update .siginfo atime on local/NFS mirror
	[ -w ${SSTATE_PKG}.siginfo ] && [ -h ${SSTATE_PKG}.siginfo ] && touch -a ${SSTATE_PKG}.siginfo
	# Use "! -w ||" to return true for read only files
	[ ! -w ${SSTATE_PKG} ] || touch --no-dereference ${SSTATE_PKG}
	[ ! -w ${SSTATE_PKG}.sig ] || [ ! -e ${SSTATE_PKG}.sig ] || touch --no-dereference ${SSTATE_PKG}.sig
	[ ! -w ${SSTATE_PKG}.siginfo ] || [ ! -e ${SSTATE_PKG}.siginfo ] || touch --no-dereference ${SSTATE_PKG}.siginfo
}

BB_HASHCHECK_FUNCTION = "sstate_checkhashes"

def sstate_checkhashes(sq_fn, sq_task, sq_hash, sq_hashfn, d, siginfo=False, *, sq_unihash=None):

    ret = []
    missed = []
    extension = ".tgz"
    if siginfo:
        extension = extension + ".siginfo"

    def gethash(task):
        if sq_unihash is not None:
            return sq_unihash[task]
        return sq_hash[task]

    def getpathcomponents(task, d):
        # Magic data from BB_HASHFILENAME
        splithashfn = sq_hashfn[task].split(" ")
        spec = splithashfn[1]
        if splithashfn[0] == "True":
            extrapath = d.getVar("NATIVELSBSTRING") + "/"
        else:
            extrapath = ""

        tname = sq_task[task][3:]

        if tname in ["fetch", "unpack", "patch", "populate_lic", "preconfigure"] and splithashfn[2]:
            spec = splithashfn[2]
            extrapath = ""

        return spec, extrapath, tname


    for task in range(len(sq_fn)):

        spec, extrapath, tname = getpathcomponents(task, d)

        sstatefile = d.expand("${SSTATE_DIR}/" + extrapath + generate_sstatefn(spec, gethash(task), d) + "_" + tname + extension)

        if os.path.exists(sstatefile):
            bb.debug(2, "SState: Found valid sstate file %s" % sstatefile)
            ret.append(task)
            continue
        else:
            missed.append(task)
            bb.debug(2, "SState: Looked for but didn't find file %s" % sstatefile)

    mirrors = d.getVar("SSTATE_MIRRORS")
    if mirrors:
        # Copy the data object and override DL_DIR and SRC_URI
        localdata = bb.data.createCopy(d)

        dldir = localdata.expand("${SSTATE_DIR}")
        localdata.delVar('MIRRORS')
        localdata.setVar('FILESPATH', dldir)
        localdata.setVar('DL_DIR', dldir)
        localdata.setVar('PREMIRRORS', mirrors)

        bb.debug(2, "SState using premirror of: %s" % mirrors)

        # if BB_NO_NETWORK is set but we also have SSTATE_MIRROR_ALLOW_NETWORK,
        # we'll want to allow network access for the current set of fetches.
        if bb.utils.to_boolean(localdata.getVar('BB_NO_NETWORK')) and \
                bb.utils.to_boolean(localdata.getVar('SSTATE_MIRROR_ALLOW_NETWORK')):
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
            bb.event.fire(bb.event.ProcessProgress(msg, len(tasklist) - thread_worker.tasks.qsize()), d)

        tasklist = []
        for task in range(len(sq_fn)):
            if task in ret:
                continue
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, gethash(task), d) + "_" + tname + extension)
            tasklist.append((task, sstatefile))

        if tasklist:
            msg = "Checking sstate mirror object availability"
            bb.event.fire(bb.event.ProcessStarted(msg, len(tasklist)), d)

            import multiprocessing
            nproc = min(multiprocessing.cpu_count(), len(tasklist))

            bb.event.enable_threadlock()
            pool = oe.utils.ThreadedPool(nproc, len(tasklist),
                    worker_init=checkstatus_init, worker_end=checkstatus_end)
            for t in tasklist:
                pool.add_task(checkstatus, t)
            pool.start()
            pool.wait_completion()
            bb.event.disable_threadlock()

            bb.event.fire(bb.event.ProcessFinished(msg), d)

    inheritlist = d.getVar("INHERIT")
    if "toaster" in inheritlist:
        evdata = {'missed': [], 'found': []};
        for task in missed:
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, gethash(task), d) + "_" + tname + ".tgz")
            evdata['missed'].append( (sq_fn[task], sq_task[task], gethash(task), sstatefile ) )
        for task in ret:
            spec, extrapath, tname = getpathcomponents(task, d)
            sstatefile = d.expand(extrapath + generate_sstatefn(spec, gethash(task), d) + "_" + tname + ".tgz")
            evdata['found'].append( (sq_fn[task], sq_task[task], gethash(task), sstatefile ) )
        bb.event.fire(bb.event.MetadataEvent("MissedSstate", evdata), d)

    # Print some summary statistics about the current task completion and how much sstate
    # reuse there was. Avoid divide by zero errors.
    total = len(sq_fn)
    currentcount = d.getVar("BB_SETSCENE_STAMPCURRENT_COUNT") or 0
    complete = 0
    if currentcount:
        complete = (len(ret) + currentcount) / (total + currentcount) * 100
    match = 0
    if total:
        match = len(ret) / total * 100
    bb.plain("Sstate summary: Wanted %d Found %d Missed %d Current %d (%d%% match, %d%% complete)" % (total, len(ret), len(missed), currentcount, match, complete))

    if hasattr(bb.parse.siggen, "checkhashes"):
        bb.parse.siggen.checkhashes(missed, ret, sq_fn, sq_task, sq_hash, sq_hashfn, d)

    return ret

BB_SETSCENE_DEPVALID = "setscene_depvalid"

def setscene_depvalid(task, taskdependees, notneeded, d, log=None):
    # taskdependees is a dict of tasks which depend on task, each being a 3 item list of [PN, TASKNAME, FILENAME]
    # task is included in taskdependees too
    # Return - False - We need this dependency
    #        - True - We can skip this dependency
    import re

    def logit(msg, log):
        if log is not None:
            log.append(msg)
        else:
            bb.debug(2, msg)

    logit("Considering setscene task: %s" % (str(taskdependees[task])), log)

    def isNativeCross(x):
        return x.endswith("-native") or "-cross-" in x or "-crosssdk" in x or x.endswith("-cross")

    # We only need to trigger populate_lic through direct dependencies
    if taskdependees[task][1] == "do_populate_lic":
        return True

    # stash_locale and gcc_stash_builddir are never needed as a dependency for built objects
    if taskdependees[task][1] == "do_stash_locale" or taskdependees[task][1] == "do_gcc_stash_builddir":
        return True

    # We only need to trigger packagedata through direct dependencies
    # but need to preserve packagedata on packagedata links
    if taskdependees[task][1] == "do_packagedata":
        for dep in taskdependees:
            if taskdependees[dep][1] == "do_packagedata":
                return False
        return True

    for dep in taskdependees:
        logit("  considering dependency: %s" % (str(taskdependees[dep])), log)
        if task == dep:
            continue
        if dep in notneeded:
            continue
        # do_package_write_* and do_package doesn't need do_package
        if taskdependees[task][1] == "do_package" and taskdependees[dep][1] in ['do_package', 'do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package_qa']:
            continue
        # do_package_write_* need do_populate_sysroot as they're mainly postinstall dependencies
        if taskdependees[task][1] == "do_populate_sysroot" and taskdependees[dep][1] in ['do_package_write_deb', 'do_package_write_ipk', 'do_package_write_rpm']:
            return False
        # do_package/packagedata/package_qa don't need do_populate_sysroot
        if taskdependees[task][1] == "do_populate_sysroot" and taskdependees[dep][1] in ['do_package', 'do_packagedata', 'do_package_qa']:
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
            # Allow excluding certain recursive dependencies. If a recipe needs it should add a
            # specific dependency itself, rather than relying on one of its dependees to pull
            # them in.
            # See also http://lists.openembedded.org/pipermail/openembedded-core/2018-January/146324.html
            not_needed = False
            excludedeps = d.getVar('_SSTATE_EXCLUDEDEPS_SYSROOT')
            if excludedeps is None:
                # Cache the regular expressions for speed
                excludedeps = []
                for excl in (d.getVar('SSTATE_EXCLUDEDEPS_SYSROOT') or "").split():
                    excludedeps.append((re.compile(excl.split('->', 1)[0]), re.compile(excl.split('->', 1)[1])))
                d.setVar('_SSTATE_EXCLUDEDEPS_SYSROOT', excludedeps)
            for excl in excludedeps:
                if excl[0].match(taskdependees[dep][0]):
                    if excl[1].match(taskdependees[task][0]):
                        not_needed = True
                        break
            if not_needed:
                continue
            # For meta-extsdk-toolchain we want all sysroot dependencies
            if taskdependees[dep][0] == 'meta-extsdk-toolchain':
                return False
            # Native/Cross populate_sysroot need their dependencies
            if isNativeCross(taskdependees[task][0]) and isNativeCross(taskdependees[dep][0]):
                return False
            # Target populate_sysroot depended on by cross tools need to be installed
            if isNativeCross(taskdependees[dep][0]):
                return False
            # Native/cross tools depended upon by target sysroot are not needed
            # Add an exception for shadow-native as required by useradd.bbclass
            if isNativeCross(taskdependees[task][0]) and taskdependees[task][0] != 'shadow-native':
                continue
            # Target populate_sysroot need their dependencies
            return False

        if taskdependees[task][1] == 'do_shared_workdir':
            continue

        if taskdependees[dep][1] == "do_populate_lic":
            continue


        # Safe fallthrough default
        logit(" Default setscene dependency fall through due to dependency: %s" % (str(taskdependees[dep])), log)
        return False
    return True

addhandler sstate_eventhandler
sstate_eventhandler[eventmask] = "bb.build.TaskSucceeded"
python sstate_eventhandler() {
    d = e.data
    # When we write an sstate package we rewrite the SSTATE_PKG
    spkg = d.getVar('SSTATE_PKG')
    if not spkg.endswith(".tgz"):
        taskname = d.getVar("BB_RUNTASK")[3:]
        spec = d.getVar('SSTATE_PKGSPEC')
        swspec = d.getVar('SSTATE_SWSPEC')
        if taskname in ["fetch", "unpack", "patch", "populate_lic", "preconfigure"] and swspec:
            d.setVar("SSTATE_PKGSPEC", "${SSTATE_SWSPEC}")
            d.setVar("SSTATE_EXTRAPATH", "")
        sstatepkg = d.getVar('SSTATE_PKG')
        bb.siggen.dump_this_task(sstatepkg + '_' + taskname + ".tgz" ".siginfo", d)
}

SSTATE_PRUNE_OBSOLETEWORKDIR ?= "1"

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
    preservestampfile = d.expand('${SSTATE_MANIFESTS}/preserve-stamps')
    preservestamps = []
    if os.path.exists(preservestampfile):
        with open(preservestampfile, 'r') as f:
            preservestamps = f.readlines()
    seen = []

    # The machine index contains all the stamps this machine has ever seen in this build directory.
    # We should only remove things which this machine once accessed but no longer does.
    machineindex = set()
    bb.utils.mkdirhier(d.expand("${SSTATE_MANIFESTS}"))
    mi = d.expand("${SSTATE_MANIFESTS}/index-machine-${MACHINE}")
    if os.path.exists(mi):
        with open(mi, "r") as f:
            machineindex = set(line.strip() for line in f.readlines())

    for a in sorted(list(set(d.getVar("SSTATE_ARCHS").split()))):
        toremove = []
        i = d.expand("${SSTATE_MANIFESTS}/index-" + a)
        if not os.path.exists(i):
            continue
        with open(i, "r") as f:
            lines = f.readlines()
            for l in lines:
                try:
                    (stamp, manifest, workdir) = l.split()
                    if stamp not in stamps and stamp not in preservestamps and stamp in machineindex:
                        toremove.append(l)
                        if stamp not in seen:
                            bb.debug(2, "Stamp %s is not reachable, removing related manifests" % stamp)
                            seen.append(stamp)
                except ValueError:
                    bb.fatal("Invalid line '%s' in sstate manifest '%s'" % (l, i))

        if toremove:
            msg = "Removing %d recipes from the %s sysroot" % (len(toremove), a)
            bb.event.fire(bb.event.ProcessStarted(msg, len(toremove)), d)

            removed = 0
            for r in toremove:
                (stamp, manifest, workdir) = r.split()
                for m in glob.glob(manifest + ".*"):
                    if m.endswith(".postrm"):
                        continue
                    sstate_clean_manifest(m, d)
                bb.utils.remove(stamp + "*")
                if removeworkdir:
                    bb.utils.remove(workdir, recurse = True)
                lines.remove(r)
                removed = removed + 1
                bb.event.fire(bb.event.ProcessProgress(msg, removed), d)

            bb.event.fire(bb.event.ProcessFinished(msg), d)

        with open(i, "w") as f:
            for l in lines:
                f.write(l)
    machineindex |= set(stamps)
    with open(mi, "w") as f:
        for l in machineindex:
            f.write(l + "\n")

    if preservestamps:
        os.remove(preservestampfile)
}
