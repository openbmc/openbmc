FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
inherit obmc-phosphor-systemd

SRC_URI += "file://psu.json"
SRC_URI:append:olympus-nuvoton = " file://0001-use-interface-xyz-openbmc_project.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-powerOn-always-return-true.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-Modify-power-supply-monitor-.service.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-Fan-fault-test.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-Add-create-PSU-DBUs-obj.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-Light-up-LED-when-fan-fault.patch"

PACKAGECONFIG:append:olympus-nuvoton = " monitor"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}-monitor:append:olympus-nuvoton = " ${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

do_install:append:olympus-nuvoton(){
    install -d ${D}${datadir}/phosphor-power
    install -m 0644 -D ${WORKDIR}/psu.json \
        ${D}${datadir}/phosphor-power/psu.json
}
FILES:${PN} += "${datadir}/phosphor-power/psu.json"