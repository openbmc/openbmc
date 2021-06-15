PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4325afd396febcb659c36b49533135d4"

SRC_URI = "git://github.com/mstpd/mstpd"
SRCREV = "973c403c79f3d419d9e93a242766ddf0099d1299"
S = "${WORKDIR}/git"

SRC_URI += "file://bridge-stp"
SRC_URI += "file://mstpd.service"

inherit autotools pkgconfig systemd

PACKAGES =+ "${PN}-mstpd"
FILES_${PN}-mstpd = "${sbindir}/mstpd ${sbindir}/mstpctl /sbin/bridge-stp"

SYSTEMD_PACKAGES = "${PN}-mstpd"
SYSTEMD_SERVICE_${PN}-mstpd = "mstpd.service"

do_install_append() {
  rm -r ${D}${libexecdir}

  install -d -m 0755 ${D}/sbin
  install -m 0755 ${WORKDIR}/bridge-stp ${D}/sbin

  install -d -m 0755 ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/mstpd.service ${D}${systemd_system_unitdir}/
}
