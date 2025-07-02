SUMMARY = "Systemd target for bare-metal setup"
DESCRIPTION = " \
  The gbmc-bare-metal-active target can be used to disable host-facing \
  interfaces while the host is running an untrusted operating system. \
  The gbmc-bare-metal-prep target can be used for any preparation work \
  before interfaces get disabled by the gbmc-bare-metal-active target. \
"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://gbmc-bare-metal-active@.target"
SRC_URI += "file://gbmc-bare-metal-prep@.target"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DEPENDS += " \
  systemd \
  "

inherit systemd

SYSTEMD_SERVICE:${PN} += " \
  gbmc-bare-metal-active@.target \
  gbmc-bare-metal-prep@.target \
  "

do_install() {
  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 ${UNPACKDIR}/gbmc-bare-metal-active@.target ${D}${systemd_system_unitdir}/
  install -m0644 ${UNPACKDIR}/gbmc-bare-metal-prep@.target ${D}${systemd_system_unitdir}/
}
