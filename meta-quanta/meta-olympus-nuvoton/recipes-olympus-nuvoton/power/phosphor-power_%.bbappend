FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
inherit obmc-phosphor-systemd
inherit entity-utils

SRC_URI += "file://psu.json"
SRC_URI:append:olympus-nuvoton = " file://psu_em.json"
SRC_URI:append:olympus-nuvoton = " file://0001-use-interface-xyz-openbmc_project.patch"
SRC_URI:append:olympus-nuvoton = " file://0002-powerOn-always-return-true.patch"
SRC_URI:append:olympus-nuvoton = " file://0003-Modify-power-supply-monitor-.service.patch"
SRC_URI:append:olympus-nuvoton = " file://0004-Fan-fault-test.patch"
SRC_URI:append:olympus-nuvoton = " file://0005-Add-create-PSU-DBUs-obj.patch"
SRC_URI:append:olympus-nuvoton = " file://0006-Light-up-LED-when-fan-fault.patch"
SRC_URI:append:olympus-nuvoton = " file://0007-support-Entity-Manager.patch"
SRC_URI:append:olympus-nuvoton = " file://0008-Get-powersupplyName-from-config-setting.patch"

EXTRA_OEMESON:append:olympus-nuvoton = " \
    ${@entity_enabled(d, '-Dnuvoton-entity=true', '')} \
    "

POWER_SERVICE_PACKAGES += " ${PN}-monitor-em"
PACKAGECONFIG[monitor-em] = "-Dsupply-monitor-em=true, -Dsupply-monitor-em=false"
PACKAGECONFIG:append:olympus-nuvoton = " \
    ${@entity_enabled(d, ' monitor-em', ' monitor')} \
    "

PSU_MONITOR_TMPL = "${@entity_enabled(d, 'power-supply-monitor-em@.service', 'power-supply-monitor@.service')}"
SYSTEMD_SERVICE:${PN}-monitor-em = "${@bb.utils.contains('PACKAGECONFIG', 'monitor-em', '${PSU_MONITOR_TMPL}', '', d)}"

PSU_MONITOR_ENV_FMT = " \
    ${@entity_enabled(d, '', 'obmc/power-supply-monitor/power-supply-monitor-{0}.conf')} \
    "
PSU_MONITOR_EM_ENV_FMT = " \
    ${@entity_enabled(d, 'obmc/power-supply-monitor/power-supply-monitor-em-{0}.conf', '')} \
    "
SYSTEMD_ENVIRONMENT_FILE:${PN}-monitor:append:olympus-nuvoton = "${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}-monitor-em:append:olympus-nuvoton = "${@compose_list(d, 'PSU_MONITOR_EM_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

FILES:${PN}-monitor = " \
    ${@entity_enabled(d, '', '${bindir}/psu-monitor')} \
    "
FILES:${PN}-monitor-em = " \
    ${@entity_enabled(d, '${bindir}/psu-monitor', '')} \
    "

do_install:append:olympus-nuvoton(){
    install -d ${D}${datadir}/phosphor-power
    if [ "${DISTRO}" != "olympus-entity" ];then
        install -m 0644 -D ${WORKDIR}/psu.json \
            ${D}${datadir}/phosphor-power/psu.json
    else
        install -m 0644 -D ${WORKDIR}/psu_em.json \
            ${D}${datadir}/phosphor-power/psu.json
    fi
}
FILES:${PN} += "${datadir}/phosphor-power/psu.json"
