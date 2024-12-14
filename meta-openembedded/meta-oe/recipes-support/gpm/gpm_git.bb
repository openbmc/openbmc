SUMMARY = "Console mouse driver"
DESCRIPTION = "GPM (General Purpose Mouse) is a mouse server \
for the console and xterm, with sample clients included \
(emacs, etc)."
HOMEPAGE = "https://www.nico.schottelius.org/software/gpm"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

PV = "1.99.7+git${SRCREV}"
SRCREV = "e82d1a653ca94aa4ed12441424da6ce780b1e530"

DEPENDS = "ncurses bison-native"

SRC_URI = "git://github.com/telmich/gpm;protocol=https;branch=master \
           file://init \
           file://gpm.service.in \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d systemd texinfo

INITSCRIPT_NAME = "gpm"
INITSCRIPT_PARAMS = "defaults"

# Avoid line statements with bison/yacc
# ERROR: lib32-gpm-1.99.7+gite82d1a653ca94aa4ed12441424da6ce780b1e530-r0 do_package_qa: QA Issue: File /usr/src/debug/lib32-gpm/1.99.7+gite82d1a653ca94aa4ed12441424da6ce780b1e530/src/prog/gpm-root.c in package lib32-gpm-src contains reference to TMPDIR [buildpaths]
EXTRA_OEMAKE = "YFLAGS='-l'"

do_configure:prepend() {
    (cd ${S};./autogen.sh;cd -)
}

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    sed 's:@bindir@:${sbindir}:' < ${UNPACKDIR}/gpm.service.in >${D}${systemd_system_unitdir}/gpm.service
    install -D -m 0755 ${UNPACKDIR}/init ${D}${INIT_D_DIR}/gpm
    ln -s libgpm.so.2 ${D}${libdir}/libgpm.so
}

SYSTEMD_SERVICE:${PN} = "gpm.service"
