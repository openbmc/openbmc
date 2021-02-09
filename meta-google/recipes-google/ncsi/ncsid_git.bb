SUMMARY = "Google NCSI daemon"
DESCRIPTION = "Google NCSI daemon."
HOMEPAGE = "http://github.com/openbmc/google-misc"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "git://github.com/openbmc/google-misc"
SRCREV = "7d6fa42fd19cf708de1257414bb375d5a552b722"
S = "${WORKDIR}/git/ncsid"

inherit meson systemd

SYSTEMD_SERVICE_${PN} += " \
  dhcp4@.service \
  dhcp6@.service \
  ncsid@.service \
  nic-hostful@.target \
  nic-hostless@.target \
  update-static-neighbors@.service \
  update-static-neighbors@.timer \
"

DEPENDS += " \
  fmt \
  sdbusplus \
  stdplus \
"

RDEPENDS_${PN} += " \
  bash \
  busybox \
  iputils-arping \
  jq \
  ndisc6-ndisc6 \
  ndisc6-rdisc6 \
  systemd \
"
