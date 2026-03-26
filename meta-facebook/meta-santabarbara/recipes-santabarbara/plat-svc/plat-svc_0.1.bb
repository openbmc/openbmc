LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"

SRC_URI += "\
    file://santabarbara-early-sys-init \
    file://santabarbara-eid-init.service \
    file://santabarbara-eid-init \
    file://santabarbara-sys-init.service \
    file://santabarbara-tray-location-init \
    file://santabarbara-tray-location-init.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    santabarbara-eid-init.service \
    santabarbara-sys-init.service \
    santabarbara-tray-location-init.service \
    "

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install() {
    install -d ${D}${libexecdir}
    install -d ${D}${libexecdir}/plat-svc
    install -m 0755 ${UNPACKDIR}/santabarbara-early-sys-init ${D}${libexecdir}/plat-svc
    install -m 0755 ${UNPACKDIR}/santabarbara-eid-init ${D}${libexecdir}/plat-svc
    install -m 0755 ${UNPACKDIR}/santabarbara-tray-location-init ${D}${libexecdir}/plat-svc

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/santabarbara-eid-init.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/santabarbara-sys-init.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/santabarbara-tray-location-init.service ${D}${systemd_system_unitdir}
}

