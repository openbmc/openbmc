SUMMARY = "Ampere Computing LLC Host Gpio Handling"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS:${PN} += "virtual/obmc-gpio-monitor"
RDEPENDS:${PN} += "bash"

SRC_URI += " \
            file://toggle_fault_led.sh \
            file://ampere_psu_reset_hotswap.sh \
            file://toggle_identify_led.sh \
            file://ampere_scp_failover.sh \
           "

AMPERE_MONITOR_INSTANCES = " \
                            reboot_ack shutdown_ack id_button S0_scp_auth_failure \
                            S0_overtemp S0_hightemp_start S0_hightemp_stop \
                            S1_overtemp S1_hightemp_start S1_hightemp_stop \
                            S0_fault_alert_start S0_fault_alert_stop \
                            S1_fault_alert_start S1_fault_alert_stop \
                            PSU1_VIN_GOOD PSU2_VIN_GOOD \
                           "

SYSTEMD_ENVIRONMENT_FILE:${PN} +=" \
                                  obmc/gpio/reboot_ack \
                                  obmc/gpio/shutdown_ack \
                                  obmc/gpio/id_button \
                                  obmc/gpio/S0_scp_auth_failure \
                                  obmc/gpio/S0_overtemp \
                                  obmc/gpio/S0_hightemp_start \
                                  obmc/gpio/S0_hightemp_stop \
                                  obmc/gpio/S1_overtemp \
                                  obmc/gpio/S1_hightemp_start \
                                  obmc/gpio/S1_hightemp_stop \
                                  obmc/gpio/S0_fault_alert_start \
                                  obmc/gpio/S0_fault_alert_stop \
                                  obmc/gpio/S1_fault_alert_start \
                                  obmc/gpio/S1_fault_alert_stop \
                                  obmc/gpio/PSU1_VIN_GOOD \
                                  obmc/gpio/PSU2_VIN_GOOD \
                                 "

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += " \
                          ampere-host-shutdown-ack@.service \
                          ampere_overtemp@.service \
                          ampere_hightemp_start@.service \
                          ampere_hightemp_stop@.service \
                          ampere_fault_led_start@.service \
                          ampere_fault_led_stop@.service \
                          psu_hotswap_reset@.service \
                          id-button-pressed.service \
                          ampere_scp_failover.service \
                         "
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'AMPERE_MONITOR_INSTANCES')}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/toggle_fault_led.sh ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/toggle_identify_led.sh ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_psu_reset_hotswap.sh ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_scp_failover.sh ${D}${sbindir}/
}
