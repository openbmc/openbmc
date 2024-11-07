SUMMARY = "Systemd target for bare-metal setup"
DESCRIPTION = " \
  This target can be used to disable host-facing interfaces while the host \
  is running an untrusted operating system. \
"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://gbmc-bare-metal-active@.target"
S = "${WORKDIR}"

DEPENDS += " \
  systemd \
  "

inherit systemd

SYSTEMD_SERVICE:${PN} += " \
  gbmc-bare-metal-active@.target \
  "

do_install() {
  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 ${WORKDIR}/gbmc-bare-metal-active@.target ${D}${systemd_system_unitdir}/
}
