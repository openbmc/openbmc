SUMMARY = "Storage management daemon for eMMC"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += "estoraged"

SRC_URI += " file://emmc.service"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "emmc.service"

do_install:append() {
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/emmc.service ${D}${systemd_system_unitdir}
}
