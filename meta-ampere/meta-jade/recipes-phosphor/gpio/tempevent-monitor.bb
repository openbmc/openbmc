SUMMARY = "Ampere Computing LLC Host temperature event monitor application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd
inherit allarch

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS:${PN} += "virtual/obmc-gpio-monitor"

OBMC_HOST_MONITOR_INSTANCES = "S0_overtemp S1_overtemp S0_hightemp_start S0_hightemp_stop S1_hightemp_start S1_hightemp_stop"

# Copies config file having arguments for monitoring host overtemp
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE:${PN} += " \
                                   obmc/gpio/S0_overtemp \
                                   obmc/gpio/S0_hightemp_start \
                                   obmc/gpio/S0_hightemp_stop \
                                   obmc/gpio/S1_overtemp \
                                   obmc/gpio/S1_hightemp_start \
                                   obmc/gpio/S1_hightemp_stop \
                                  "


SYSTEMD_SERVICE:${PN} = "ampere_overtemp@.service ampere_hightemp_start@.service ampere_hightemp_stop@.service"

GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
TEMPEVENT_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:${TGT}.requires/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'TEMPEVENT_MONITOR_FMT', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
