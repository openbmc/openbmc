SUMMARY = "Phosphor Fan"
DESCRIPTION = "Phosphor fan provides a set of fan monitoring and \
control applications."
# Common build dependencies
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdeventplus"
DEPENDS += "gpioplus"
DEPENDS += "phosphor-logging"
DEPENDS += "libevdev"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"
PACKAGECONFIG ?= "presence control monitor sensor-monitor"
# The control, monitor, and presence apps can either be JSON or YAML driven.
PACKAGECONFIG[json] = "-Djson-config=enabled, -Djson-config=disabled"
# --------------------------------------
# ${PN}-presence-tach specific configuration
PACKAGECONFIG[presence] = "-Dpresence-service=enabled \
    -Dmachine-name=${PKG_DEFAULT_MACHINE} \
    -Dpresence-config=${STAGING_DIR_HOST}${presence_datadir}/config.yaml, \
    -Dpresence-service=disabled, \
    virtual/phosphor-fan-presence-config \
    , \
"
# --------------------------------------
# ${PN}-control specific configuration
PACKAGECONFIG[control] = "-Dcontrol-service=enabled \
    -Dmachine-name=${PKG_DEFAULT_MACHINE} \
    -Dfan-def-yaml-file=${STAGING_DIR_HOST}${control_datadir}/fans.yaml \
    -Dfan-zone-yaml-file=${STAGING_DIR_HOST}${control_datadir}/zones.yaml \
    -Dzone-events-yaml-file=${STAGING_DIR_HOST}${control_datadir}/events.yaml \
    -Dzone-conditions-yaml-file=${STAGING_DIR_HOST}${control_datadir}/zone_conditions.yaml, \
    -Dcontrol-service=disabled, \
    virtual/phosphor-fan-control-fan-config \
    phosphor-fan-control-zone-config \
    phosphor-fan-control-events-config \
    phosphor-fan-control-zone-conditions-config \
    , \
"
# --------------------------------------
# ${PN}-monitor specific configuration
PACKAGECONFIG[monitor] = "-Dmonitor-service=enabled \
    -Dmachine-name=${PKG_DEFAULT_MACHINE} \
    -Dfan-monitor-yaml-file=${STAGING_DIR_HOST}${monitor_datadir}/monitor.yaml, \
    -Dmonitor-service=disabled, \
    phosphor-fan-monitor-config \
    , \
"
RDEPENDS:${PN}-monitor:append = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'sensor-monitor', '${PN}-sensor-monitor', '', d)} \
"
# --------------------------------------
# phosphor-cooling-type specific configuration
PACKAGECONFIG[cooling-type] = "-Dcooling-type-service=enabled,-Dcooling-type-service=disabled,,"
# --------------------------------------
# ${PN}-sensor-monitor specific configuration
PACKAGECONFIG[sensor-monitor] = "-Dsensor-monitor-service=enabled,-Dsensor-monitor-service=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

# OBMC_CHASSIS_ZERO_ONLY: hacky way to fix the templates until
# openbmc/phosphor-fan-presence#26 is resolved.  This should likely be
# returned to OBMC_CHASSIS_INSTANCES.
OBMC_CHASSIS_ZERO_ONLY = "0"

SYSTEMD_PACKAGES = "${FAN_PACKAGES}"
SYSTEMD_SERVICE:${PN}-presence-tach += "${TMPL_TACH}"
SYSTEMD_LINK:${PN}-presence-tach += "${@compose_list(d, 'FMT_TACH', 'OBMC_CHASSIS_ZERO_ONLY')}"
# JSON mode also gets linked into multi-user
SYSTEMD_LINK:${PN}-presence-tach += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
        compose_list(d, 'FMT_TACH_MUSR', 'OBMC_CHASSIS_ZERO_ONLY'), '', d)}"
SYSTEMD_SERVICE:${PN}-control += "${TMPL_CONTROL}"
SYSTEMD_SERVICE:${PN}-control += "${@bb.utils.contains('PACKAGECONFIG', 'json', '', '${TMPL_CONTROL_INIT}', d)}"
# JSON: Linked to multi-user and poweron
# YAML: Linked to fans-ready and fan control-init poweron
SYSTEMD_LINK:${PN}-control += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
        compose_list(d, 'FMT_CONTROL_MUSR', 'OBMC_CHASSIS_ZERO_ONLY'), \
        compose_list(d, 'FMT_CONTROL', 'OBMC_CHASSIS_ZERO_ONLY'), d)}"
SYSTEMD_LINK:${PN}-control += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
        compose_list(d, 'FMT_CONTROL_PWRON', 'OBMC_CHASSIS_ZERO_ONLY'), \
        compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_ZERO_ONLY'), d)}"
SYSTEMD_SERVICE:${PN}-monitor += "${TMPL_MONITOR}"
SYSTEMD_SERVICE:${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', '', '${TMPL_MONITOR_INIT}', d)}"

# JSON: power on and multi-user links.  YAML: fans-ready and fan monitor init links
SYSTEMD_LINK:${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
                                compose_list(d, 'FMT_MONITOR_PWRON', 'OBMC_CHASSIS_ZERO_ONLY'), \
                                compose_list(d, 'FMT_MONITOR_FANSREADY', 'OBMC_CHASSIS_ZERO_ONLY'), d)}"
SYSTEMD_LINK:${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
                                compose_list(d, 'FMT_MONITOR_MUSR', 'OBMC_CHASSIS_ZERO_ONLY'), \
                                compose_list(d, 'FMT_MONITOR_INIT', 'OBMC_CHASSIS_ZERO_ONLY'), d)}"
SYSTEMD_SERVICE:${PN}-sensor-monitor += "sensor-monitor.service"
SYSTEMD_LINK:${PN}-sensor-monitor += "../sensor-monitor.service:${MULTI_USR_TGT}.wants/sensor-monitor.service"

inherit meson pkgconfig python3native
inherit obmc-phosphor-systemd
inherit phosphor-fan

EXTRA_OEMESON:append = " -Dtests=disabled"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN}-presence-tach = "${bindir}/phosphor-fan-presence-tach"
# Package the JSON config files installed from the repo
FILES:${PN}-presence-tach += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
    '${datadir}/phosphor-fan-presence/presence/*', '', d)}"
FILES:${PN}-control = "${bindir}/phosphor-fan-control"
FILES:${PN}-control += "${bindir}/fanctl"
# Package the JSON config files installed from the repo
FILES:${PN}-control += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
    '${datadir}/phosphor-fan-presence/control/*', '', d)}"
FILES:${PN}-monitor = "${bindir}/phosphor-fan-monitor"
# Package the JSON config files installed from the repo
FILES:${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
    '${datadir}/phosphor-fan-presence/monitor/*', '', d)}"
FILES:${PN}-sensor-monitor += " \
    ${bindir}/sensor-monitor \
    ${@bb.utils.contains('PACKAGECONFIG', 'json', '${datadir}/phosphor-fan-presence/sensor-monitor/*', '', d)} \
"

require ${BPN}.inc

ALLOW_EMPTY:${PN} = "1"

PKG_DEFAULT_MACHINE ??= "${MACHINE}"

# Package configuration
FAN_PACKAGES = " \
        ${PN}-presence-tach \
        ${PN}-control \
        ${PN}-monitor \
        ${PN}-sensor-monitor \
"
PACKAGE_BEFORE_PN += "${FAN_PACKAGES}"
MULTI_USR_TGT = "multi-user.target"
TMPL_TACH = "phosphor-fan-presence-tach@.service"
INSTFMT_TACH = "phosphor-fan-presence-tach@{0}.service"
POWERON_TGT = "obmc-chassis-poweron@{0}.target"
FMT_TACH = "../${TMPL_TACH}:${POWERON_TGT}.wants/${INSTFMT_TACH}"
FMT_TACH_MUSR = "../${TMPL_TACH}:${MULTI_USR_TGT}.wants/${INSTFMT_TACH}"
FAN_CONTROL_TGT = "obmc-fan-control-ready@{0}.target"
TMPL_CONTROL = "phosphor-fan-control@.service"
INSTFMT_CONTROL = "phosphor-fan-control@{0}.service"
FMT_CONTROL = "../${TMPL_CONTROL}:${FAN_CONTROL_TGT}.requires/${INSTFMT_CONTROL}"
FMT_CONTROL_MUSR = "../${TMPL_CONTROL}:${MULTI_USR_TGT}.wants/${INSTFMT_CONTROL}"
FMT_CONTROL_PWRON = "../${TMPL_CONTROL}:${POWERON_TGT}.wants/${INSTFMT_CONTROL}"
TMPL_CONTROL_INIT = "phosphor-fan-control-init@.service"
INSTFMT_CONTROL_INIT = "phosphor-fan-control-init@{0}.service"
FMT_CONTROL_INIT = "../${TMPL_CONTROL_INIT}:${POWERON_TGT}.wants/${INSTFMT_CONTROL_INIT}"
TMPL_MONITOR = "phosphor-fan-monitor@.service"
INSTFMT_MONITOR = "phosphor-fan-monitor@{0}.service"
FMT_MONITOR_FANSREADY = "../${TMPL_MONITOR}:${FAN_CONTROL_TGT}.requires/${INSTFMT_MONITOR}"
FMT_MONITOR_PWRON = "../${TMPL_MONITOR}:${POWERON_TGT}.wants/${INSTFMT_MONITOR}"
FMT_MONITOR_MUSR = "../${TMPL_MONITOR}:${MULTI_USR_TGT}.wants/${INSTFMT_MONITOR}"
TMPL_MONITOR_INIT = "phosphor-fan-monitor-init@.service"
INSTFMT_MONITOR_INIT = "phosphor-fan-monitor-init@{0}.service"
FMT_MONITOR_INIT = "../${TMPL_MONITOR_INIT}:${POWERON_TGT}.wants/${INSTFMT_MONITOR_INIT}"
