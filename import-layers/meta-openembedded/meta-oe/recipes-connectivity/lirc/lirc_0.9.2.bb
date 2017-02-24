require lirc.inc

SRC_URI += " \
    file://0001-lircrcd-Mark-local-inline-funtions-as-static.patch \
    file://lircd.service \
    file://lircd.init \
    file://lircexec.init \
    file://lircd.conf \
    file://lirc_options.conf \
    file://lirc.tmpfiles \
"
#file://0001-Adaptation-for-STM-configuration.patch \
#
SRC_URI[md5sum] = "3afc84e79c0839823cc20e7a710dd06d"
SRC_URI[sha256sum] = "4e3f948fcdee6dce009171143f0cb7cd7be48593dd58138db4101a41f651a1dd"

SYSTEMD_PACKAGES = "lirc"
SYSTEMD_SERVICE_${PN} = "lircd.service lircmd.service"
SYSTEMD_AUTO_ENABLE_lirc = "enable"

inherit autotools pkgconfig systemd python3native

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd"
PACKAGECONFIG[x11] = "--with-x,--with-x=no,libx11,"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' x11', '', d)} \
"

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
}

PACKAGES =+ "${PN}-contrib ${PN}-exec ${PN}-plugins ${PN}-python"

RDEPENDS_${PN} = "bash"
RDEPENDS_${PN}-exec = "${PN}"
RDEPENDS_${PN}-python = "python"

RRECOMMENDS_lirc = "${PN}-exec ${PN}-plugins"

FILES_${PN}-plugins = "${libdir}/lirc/plugins/*.so ${datadir}/lirc/configs"
FILES_${PN}-contrib = "${datadir}/lirc/contrib"
FILES_${PN}-exec = "${bindir}/irexec ${sysconfdir}/lircexec"
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
