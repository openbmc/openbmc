DESCRIPTION = "GPM (General Purpose Mouse) is a mouse server \
for the console and xterm, with sample clients included \
(emacs, etc)."
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

PV = "1.99.7+git${SRCREV}"
PR = "r2"
SRCREV = "1fd19417b8a4dd9945347e98dfa97e4cfd798d77"

DEPENDS = "ncurses bison-native"

SRC_URI = "git://github.com/telmich/gpm;protocol=git \
           file://init \
           file://no-docs.patch \
           file://processcreds.patch \
           file://gpm.service.in \
           file://0001-Use-sigemptyset-API-instead-of-__sigemptyset.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d systemd

INITSCRIPT_NAME = "gpm"
INITSCRIPT_PARAMS = "defaults"

do_configure_prepend() {
    (cd ${S};./autogen.sh;cd -)
}

do_install_append () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        sed 's:@bindir@:${bindir}:' < ${WORKDIR}/gpm.service.in >${D}${systemd_system_unitdir}/gpm.service
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -D -m 0755 ${WORKDIR}/init ${D}/${sysconfdir}/init.d/gpm
    fi
    install -D -m 0644 ${S}/src/headers/gpm.h ${D}${includedir}/gpm.h
    ln -s libgpm.so.2 ${D}${libdir}/libgpm.so
}

SYSTEMD_SERVICE_${PN} = "gpm.service"

FILES_${PN} += "${datadir}/emacs"
