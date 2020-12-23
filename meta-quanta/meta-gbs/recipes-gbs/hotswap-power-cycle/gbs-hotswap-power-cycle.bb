SUMMARY = "Power Cycle by Hotswap Controller"
DESCRIPTION = "Power Cycle by Hotswap Controller Daemon"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

SRC_URI = " file://hotswap-power-cycle.service \
            file://tray_powercycle.sh \
          "

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/hotswap-power-cycle.service ${D}${systemd_system_unitdir}

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/tray_powercycle.sh ${D}${bindir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "hotswap-power-cycle.service"

