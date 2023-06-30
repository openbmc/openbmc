SUMMARY = "OpenBMC Mori Boot Status LED Service"
DESCRIPTION = "OpenBMC Mori Boot Status LED Daemon."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " systemd"
PR = "r1"

SRC_URI = " \
    file://boot-status-led.sh \
    file://boot-status-led.service \
"

SYSTEMD_SERVICE:${PN} = "boot-status-led.service"

inherit systemd

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/boot-status-led.sh ${D}${bindir}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/boot-status-led.service \
        ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN}:append = " bash"
