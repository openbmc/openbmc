DESCRIPTION = "LIRC is a package that allows you to decode and send infra-red signals of many commonly used remote controls."
DESCRIPTION_append_lirc = " This package contains the lirc daemon, libraries and tools."
DESCRIPTION_append_lirc-exec = " This package contains a daemon that runs programs on IR signals."
DESCRIPTION_append_lirc-remotes = " This package contains some config files for remotes."
DESCRIPTION_append_lirc-nslu2example = " This package contains a working config for RC5 remotes and a modified NSLU2."
HOMEPAGE = "http://www.lirc.org"
SECTION = "console/network"
LICENSE = "GPLv2"
DEPENDS = "libxslt-native alsa-lib libftdi libusb1 libusb-compat jack portaudio-v19"


LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://prdownloads.sourceforge.net/lirc/lirc-${PV}.tar.bz2 \
    file://pollfd.patch \
    file://lircd.service \
    file://lircd.init \
    file://lircexec.init \
    file://lircd.conf \
    file://lirc_options.conf \
    file://lirc.tmpfiles \
"
SRC_URI[md5sum] = "0d11679cbdd94a5a6da00a8e7231b4bf"
SRC_URI[sha256sum] = "c68f18c35b489b865c0a741d119b136e8702191538cd3551b977a7af6c4e41ab"

SYSTEMD_PACKAGES = "lirc lirc-exec"
SYSTEMD_SERVICE_${PN} = "lircd.service lircmd.service lircd-setup.service lircd-uinput.service"
SYSTEMD_SERVICE_${PN}-exec = "irexec.service"
SYSTEMD_AUTO_ENABLE_lirc = "enable"
SYSTEMD_AUTO_ENABLE_lirc-exec = "enable"

inherit autotools pkgconfig systemd python3native

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd"
PACKAGECONFIG[x11] = "--with-x,--with-x=no,libx11,"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' x11', '', d)} \
"
CACHED_CONFIGUREVARS = "HAVE_WORKING_POLL=yes"

#EXTRA_OEMAKE = 'SUBDIRS="lib daemons tools"'
do_install_append() {
    install -m 0755 -d ${D}${sysconfdir}
    install -m 0755 -d ${D}${sysconfdir}/lirc
    install -m 0755 -d ${D}${systemd_unitdir}/system
    install -m 0755 -d ${D}${libdir}/tmpfiles.d
    install -m 0644 ${WORKDIR}/lircd.conf ${D}${sysconfdir}/lirc/
    install -m 0644 ${WORKDIR}/lirc_options.conf ${D}${sysconfdir}/lirc/
    install -m 0644 ${WORKDIR}/lircd.service ${D}${systemd_unitdir}/system/
    install -m 0755 ${WORKDIR}/lircexec.init ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/lirc.tmpfiles ${D}${libdir}/tmpfiles.d/lirc.conf
    rm -rf ${D}${libdir}/lirc/plugins/*.la
    rmdir ${D}/var/run/lirc ${D}/var/run
    chown -R root:root ${D}${datadir}/lirc/contrib
}

PACKAGES =+ "${PN}-contrib ${PN}-exec ${PN}-plugins ${PN}-python"

RDEPENDS_${PN} = "bash"
RDEPENDS_${PN}-exec = "${PN}"
RDEPENDS_${PN}-python = "python"

RRECOMMENDS_lirc = "${PN}-exec ${PN}-plugins"

FILES_${PN}-plugins = "${libdir}/lirc/plugins/*.so ${datadir}/lirc/configs"
FILES_${PN}-contrib = "${datadir}/lirc/contrib"
FILES_${PN}-exec = "${bindir}/irexec ${sysconfdir}/lircexec ${systemd_unitdir}/system/irexec.service"
FILES_${PN} += "${systemd_unitdir}/system/lircexec.init"
FILES_${PN} += "${systemd_unitdir}/system/lircd.service"
FILES_${PN} += "${systemd_unitdir}/system/lircd.socket"
FILES_${PN} += "${libdir}/tmpfiles.d/lirc.conf"
FILES_${PN}-dbg += "${libdir}/lirc/plugins/.debug"
FILES_${PN}-python += "${libdir}/python*/site-packages"


INITSCRIPT_PACKAGES = "lirc lirc-exec"
INITSCRIPT_NAME_lirc-exec = "lircexec"
INITSCRIPT_PARAMS_lirc-exec = "defaults 21"

# this is for distributions that don't use udev
pkg_postinst_${PN}_append() {
    if [ ! -c $D/dev/lirc -a ! -f /sbin/udevd ]; then mknod $D/dev/lirc c 61 0; fi
}

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
