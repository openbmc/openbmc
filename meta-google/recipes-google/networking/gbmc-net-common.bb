SUMMARY = "gBMC common networking components"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += " \
  file://gbmc-net-lib.sh \
  file://gbmc-net-lib-test.sh \
  file://gbmc-ra.sh \
  "
S = "${UNPACKDIR}"

FILES:${PN} += " \
  ${datadir}/ \
  "

DEPENDS += "test-sh"

RDEPENDS:${PN}:append = " \
  bash \
  network-sh \
  ndisc6-rdisc6 \
  "

do_compile() {
  SYSROOT="${STAGING_DIR_HOST}" bash gbmc-net-lib-test.sh || exit
}

do_install() {
  install -d -m0755 ${D}${datadir}
  install -m0644 ${UNPACKDIR}/gbmc-net-lib.sh ${D}${datadir}/
  install -m0644 ${UNPACKDIR}/gbmc-ra.sh ${D}${datadir}/
}

