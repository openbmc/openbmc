SUMMARY = "Ampere Computing LLC Host temperature event monitor application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd
inherit allarch

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

OBMC_HOST_MONITOR_INSTANCES = "overtemp_S0  hightemp_start_S0 hightemp_stop_S0 overtemp_S1  hightemp_start_S1 hightemp_stop_S1"

# Copies config file having arguments for monitoring host overtemp
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/overtemp_S0 \
                                  obmc/gpio/hightemp_start_S0 \
                                  obmc/gpio/hightemp_stop_S0 \
                                  obmc/gpio/overtemp_S1 \
                                  obmc/gpio/hightemp_start_S1 \
                                  obmc/gpio/hightemp_stop_S1 \
                                 "

SYSTEMD_SERVICE_${PN} ?= "ampere_hightemp_start.service ampere_hightemp_stop.service ampere_overtemp.service"

GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
TEMPEVENT_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:${TGT}.requires/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'TEMPEVENT_MONITOR_FMT', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
