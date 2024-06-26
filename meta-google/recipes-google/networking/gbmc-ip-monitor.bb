SUMMARY = "Allows hooking netlink events to perform network actions"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://gbmc-ip-monitor.service \
  file://gbmc-ip-monitor.sh \
  file://gbmc-ip-monitor-test.sh \
  "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DEPENDS += "test-sh"

RDEPENDS:${PN} += " \
  bash \
  iproute2 \
  "

SYSTEMD_SERVICE:${PN} += "gbmc-ip-monitor.service"

do_compile() {
  SYSROOT="$PKG_CONFIG_SYSROOT_DIR" bash gbmc-ip-monitor-test.sh || exit
}

do_install:append() {
  install -d -m0755 ${D}${libexecdir}
  install -m0755 gbmc-ip-monitor.sh ${D}${libexecdir}/

  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 gbmc-ip-monitor.service ${D}${systemd_system_unitdir}/
}
