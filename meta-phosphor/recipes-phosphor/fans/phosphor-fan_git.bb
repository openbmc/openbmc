SUMMARY = "Phosphor Fan"
DESCRIPTION = "Phosphor fan provides a set of fan monitoring and \
control applications."
PR = "r1"
PV = "1.0+git${SRCPV}"

require ${BPN}.inc

inherit autotools pkgconfig python3native
inherit obmc-phosphor-systemd
inherit phosphor-fan

S = "${WORKDIR}/git"

# Common build dependencies
DEPENDS += "autoconf-archive-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdeventplus"
DEPENDS += "gpioplus"
DEPENDS += "phosphor-logging"
DEPENDS += "libevdev"
DEPENDS += "nlohmann-json"

# Package configuration
FAN_PACKAGES = " \
        ${PN}-presence-tach \
        ${PN}-control \
        ${PN}-monitor \
"

ALLOW_EMPTY_${PN} = "1"
PACKAGE_BEFORE_PN += "${FAN_PACKAGES}"
PACKAGECONFIG ?= "presence control monitor"
SYSTEMD_PACKAGES = "${FAN_PACKAGES}"

# The control, monitor, and presence apps can either be JSON or YAML driven.
PACKAGECONFIG[json] = "--enable-json, --disable-json"

# --------------------------------------
# ${PN}-presence-tach specific configuration
PACKAGECONFIG[presence] = " \
        --enable-presence \
        PRESENCE_CONFIG=${STAGING_DIR_HOST}${presence_datadir}/config.yaml, \
        --disable-presence, \
        virtual/phosphor-fan-presence-config \
        , \
"

MULTI_USR_TGT = "multi-user.target"
TMPL_TACH = "phosphor-fan-presence-tach@.service"
INSTFMT_TACH = "phosphor-fan-presence-tach@{0}.service"
POWERON_TGT = "obmc-chassis-poweron@{0}.target"
FMT_TACH = "../${TMPL_TACH}:${POWERON_TGT}.requires/${INSTFMT_TACH}"
FMT_TACH_MUSR = "../${TMPL_TACH}:${MULTI_USR_TGT}.wants/${INSTFMT_TACH}"

FILES_${PN}-presence-tach = "${bindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-presence-tach += "${TMPL_TACH}"
SYSTEMD_LINK_${PN}-presence-tach += "${@compose_list(d, 'FMT_TACH', 'OBMC_CHASSIS_INSTANCES')}"

# JSON mode also gets linked into multi-user
SYSTEMD_LINK_${PN}-presence-tach += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
        compose_list(d, 'FMT_TACH_MUSR', 'OBMC_CHASSIS_INSTANCES'), '', d)}"

# --------------------------------------
# ${PN}-control specific configuration
PACKAGECONFIG[control] = "--enable-control \
     FAN_DEF_YAML_FILE=${STAGING_DIR_HOST}${control_datadir}/fans.yaml \
     FAN_ZONE_YAML_FILE=${STAGING_DIR_HOST}${control_datadir}/zones.yaml \
     ZONE_EVENTS_YAML_FILE=${STAGING_DIR_HOST}${control_datadir}/events.yaml \
     ZONE_CONDITIONS_YAML_FILE=${STAGING_DIR_HOST}${control_datadir}/zone_conditions.yaml, \
    --disable-control, \
    virtual/phosphor-fan-control-fan-config \
    phosphor-fan-control-zone-config \
    phosphor-fan-control-events-config \
    phosphor-fan-control-zone-conditions-config \
    , \
"

FAN_CONTROL_TGT = "obmc-fan-control-ready@{0}.target"

TMPL_CONTROL = "phosphor-fan-control@.service"
INSTFMT_CONTROL = "phosphor-fan-control@{0}.service"
FMT_CONTROL = "../${TMPL_CONTROL}:${FAN_CONTROL_TGT}.requires/${INSTFMT_CONTROL}"

TMPL_CONTROL_INIT = "phosphor-fan-control-init@.service"
INSTFMT_CONTROL_INIT = "phosphor-fan-control-init@{0}.service"
FMT_CONTROL_INIT = "../${TMPL_CONTROL_INIT}:${POWERON_TGT}.wants/${INSTFMT_CONTROL_INIT}"

FILES_${PN}-control = "${bindir}/phosphor-fan-control"
SYSTEMD_SERVICE_${PN}-control += "${TMPL_CONTROL} ${TMPL_CONTROL_INIT}"
SYSTEMD_LINK_${PN}-control += "${@compose_list(d, 'FMT_CONTROL', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-control += "${@compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_INSTANCES')}"

# --------------------------------------
# ${PN}-monitor specific configuration
PACKAGECONFIG[monitor] = "--enable-monitor \
     FAN_MONITOR_YAML_FILE=${STAGING_DIR_HOST}${monitor_datadir}/monitor.yaml, \
    --disable-monitor, \
    phosphor-fan-monitor-config \
    , \
"

TMPL_MONITOR = "phosphor-fan-monitor@.service"
INSTFMT_MONITOR = "phosphor-fan-monitor@{0}.service"
FMT_MONITOR_FANSREADY = "../${TMPL_MONITOR}:${FAN_CONTROL_TGT}.requires/${INSTFMT_MONITOR}"
FMT_MONITOR_PWRON = "../${TMPL_MONITOR}:${POWERON_TGT}.requires/${INSTFMT_MONITOR}"
FMT_MONITOR_MUSR = "../${TMPL_MONITOR}:${MULTI_USR_TGT}.wants/${INSTFMT_MONITOR}"

TMPL_MONITOR_INIT = "phosphor-fan-monitor-init@.service"
INSTFMT_MONITOR_INIT = "phosphor-fan-monitor-init@{0}.service"
FMT_MONITOR_INIT = "../${TMPL_MONITOR_INIT}:${POWERON_TGT}.wants/${INSTFMT_MONITOR_INIT}"

FILES_${PN}-monitor = "${bindir}/phosphor-fan-monitor"
SYSTEMD_SERVICE_${PN}-monitor += "${TMPL_MONITOR}"
SYSTEMD_SERVICE_${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', '', '${TMPL_MONITOR_INIT}', d)}"

# JSON: power on and multi-user links.  YAML: fans-ready and fan monitor init links
SYSTEMD_LINK_${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
                                compose_list(d, 'FMT_MONITOR_PWRON', 'OBMC_CHASSIS_INSTANCES'), \
                                compose_list(d, 'FMT_MONITOR_FANSREADY', 'OBMC_CHASSIS_INSTANCES'), d)}"

SYSTEMD_LINK_${PN}-monitor += "${@bb.utils.contains('PACKAGECONFIG', 'json', \
                                compose_list(d, 'FMT_MONITOR_MUSR', 'OBMC_CHASSIS_INSTANCES'), \
                                compose_list(d, 'FMT_MONITOR_INIT', 'OBMC_CHASSIS_INSTANCES'), d)}"

# --------------------------------------
# phosphor-cooling-type specific configuration
PACKAGECONFIG[cooling-type] = "--enable-cooling-type,--disable-cooling-type,,"

# --------------------------------------
# ${PN}-sensor-monitor specific configuration
PACKAGECONFIG[sensor-monitor] = "--enable-sensor-monitor, --disable-sensor-monitor"

FILES_sensor-monitor += " ${bindir}/sensor-monitor"
SYSTEMD_SERVICE_sensor-monitor += "sensor-monitor.service"
SYSTEMD_LINK_sensor-monitor += "../sensor-monitor.service:${MULTI_USR_TGT}.wants/sensor-monitor.service"
