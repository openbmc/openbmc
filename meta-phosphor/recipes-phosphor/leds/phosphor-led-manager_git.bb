SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-inflection-native"
DEPENDS += "cli11"
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
PV = "1.0+git${SRCPV}"
PR = "r1"

SYSTEMD_PACKAGES = "${PN} ${PN}-faultmonitor"
S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "obmc-led-group-start@.service obmc-led-group-stop@.service"
SYSTEMD_SERVICE:${PN}-faultmonitor += "obmc-fru-fault-monitor.service"
SYSTEMD_LINK:${PN} += "../obmc-led-group-start@.service:multi-user.target.wants/obmc-led-group-start@bmc_booted.service"
STATES = "start stop"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'FMT', 'CHASSIS_TARGETS', 'STATES')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'CHASSIS_LED_BLACKOUT_FMT', 'OBMC_CHASSIS_INSTANCES' )}"
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
    elif [ -f "${WORKDIR}/led.yaml" ]; then
        cp "${WORKDIR}/led.yaml" "${S}/led.yaml"
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
