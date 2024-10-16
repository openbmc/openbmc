SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-inflection-native"
DEPENDS += "cli11"
DEPENDS += "libcereal"
DEPENDS += "nlohmann-json"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "systemd"
PACKAGECONFIG ??= "\
    ${@oe.utils.conditional( \
        'PREFERRED_PROVIDER_virtual/${PN}-config-native', \
        'phosphor-led-manager-config-example-native', \
        'use-json', 'use-yaml', d)} \
"
PACKAGECONFIG[use-json] = "-Duse-json=enabled,,,,,use-yaml"
PACKAGECONFIG[use-yaml] = "-Duse-json=disabled,,virtual/${PN}-config-native,,,use-json"
PACKAGECONFIG[use-lamp-test] = "-Duse-lamp-test=enabled, -Duse-lamp-test=disabled"
PACKAGECONFIG[monitor-operational-status] = "-Dmonitor-operational-status=enabled, \
                                             -Dmonitor-operational-status=disabled"
PACKAGECONFIG[persistent-led-asserted] = "-Dpersistent-led-asserted=enabled, \
                                          -Dpersistent-led-asserted=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

LED_ORG_JSON_PATTERNS ??= "${@ d.getVar('OBMC_ORG_YAML_SUBDIRS').replace('/', '.')}"
LED_CONFIG_GREP_ARGS = "${@ ''.join([ ' -e ' + x for x in d.getVar('LED_ORG_JSON_PATTERNS').split() ])}"
do_install:append() {
    for f in "${D}${datadir}/${PN}/"*.json ;
    do
        if ! echo "$(basename $f)" | grep -q ${LED_CONFIG_GREP_ARGS};
        then
            rm -f ${f}
        fi
    done
}

SYSTEMD_PACKAGES = "${PN} ${PN}-faultmonitor"
S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "obmc-led-group-start@.service obmc-led-group-stop@.service"
SYSTEMD_SERVICE:${PN}-faultmonitor += "obmc-fru-fault-monitor.service"
SYSTEMD_LINK:${PN} += "../obmc-led-group-start@.service:multi-user.target.wants/obmc-led-group-start@bmc_booted.service"
STATES = "start stop"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'FMT', 'CHASSIS_TARGETS', 'STATES')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'CHASSIS_LED_BLACKOUT_FMT', 'OBMC_CHASSIS_INSTANCES' )}"
SYSTEMD_LINK[vardeps] += "OBMC_CHASSIS_INSTANCES"
# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE:${PN} += "bmc_booted.conf:obmc-led-group-start@bmc_booted.service.d/bmc_booted.conf"

inherit meson pkgconfig python3native
inherit obmc-phosphor-dbus-service obmc-phosphor-systemd

EXTRA_OEMESON:append = " -Dtests=disabled"

do_compile:prepend() {
    if [ -f "${LED_YAML_PATH}/led.yaml" ]; then
        cp "${LED_YAML_PATH}/led.yaml" "${S}/led.yaml"
    elif [ -f "${STAGING_DATADIR_NATIVE}/${PN}/led.yaml" ]; then
        cp "${STAGING_DATADIR_NATIVE}/${PN}/led.yaml" "${S}/led.yaml"
    elif [ -f "${UNPACKDIR}/led.yaml" ]; then
        cp "${UNPACKDIR}/led.yaml" "${S}/led.yaml"
    fi
}

RDEPENDS:${PN} += "bash"

FILES:${PN}-faultmonitor += "${bindir}/phosphor-fru-fault-monitor"

require ${PN}.inc

PACKAGE_BEFORE_PN += "${PN}-faultmonitor"
DBUS_SERVICE:${PN} += "xyz.openbmc_project.LED.GroupManager.service"
CHASSIS_TARGETS = "poweron poweroff"
TMPLFMT = "obmc-led-group-{1}@.service"
TGTFMT = "obmc-chassis-{0}@0.target"
INSTFMT = "obmc-led-group-{1}@power_on.service"
FMT = "../${TMPLFMT}:${TGTFMT}.wants/${INSTFMT}"
CHASSIS_BLACKOUT_TGT = "obmc-chassis-blackout@{0}.target"
LED_STOP_SVC = "obmc-led-group-stop@.service"
LED_POWER_STOP_SVC = "obmc-led-group-stop@power_on.service"
CHASSIS_LED_BLACKOUT_FMT = "../${LED_STOP_SVC}:${CHASSIS_BLACKOUT_TGT}.wants/${LED_POWER_STOP_SVC}"
