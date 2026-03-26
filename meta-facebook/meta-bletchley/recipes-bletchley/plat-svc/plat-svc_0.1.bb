LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "bletchley-common-functions"
RDEPENDS:${PN} += "mdio-tools"

SRC_URI += " \
    file://bletchley-early-sys-init \
    file://bletchley-sys-init.service \
    file://bletchley-host-state-monitor \
    file://bletchley-host-state-monitor.service \
    file://bletchley-sys-init.conf \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    bletchley-sys-init.service \
    bletchley-host-state-monitor.service \
    "

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-early-sys-init ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-host-state-monitor ${D}${libexecdir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/bletchley-sys-init.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/bletchley-host-state-monitor.service ${D}${systemd_system_unitdir}

    install -d ${D}${systemd_system_unitdir}/bletchley-sys-init.service.d
    install -m 0644 ${UNPACKDIR}/bletchley-sys-init.conf \
        ${D}${systemd_system_unitdir}/bletchley-sys-init.service.d/bletchley-sys-init.conf
}
