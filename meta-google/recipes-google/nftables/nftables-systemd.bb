SUMMARY = "nftables systemd wrapper"
DESCRIPTION = "nftables systemd wrapper"
PR = "r1"
PV = "1.0"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://nft-configure.sh \
  file://nftables.service \
  "

SYSTEMD_SERVICE:${PN} += "nftables.service"

RDEPENDS:${PN} += " \
  bash \
  nftables \
  "

do_install() {
  install -d ${D}${libexecdir}
  install -m0755 ${UNPACKDIR}/nft-configure.sh ${D}${libexecdir}/

  install -d ${D}${systemd_system_unitdir}
  install -m0644 ${UNPACKDIR}/nftables.service ${D}${systemd_system_unitdir}/
}
