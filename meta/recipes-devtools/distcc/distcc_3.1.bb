SUMMARY = "A parallel build system"
DESCRIPTION = "distcc is a parallel build system that distributes \
compilation of C/C++/ObjC code across machines on a network."
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "avahi"

GTKCONFIG = "gtk"
GTKCONFIG_libc-uclibc = ""

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${GTKCONFIG}', '', d)} popt"
PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk --without-gnome,gtk+"
# use system popt by default
PACKAGECONFIG[popt] = "--without-included-popt,--with-included-popt,popt"

RRECOMMENDS_${PN} = "avahi-daemon"

SRC_URI = "git://github.com/distcc/distcc.git \
           file://separatebuilddir.patch \
           file://0001-zeroconf-Include-fcntl.h.patch \
           file://default \
           file://distccmon-gnome.desktop \
           file://distcc \
           file://distcc.service"
SRCREV = "9a172d3d20042754a30c0cd9a5d63fd1a8120c31"
S = "${WORKDIR}/git"

inherit autotools pkgconfig update-rc.d useradd systemd

EXTRA_OECONF += "--disable-Werror PYTHON=/dev/null"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system \
                       --home /dev/null \
                       --no-create-home \
                       --gid nogroup \
                       distcc"

INITSCRIPT_NAME = "distcc"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "distcc.service"

do_install_append() {
    install -d ${D}${sysconfdir}/init.d/
    install -d ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/distcc ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/default ${D}${sysconfdir}/default/distcc
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/distcc.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BINDIR@,${bindir},g' ${D}${systemd_unitdir}/system/distcc.service
    ${DESKTOPINSTALL}
}
DESKTOPINSTALL = ""
DESKTOPINSTALL_libc-glibc () {
    install -d ${D}${datadir}/distcc/
    install -m 0644 ${WORKDIR}/distccmon-gnome.desktop ${D}${datadir}/distcc/
}
PACKAGES += "distcc-distmon-gnome"

FILES_${PN} = " ${sysconfdir} \
		${bindir}/distcc \
    ${bindir}/lsdistcc \
		${bindir}/distccd \
		${bindir}/distccmon-text \
		${systemd_unitdir}/system/distcc.service"
FILES_distcc-distmon-gnome = "  ${bindir}/distccmon-gnome \
				${datadir}/distcc"

pkg_postrm_${PN} () {
	deluser distcc || true
}
