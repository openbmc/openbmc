FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI += "file://psu.json"

EXTRA_OEMESON:append:ibm-ac-server = " -Ducd90160-yaml=${STAGING_DIR_HOST}${datadir}/power-sequencer/ucd90160.yaml"
EXTRA_OEMESON:append:p10bmc = " -Ducd90160-yaml=${STAGING_DIR_HOST}${datadir}/power-sequencer/ucd90160.yaml -Dibm-vpd=true"
EXTRA_OEMESON:append:mihawk = " -Dpower_sequencer=mihawk-cpld"

DEPENDS:append:ibm-ac-server = " power-sequencer"
DEPENDS:append:p10bmc = " power-sequencer"

PACKAGECONFIG:append:ibm-ac-server = " monitor"
PACKAGECONFIG:append:swift = " monitor"
PACKAGECONFIG:append:mihawk = " monitor"

do_install:append(){
    install -D ${WORKDIR}/psu.json ${D}${datadir}/phosphor-power/psu.json
}
FILES:${PN} += "${datadir}/phosphor-power/psu.json"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}-monitor:append:ibm-ac-server += "${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
