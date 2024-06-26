SUMMARY = "Publicly exposed development SSH key"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://gbmc-dev.pub"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILES:${PN} += " ${datadir}/authorized_keys.d"

do_install() {
  install -d ${D}${datadir}/authorized_keys.d/root
  install -m 0755 ${S}/gbmc-dev.pub ${D}${datadir}/authorized_keys.d/root/50-gbmc-dev
}
