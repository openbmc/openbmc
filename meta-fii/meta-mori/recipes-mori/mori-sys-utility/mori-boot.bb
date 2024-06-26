SUMMARY = "Phosphor OpenBMC Mori System Power Control Service"
DESCRIPTION = "Phosphor OpenBMC Mori System Power Control Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " systemd"
PR = "r1"

SRC_URI = " \
    file://init_once.sh \
    file://host-powerctrl.service \
"

SYSTEMD_SERVICE:${PN} = "host-powerctrl.service"

inherit systemd obmc-phosphor-systemd

do_install () {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/init_once.sh ${D}${libexecdir}/${PN}
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/host-powerctrl.service \
        ${D}${systemd_unitdir}/system
}

RDEPENDS:${PN}:append = " libsystemd bash"
