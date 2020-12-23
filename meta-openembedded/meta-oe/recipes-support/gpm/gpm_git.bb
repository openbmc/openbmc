SUMMARY = "Console mouse driver"
DESCRIPTION = "GPM (General Purpose Mouse) is a mouse server \
for the console and xterm, with sample clients included \
(emacs, etc)."
HOMEPAGE = "https://www.nico.schottelius.org/software/gpm"
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

PV = "1.99.7+git${SRCREV}"
PR = "r2"
SRCREV = "e82d1a653ca94aa4ed12441424da6ce780b1e530"

DEPENDS = "ncurses bison-native"

SRC_URI = "git://github.com/telmich/gpm;protocol=git \
           file://init \
           file://gpm.service.in \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d systemd texinfo

INITSCRIPT_NAME = "gpm"
INITSCRIPT_PARAMS = "defaults"

do_configure_prepend() {
    (cd ${S};./autogen.sh;cd -)
}

do_install_append () {
    install -d ${D}${systemd_system_unitdir}
    sed 's:@bindir@:${sbindir}:' < ${WORKDIR}/gpm.service.in >${D}${systemd_system_unitdir}/gpm.service
    install -D -m 0755 ${WORKDIR}/init ${D}${INIT_D_DIR}/gpm
    ln -s libgpm.so.2 ${D}${libdir}/libgpm.so
}

SYSTEMD_SERVICE_${PN} = "gpm.service"
