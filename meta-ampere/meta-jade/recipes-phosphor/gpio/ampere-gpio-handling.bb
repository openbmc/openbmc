SUMMARY = "Ampere Computing LLC Host Gpio Handling"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "phosphor-gpio-monitor"
RDEPENDS:${PN} += "phosphor-gpio-monitor-monitor"
RDEPENDS:${PN} += "bash"

AMPERE_MONITOR_INSTANCES = " \
                            S0_hightemp_start S0_hightemp_stop \
                            S1_hightemp_start S1_hightemp_stop \
                           "

SYSTEMD_ENVIRONMENT_FILE:${PN} +=" \
                                  obmc/gpio/S0_hightemp_start \
                                  obmc/gpio/S0_hightemp_stop \
                                  obmc/gpio/S1_hightemp_start \
                                  obmc/gpio/S1_hightemp_stop \
                                 "

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += " \
                          ampere_hightemp_start@.service \
                          ampere_hightemp_stop@.service \
                         "
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'AMPERE_MONITOR_INSTANCES')}"

