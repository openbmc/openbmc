NATIVELSBSTRING = "universal"

UNINATIVE_LOADER = "${STAGING_DIR_NATIVE}/lib/ld-linux-x86-64.so.2"

addhandler uninative_eventhandler
uninative_eventhandler[eventmask] = "bb.event.BuildStarted"

python uninative_eventhandler() {
    loader = e.data.getVar("UNINATIVE_LOADER", True)
    if not os.path.exists(loader):
        import subprocess
        cmd = e.data.expand("mkdir -p ${STAGING_DIR}; cd ${STAGING_DIR}; tar -xjf ${COREBASE}/${BUILD_ARCH}-nativesdk-libc.tar.bz2; ${STAGING_DIR}/relocate_sdk.py ${STAGING_DIR_NATIVE} ${UNINATIVE_LOADER} ${UNINATIVE_LOADER} ${STAGING_BINDIR_NATIVE}/patchelf-uninative")
        #bb.warn("nativesdk lib extraction: " + cmd)
        subprocess.check_call(cmd, shell=True)
}

SSTATEPOSTUNPACKFUNCS_append = " uninative_changeinterp"

python uninative_changeinterp () {
    import subprocess
    import stat
    import oe.qa

    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('cross', d)):
        return

    sstateinst = d.getVar('SSTATE_INSTDIR', True)
    for walkroot, dirs, files in os.walk(sstateinst):
        for file in files:
            f = os.path.join(walkroot, file)
            if os.path.islink(f):
                continue
            s = os.stat(f)
            if not ((s[stat.ST_MODE] & stat.S_IXUSR) or (s[stat.ST_MODE] & stat.S_IXGRP) or (s[stat.ST_MODE] & stat.S_IXOTH)):
                continue
            elf = oe.qa.ELFFile(f)
            try:
                elf.open()
            except:
                continue

            #bb.warn("patchelf-uninative --set-interpreter %s %s" % (d.getVar("UNINATIVE_LOADER", True), f))
            subprocess.call("patchelf-uninative --set-interpreter %s %s" % (d.getVar("UNINATIVE_LOADER", True), f), shell=True)
}
