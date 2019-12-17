SUMMARY = "Userspace framebuffer boot logo based on usplash"
DESCRIPTION = "PSplash is a userspace graphical boot splash screen for mainly embedded Linux devices supporting a 16bpp or 32bpp framebuffer. It has few dependencies (just libc), supports basic images and text and handles rotation. Its visual look is configurable by basic source changes. Also included is a 'client' command utility for sending information to psplash such as boot progress information."
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/psplash"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://psplash.h;beginline=1;endline=16;md5=840fb2356b10a85bed78dd09dc7745c6"

SRCREV = "2015f7073e98dd9562db0936a254af5ef56356cf"
PV = "0.1+git${SRCPV}"
PR = "r15"

SRC_URI = "git://git.yoctoproject.org/${BPN} \
           file://psplash-init \
           ${SPLASH_IMAGES}"
UPSTREAM_CHECK_COMMITS = "1"

SPLASH_IMAGES = "file://psplash-poky-img.h;outsuffix=default"

python __anonymous() {
    oldpkgs = d.getVar("PACKAGES").split()
    splashfiles = d.getVar('SPLASH_IMAGES').split()
    pkgs = []
    localpaths = []
    haspng = False
    for uri in splashfiles:
        fetcher = bb.fetch2.Fetch([uri], d)
        flocal = os.path.basename(fetcher.localpath(uri))
        fbase = os.path.splitext(flocal)[0]
        outsuffix = fetcher.ud[uri].parm.get("outsuffix")
        if not outsuffix:
            if fbase.startswith("psplash-"):
                outsuffix = fbase[8:]
            else:
                outsuffix = fbase
            if outsuffix.endswith('-img'):
                outsuffix = outsuffix[:-4]
        outname = "psplash-%s" % outsuffix
        if outname == '' or outname in oldpkgs:
            bb.fatal("The output name '%s' derived from the URI %s is not valid, please specify the outsuffix parameter" % (outname, uri))
        else:
            pkgs.append(outname)
        if flocal.endswith(".png"):
            haspng = True
        localpaths.append(flocal)

    # Set these so that we have less work to do in do_compile and do_install_append
    d.setVar("SPLASH_INSTALL", " ".join(pkgs))
    d.setVar("SPLASH_LOCALPATHS", " ".join(localpaths))

    if haspng:
        d.appendVar("DEPENDS", " gdk-pixbuf-native")

    d.prependVar("PACKAGES", "%s " % (" ".join(pkgs)))
    mlprefix = d.getVar('MLPREFIX') or ''
    pn = d.getVar('PN') or ''
    for p in pkgs:
        ep = '%s%s' % (mlprefix, p)
        epsplash = '%s%s' % (mlprefix, 'psplash')
        d.setVar("FILES_%s" % ep, "${bindir}/%s" % p)
        d.setVar("ALTERNATIVE_%s" % ep, 'psplash')
        d.setVarFlag("ALTERNATIVE_TARGET_%s" % ep, 'psplash', '${bindir}/%s' % p)
        d.appendVar("RDEPENDS_%s" % ep, " %s" % pn)
        if p == "psplash-default":
            d.appendVar("RRECOMMENDS_%s" % pn, " %s" % ep)
}

S = "${WORKDIR}/git"

inherit autotools pkgconfig update-rc.d update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_LINK_NAME[psplash] = "${bindir}/psplash"

python do_compile () {
    import shutil
    import subprocess

    # Build a separate executable for each splash image
    workdir = d.getVar('WORKDIR')
    convertscript = "%s/make-image-header.sh" % d.getVar('S')
    destfile = "%s/psplash-poky-img.h" % d.getVar('S')
    localfiles = d.getVar('SPLASH_LOCALPATHS').split()
    outputfiles = d.getVar('SPLASH_INSTALL').split()
    for localfile, outputfile in zip(localfiles, outputfiles):
        if localfile.endswith(".png"):
            if subprocess.call([ convertscript, os.path.join(workdir, localfile), 'POKY' ], cwd=workdir):
                bb.fatal("Error calling convert script '%s'" % (convertscript))
            fbase = os.path.splitext(localfile)[0]
            shutil.copyfile(os.path.join(workdir, "%s-img.h" % fbase), destfile)
        else:
            shutil.copyfile(os.path.join(workdir, localfile), destfile)
        # For some reason just updating the header is not enough, we have to touch the .c
        # file in order to get it to rebuild
        os.utime("%s/psplash.c" % d.getVar('S'), None)
        bb.build.exec_func("oe_runmake", d)
        shutil.copyfile("psplash", outputfile)
}

do_install_append() {
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/psplash-init ${D}${sysconfdir}/init.d/psplash.sh
	install -d ${D}${bindir}
	for i in ${SPLASH_INSTALL} ; do
		install -m 0755 $i ${D}${bindir}/$i
	done
	rm -f ${D}${bindir}/psplash
}

INITSCRIPT_NAME = "psplash.sh"
INITSCRIPT_PARAMS = "start 0 S . stop 20 0 1 6 ."

PACKAGE_WRITE_DEPS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"
pkg_postinst_${PN} () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask psplash.service
	fi
}
