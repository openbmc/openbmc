SUMMARY = "Disable obmc-console while the customer's host OS is running"
DESCRIPTION = "Disable obmc-console while an untrusted host OS is running"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://disable-obmc-console.service \
  file://disable_obmc_console.sh \
"

DEPENDS += "systemd"

RDEPENDS:${PN}:append = " \
  bash \
  bare-metal-active \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
  disable-obmc-console.service \
  "

do_install:append() {
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/disable-obmc-console.service ${D}${systemd_system_unitdir}
  install -d -m0755 ${D}${libexecdir}
  install -m0755 ${UNPACKDIR}/disable_obmc_console.sh ${D}${libexecdir}/
}
