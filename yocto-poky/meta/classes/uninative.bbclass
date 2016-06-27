UNINATIVE_LOADER ?= "${STAGING_DIR}-uninative/${BUILD_ARCH}-linux/lib/${@bb.utils.contains('BUILD_ARCH', 'x86_64', 'ld-linux-x86-64.so.2', 'ld-linux.so.2', d)}"

UNINATIVE_URL ?= "unset"
UNINATIVE_TARBALL ?= "${BUILD_ARCH}-nativesdk-libc.tar.bz2"
# Example checksums
#UNINATIVE_CHECKSUM[i586] = "dead"
#UNINATIVE_CHECKSUM[x86_64] = "dead"
UNINATIVE_DLDIR ?= "${DL_DIR}/uninative/"

# https://wiki.debian.org/GCC5
# We may see binaries built with gcc5 run or linked into gcc4 environment
# so use the older libstdc++ standard for now until we don't support gcc4
# on the host system.
BUILD_CXXFLAGS_append = " -D_GLIBCXX_USE_CXX11_ABI=0"

#
# icu configure defaults to CXX11 if no -std= option is passed in CXXFLAGS
# therefore pass one
BUILD_CXXFLAGS_append_pn-icu-native = " -std=c++98"

addhandler uninative_event_fetchloader
uninative_event_fetchloader[eventmask] = "bb.event.BuildStarted"

addhandler uninative_event_enable
uninative_event_enable[eventmask] = "bb.event.ConfigParsed"

python uninative_event_fetchloader() {
    """
    This event fires on the parent and will try to fetch the tarball if the
    loader isn't already present.
    """

    chksum = d.getVarFlag("UNINATIVE_CHECKSUM", d.getVar("BUILD_ARCH", True), True)
    if not chksum:
        bb.fatal("Uninative selected but not configured correctly, please set UNINATIVE_CHECKSUM[%s]" % d.getVar("BUILD_ARCH", True))

    loader = d.getVar("UNINATIVE_LOADER", True)
    loaderchksum = loader + ".chksum"
    if os.path.exists(loader) and os.path.exists(loaderchksum):
        with open(loaderchksum, "r") as f:
            readchksum = f.read().strip()
        if readchksum == chksum:
            return

    import subprocess
    try:
        # Save and restore cwd as Fetch.download() does a chdir()
        olddir = os.getcwd()

        tarball = d.getVar("UNINATIVE_TARBALL", True)
        tarballdir = os.path.join(d.getVar("UNINATIVE_DLDIR", True), chksum)
        tarballpath = os.path.join(tarballdir, tarball)

        if not os.path.exists(tarballpath):
            bb.utils.mkdirhier(tarballdir)
            if d.getVar("UNINATIVE_URL", True) == "unset":
                bb.fatal("Uninative selected but not configured, please set UNINATIVE_URL")

            localdata = bb.data.createCopy(d)
            localdata.setVar('FILESPATH', "")
            localdata.setVar('DL_DIR', tarballdir)

            srcuri = d.expand("${UNINATIVE_URL}${UNINATIVE_TARBALL};sha256sum=%s" % chksum)
            bb.note("Fetching uninative binary shim from %s" % srcuri)

            fetcher = bb.fetch2.Fetch([srcuri], localdata, cache=False)
            fetcher.download()
            localpath = fetcher.localpath(srcuri)
            if localpath != tarballpath and os.path.exists(localpath) and not os.path.exists(tarballpath):
                    os.symlink(localpath, tarballpath)

        cmd = d.expand("mkdir -p ${STAGING_DIR}-uninative; cd ${STAGING_DIR}-uninative; tar -xjf ${UNINATIVE_DLDIR}/%s/${UNINATIVE_TARBALL}; ${STAGING_DIR}-uninative/relocate_sdk.py ${STAGING_DIR}-uninative/${BUILD_ARCH}-linux ${UNINATIVE_LOADER} ${UNINATIVE_LOADER} ${STAGING_DIR}-uninative/${BUILD_ARCH}-linux/${bindir_native}/patchelf-uninative ${STAGING_DIR}-uninative/${BUILD_ARCH}-linux${base_libdir_native}/libc*.so" % chksum)
        subprocess.check_call(cmd, shell=True)

        with open(loaderchksum, "w") as f:
            f.write(chksum)

        enable_uninative(d)

    except bb.fetch2.BBFetchException as exc:
        bb.warn("Disabling uninative as unable to fetch uninative tarball: %s" % str(exc))
        bb.warn("To build your own uninative loader, please bitbake uninative-tarball and set UNINATIVE_TARBALL appropriately.")
    except subprocess.CalledProcessError as exc:
        bb.warn("Disabling uninative as unable to install uninative tarball: %s" % str(exc))
        bb.warn("To build your own uninative loader, please bitbake uninative-tarball and set UNINATIVE_TARBALL appropriately.")
    finally:
        os.chdir(olddir)
}

python uninative_event_enable() {
    """
    This event handler is called in the workers and is responsible for setting
    up uninative if a loader is found.
    """
    enable_uninative(d)
}

def enable_uninative(d):
    loader = d.getVar("UNINATIVE_LOADER", True)
    if os.path.exists(loader):
        bb.debug(2, "Enabling uninative")
        d.setVar("NATIVELSBSTRING", "universal")
        d.appendVar("SSTATEPOSTUNPACKFUNCS", " uninative_changeinterp")
        d.prependVar("PATH", "${STAGING_DIR}-uninative/${BUILD_ARCH}-linux${bindir_native}:")

python uninative_changeinterp () {
    import subprocess
    import stat
    import oe.qa

    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross', d)):
        return

    sstateinst = d.getVar('SSTATE_INSTDIR', True)
    for walkroot, dirs, files in os.walk(sstateinst):
        for file in files:
            if file.endswith(".so") or ".so." in file:
                continue
            f = os.path.join(walkroot, file)
            if os.path.islink(f):
                continue
            s = os.stat(f)
            if not ((s[stat.ST_MODE] & stat.S_IXUSR) or (s[stat.ST_MODE] & stat.S_IXGRP) or (s[stat.ST_MODE] & stat.S_IXOTH)):
                continue
            elf = oe.qa.ELFFile(f)
            try:
                elf.open()
            except oe.qa.NotELFFileError:
                continue
            if not elf.isDynamic():
                continue

            try:
                subprocess.check_output(("patchelf-uninative", "--set-interpreter",
                                         d.getVar("UNINATIVE_LOADER", True), f),
                                        stderr=subprocess.STDOUT)
            except subprocess.CalledProcessError as e:
                bb.fatal("'%s' failed with exit code %d and the following output:\n%s" %
                         (e.cmd, e.returncode, e.output))
}
