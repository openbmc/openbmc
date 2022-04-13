LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"

SRC_URI += " \
    file://bletchley-early-sys-init \
    file://bletchley-sys-init.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    bletchley-sys-init.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/bletchley-early-sys-init ${D}${libexecdir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/bletchley-sys-init.service ${D}${systemd_system_unitdir}
}

SYSTEMD_OVERRIDE:${PN}:bletchley += "bletchley-sys-init.conf:bletchley-sys-init.service.d/bletchley-sys-init.conf"
