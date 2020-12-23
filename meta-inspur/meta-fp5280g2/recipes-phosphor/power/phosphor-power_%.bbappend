FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
inherit obmc-phosphor-systemd

SRC_URI += "file://psu.json"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN}-monitor_append_fp5280g2 = " ${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

do_install_append() {
    install -D ${WORKDIR}/psu.json ${D}${datadir}/phosphor-power/psu.json
}

FILES_${PN} += "${datadir}/phosphor-power/psu.json"
