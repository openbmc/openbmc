SUMMARY = "Google NCSI daemon"
DESCRIPTION = "Google NCSI daemon."
GOOGLE_MISC_PROJ = "ncsid"

require ../google-misc/google-misc.inc

inherit systemd

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

SYSTEMD_SERVICE:${PN} += " \
  ncsid@.service \
  nic-hostful@.target \
  nic-hostless@.target \
  "

DEPENDS += " \
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
