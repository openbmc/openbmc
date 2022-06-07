SUMMARY = "Phosphor OpenBMC Mori BMC Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Mori BMC Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "mori-fw"

RPROVIDES:${PN} += "virtual/bmc-update"
PROVIDES += "virtual/bmc-update"
RPROVIDES:${PN} += "virtual/bmc-update"
FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-bmc.json"

SRC_URI += " \
    file://config-bmc.json \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bmc.json ${D}${datadir}/phosphor-ipmi-flash
}
