SUMMARY = "OpenBMC Reload Sensors Service"
DESCRIPTION = "OpenBMC Reload Sensors Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " file://reload-sensors.sh \
            file://reload-sensors.service \
          "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/reload-sensors.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/reload-sensors.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "reload-sensors.service"
