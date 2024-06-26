SUMMARY = "Phosphor OpenBMC Kudo BMC Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo BMC Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "bash"
RPROVIDES:${PN} += "virtual/bmc-update"
FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-bmc.json"

SRC_URI += " \
    file://config-bmc.json \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-bmc.json ${D}${datadir}/phosphor-ipmi-flash
}
