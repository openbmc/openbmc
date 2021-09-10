SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
PR = "r1"
PV = "1.0+git${SRCPV}"

require ${PN}.inc

inherit meson pkgconfig python3native
inherit obmc-phosphor-dbus-service obmc-phosphor-systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[use-json] = "-Duse-json=enabled, -Duse-json=disabled"
PACKAGECONFIG[use-lamp-test] = "-Duse-lamp-test=enabled, -Duse-lamp-test=disabled"
PACKAGECONFIG[monitor-operational-status] = "-Dmonitor-operational-status=enabled, \
                                             -Dmonitor-operational-status=disabled"

SYSTEMD_PACKAGES = "${PN} ${PN}-faultmonitor"
PACKAGE_BEFORE_PN += "${PN}-faultmonitor"

DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-inflection-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"

DEPENDS += "virtual/${PN}-config-native"

RDEPENDS:${PN} += "bash"

S = "${WORKDIR}/git"

FILES:${PN}-faultmonitor += "${bindir}/phosphor-fru-fault-monitor"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.LED.GroupManager.service"

SYSTEMD_SERVICE:${PN} += "obmc-led-group-start@.service obmc-led-group-stop@.service"
SYSTEMD_SERVICE:${PN}-faultmonitor += "obmc-fru-fault-monitor.service"

SYSTEMD_LINK:${PN} += "../obmc-led-group-start@.service:multi-user.target.wants/obmc-led-group-start@bmc_booted.service"

CHASSIS_TARGETS = "poweron poweroff"
STATES = "start stop"
TMPLFMT = "obmc-led-group-{1}@.service"
TGTFMT = "obmc-chassis-{0}@0.target"
INSTFMT = "obmc-led-group-{1}@power_on.service"
FMT = "../${TMPLFMT}:${TGTFMT}.wants/${INSTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'FMT', 'CHASSIS_TARGETS', 'STATES')}"

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE:${PN} += "bmc_booted.conf:obmc-led-group-start@bmc_booted.service.d/bmc_booted.conf"

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
