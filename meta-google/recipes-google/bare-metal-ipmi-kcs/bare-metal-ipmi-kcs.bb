SUMMARY = "Disable ipmi kcs while in Guest Os"
DESCRIPTION = "Disable ipmi kcs while an untrusted host OS is running"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://disable-ipmi-kcs.service.in \
"

DEPENDS += "systemd"

RDEPENDS:${PN}:append = " \
  bare-metal-active \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
  disable-ipmi-kcs.service \
  "

# This should be aligned with the phosphor-ipmi-kcs and override per platform
KCS_DEVICE ?= "ipmi_kcs1"

do_install:append() {

  sed ${UNPACKDIR}/disable-ipmi-kcs.service.in \
        -e "s#@KCS_DEV@#${KCS_DEVICE}#" \
        > ${UNPACKDIR}/disable-ipmi-kcs.service

  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/disable-ipmi-kcs.service ${D}${systemd_system_unitdir}
}
