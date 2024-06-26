SUMMARY = "OpenBMC NCPLite Check Inventory State Service"
DESCRIPTION = "OpenBMC NCPLite Check Inventory State Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " file://inventory-log.sh \
            file://inventory-log.service \
          "

do_install() {
    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${UNPACKDIR}/inventory-log.sh ${D}${libexecdir}/${BPN}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/inventory-log.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "inventory-log.service"
