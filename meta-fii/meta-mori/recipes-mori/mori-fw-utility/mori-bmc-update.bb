SUMMARY = "Phosphor OpenBMC Mori BMC Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Mori BMC Firmware Upgrade Comman Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " phosphor-ipmi-flash"
PROVIDES:append = " virtual/bmc-update"
PR = "r1"

SRC_URI = "file://config-bmc.json"

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bmc.json \
        ${D}${datadir}/phosphor-ipmi-flash
}

RDEPENDS:${PN}:append = " mori-fw"

RPROVIDES:${PN}:append = " virtual/bmc-update"

FILES:${PN}:append = " ${datadir}/phosphor-ipmi-flash/config-bmc.json"
