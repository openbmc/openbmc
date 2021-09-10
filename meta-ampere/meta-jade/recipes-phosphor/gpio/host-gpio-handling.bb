SUMMARY = "Ampere Computing LLC Host Gpio Handling"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS:${PN} += "virtual/obmc-gpio-monitor"

OBMC_HOST_ACK_MONITOR_INSTANCES = "reboot_ack shutdown_ack"

SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/gpio/reboot_ack \
                                  obmc/gpio/shutdown_ack \
                                 "

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += "ampere-host-shutdown-ack@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_ACK_MONITOR_INSTANCES')}"
