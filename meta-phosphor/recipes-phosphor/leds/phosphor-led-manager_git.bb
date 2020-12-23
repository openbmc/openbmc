SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
PR = "r1"
PV = "1.0+git${SRCPV}"

require ${BPN}.inc

inherit autotools pkgconfig python3native
inherit obmc-phosphor-dbus-service obmc-phosphor-systemd

LED_MGR_PACKAGES = " \
    ${PN}-ledmanager \
    ${PN}-faultmonitor \
"

PACKAGE_BEFORE_PN += "${LED_MGR_PACKAGES}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${PN}-ledmanager"

SYSTEMD_PACKAGES = "${LED_MGR_PACKAGES}"

DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-inflection-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"

DEPENDS += "virtual/${PN}-config-native"

RDEPENDS_${PN}-ledmanager += "bash"

S = "${WORKDIR}/git"

FILES_${PN}-ledmanager += "${bindir}/phosphor-ledmanager ${bindir}/led-set-all-groups-asserted.sh"
FILES_${PN}-faultmonitor += "${bindir}/phosphor-fru-fault-monitor"

DBUS_SERVICE_${PN}-ledmanager += "xyz.openbmc_project.LED.GroupManager.service"

SYSTEMD_SERVICE_${PN}-ledmanager += "obmc-led-group-start@.service obmc-led-group-stop@.service"
SYSTEMD_SERVICE_${PN}-faultmonitor += "obmc-fru-fault-monitor.service"

SYSTEMD_LINK_${PN}-ledmanager += "../obmc-led-group-start@.service:multi-user.target.wants/obmc-led-group-start@bmc_booted.service"

STATES = "start stop"
TMPLFMT = "obmc-led-group-{0}@.service"
TGTFMT = "obmc-power-{0}@0.target"
INSTFMT = "obmc-led-group-{0}@power_on.service"
FMT = "../${TMPLFMT}:${TGTFMT}.wants/${INSTFMT}"
SYSTEMD_LINK_${PN}-ledmanager += "${@compose_list(d, 'FMT', 'STATES')}"

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE_${PN}-ledmanager += "bmc_booted.conf:obmc-led-group-start@bmc_booted.service.d/bmc_booted.conf"

EXTRA_OECONF = "YAML_PATH=${STAGING_DATADIR_NATIVE}/${PN}"
