FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI += "file://psu.json"

EXTRA_OEMESON_append_ibm-ac-server = " -Ducd90160-yaml=${STAGING_DIR_HOST}${datadir}/power-sequencer/ucd90160.yaml"
EXTRA_OEMESON_append_rainier = " -Ducd90160-yaml=${STAGING_DIR_HOST}${datadir}/power-sequencer/ucd90160.yaml -Dibm-vpd=true"
EXTRA_OEMESON_append_mihawk = " -Dpower_sequencer=mihawk-cpld"

DEPENDS_append_ibm-ac-server = " power-sequencer"
DEPENDS_append_rainier = " power-sequencer"

do_install_append(){
    install -D ${WORKDIR}/psu.json ${D}${datadir}/phosphor-power/psu.json
}
FILES_${PN} += "${datadir}/phosphor-power/psu.json"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN}-monitor_append_ibm-ac-server += "${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
