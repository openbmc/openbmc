FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
inherit obmc-phosphor-systemd

SRC_URI += "file://psu.json"
SRC_URI:append:buv-runbmc = " file://0001-use-interface-xyz-openbmc_project.patch"
SRC_URI:append:buv-runbmc = " file://0002-powerOn-always-return-true.patch"
SRC_URI:append:buv-runbmc = " file://0003-Modify-power-supply-monitor-.service.patch"
SRC_URI:append:buv-runbmc = " file://0004-Fan-fault-test.patch"
SRC_URI:append:buv-runbmc = " file://0005-Add-create-PSU-DBUs-obj.patch"
SRC_URI:append:buv-runbmc = " file://0006-Light-up-LED-when-fan-fault.patch"
SRC_URI:append:buv-runbmc = " file://0007-Get-powersupplyName-from-config-setting.patch"

PACKAGECONFIG:append:buv-runbmc = " monitor"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}-monitor:append:buv-runbmc = " ${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

do_install:append:buv-runbmc(){
    install -d ${D}${datadir}/phosphor-power
    install -m 0644 -D ${WORKDIR}/psu.json \
        ${D}${datadir}/phosphor-power/psu.json
}
FILES:${PN} += "${datadir}/phosphor-power/psu.json"