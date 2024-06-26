LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "bletchley-common-functions"
RDEPENDS:${PN} += "mdio-tools"

SRC_URI += " \
    file://bletchley-early-sys-init \
    file://bletchley-sys-init.service \
    file://bletchley-host-state-monitor \
    file://bletchley-host-state-monitor.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    bletchley-sys-init.service \
    bletchley-host-state-monitor.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-early-sys-init ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-host-state-monitor ${D}${libexecdir}
}

SYSTEMD_OVERRIDE:${PN} += "bletchley-sys-init.conf:bletchley-sys-init.service.d/bletchley-sys-init.conf"
