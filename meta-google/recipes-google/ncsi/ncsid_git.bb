SUMMARY = "Google NCSI daemon"
DESCRIPTION = "Google NCSI daemon."
GOOGLE_MISC_PROJ = "ncsid"

require ../google-misc/google-misc.inc

inherit systemd

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

SYSTEMD_SERVICE:${PN} += " \
  dhcp4@.service \
  dhcp6@.service \
  ncsid@.service \
  nic-hostful@.target \
  nic-hostless@.target \
  update-ra-gw@.service \
  update-ra-neighbor@.service \
  update-ra-neighbor@.timer \
  update-static-neighbors@.service \
  update-static-neighbors@.timer \
  "

DEPENDS += " \
  fmt \
  sdbusplus \
  stdplus \
  "

RDEPENDS:${PN} += " \
  bash \
  busybox \
  iputils-arping \
  jq \
  ndisc6-ndisc6 \
  ndisc6-rdisc6 \
  systemd \
  "
