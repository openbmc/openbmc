SUMMARY = "OpenBMC Quanta Boot Status LED Service"
DESCRIPTION = "OpenBMC Quanta Boot Status LED Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

SRC_URI = " file://boot-status-led.sh \
            file://boot-status-led.service \
          "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/boot-status-led.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/boot-status-led.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "boot-status-led.service"
