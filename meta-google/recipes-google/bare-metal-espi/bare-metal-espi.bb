SUMMARY = "Disable eSPI while the customer's host OS is running"
DESCRIPTION = "Disable eSPI while an untrusted host OS is running"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://disable-espi.service \
"

DEPENDS += "systemd"

RDEPENDS:${PN}:append = " \
  bare-metal-active \
  espi-control \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
  disable-espi.service \
  "

do_install:append() {
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/disable-espi.service ${D}${systemd_system_unitdir}
}
