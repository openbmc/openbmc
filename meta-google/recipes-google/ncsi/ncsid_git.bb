SUMMARY = "Google NCSI daemon"
DESCRIPTION = "Google NCSI daemon."
GOOGLE_MISC_PROJ = "ncsid"

require ../google-misc/google-misc.inc

inherit obmc-phosphor-utils
inherit systemd

OBMC_NETWORK_INTERFACES ?= "eth0"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

SYSTEMD_SERVICE_FMT = " \
    ncsid@{0}.service \
    nic-hostful@{0}.target \
    nic-hostless@{0}.target \
    "

SYSTEMD_SERVICE:${PN} += "${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_NETWORK_INTERFACES')}"

FILES:${PN} += " \
    ${systemd_system_unitdir}/ncsid@.service \
    ${systemd_system_unitdir}/nic-hostful@.target \
    ${systemd_system_unitdir}/nic-hostless@.target \
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
