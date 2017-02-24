SUMMARY = "minicoredumper provides an alternate core dump facility for Linux \
to allow minimal and customized crash dumps"
LICENSE = " LGPLv2.1 & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=45445387350de96a0e70410470ee5cab"
DEPENDS = "elfutils dbus dbus-glib-native glib-2.0 dbus-glib util-linux"

inherit autotools pkgconfig systemd update-rc.d

SRC_URI = "https://linutronix.de/${BPN}/files/${BPN}-${PV}.tar.gz \
           file://minicoredumper.service \
           file://minicoredumper.init \
"
SRC_URI[md5sum] = "5ba9d116b52a8e2fb93456260644e753"
SRC_URI[sha256sum] = "1b0eeb3d70dbd2ad6f2f673e4e3446e5dd784e132730e21d8f9dc0977e47dd9a"

SYSTEMD_SERVICE_${PN} = "minicoredumper.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "minicoredumper"
INITSCRIPT_PARAMS_${PN} = "defaults 89"

do_install_append() {
    install -d ${D}/${sysconfdir}/minicoredumper
    cp -rf ${S}/etc/* ${D}/${sysconfdir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/minicoredumper.service ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/minicoredumper.init ${D}${sysconfdir}/init.d/minicoredumper
}
