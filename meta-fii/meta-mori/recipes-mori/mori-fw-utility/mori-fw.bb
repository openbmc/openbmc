SUMMARY = "Phosphor OpenBMC mori Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC mori Firmware Upgrade Comman Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " systemd phosphor-ipmi-flash"
PR = "r1"

SRC_URI = " \
    file://mori-fw.sh \
    file://mori-fw-ver.service \
    file://mori-fw-ver.sh \
    file://mori-lib.sh \
"

SYSTEMD_SERVICE:${PN} = "mori-fw-ver.service"

inherit systemd obmc-phosphor-systemd

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/mori-fw.sh ${D}${bindir}/mori-fw.sh
    install -m 0755 ${UNPACKDIR}/mori-fw-ver.sh \
        ${D}${libexecdir}/${PN}/mori-fw-ver.sh
    install -m 0755 ${UNPACKDIR}/mori-lib.sh ${D}${libexecdir}/${PN}/mori-lib.sh
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/mori-fw-ver.service ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN}:append = " libsystemd bash"
