SUMMARY = "Phosphor Fan"
DESCRIPTION = "Phosphor fan provides a set of fan monitoring and \
control applications."
PR = "r1"

require ${PN}.inc

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd
inherit phosphor-fan

S = "${WORKDIR}/git"

# Common build dependencies
DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "sdbusplus-native"
DEPENDS += "phosphor-logging"
DEPENDS += "libevdev"

# Package configuration
FAN_PACKAGES = " \
        ${PN}-presence-tach \
        ${PN}-control \
        ${PN}-monitor \
"

PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PACKAGES}"
PACKAGECONFIG ?= "presence control monitor"
SYSTEMD_PACKAGES = "${FAN_PACKAGES}"
RDEPENDS_${PN}-dev = "${FAN_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${FAN_PACKAGES}"

# --------------------------------------
# ${PN}-presence-tach specific configuration
PACKAGECONFIG[presence] = " \
        --enable-presence \
        PRESENCE_CONFIG=${STAGING_DIR_NATIVE}${presence_datadir}/config.yaml, \
        --disable-presence, \
        virtual/phosphor-fan-presence-config \
        , \
"
RDEPENDS_${PN}-presence-tach += "sdbusplus"

# Needed to install into the obmc-chassis-poweron target
TMPL_TACH = "phosphor-fan-presence-tach@.service"
INSTFMT_TACH = "phosphor-fan-presence-tach@{0}.service"
POWERON_TGT = "obmc-chassis-poweron@{0}.target"
FMT_TACH = "../${TMPL_TACH}:${POWERON_TGT}.requires/${INSTFMT_TACH}"

FILES_${PN}-presence-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-presence-tach += "${TMPL_TACH}"
SYSTEMD_LINK_${PN}-presence-tach += "${@compose_list(d, 'FMT_TACH', 'OBMC_CHASSIS_INSTANCES')}"

# --------------------------------------
# ${PN}-control specific configuration
PACKAGECONFIG[control] = "--enable-control \
     FAN_DEF_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/fans.yaml \
     FAN_ZONE_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/zones.yaml \
     ZONE_EVENTS_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/events.yaml \
     ZONE_CONDITIONS_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/zone_conditions.yaml \
     FAN_ZONE_OUTPUT_DIR=${S}/control, \
    --disable-control, \
    virtual/phosphor-fan-control-fan-config \
    phosphor-fan-control-zone-config-native \
    phosphor-fan-control-events-config-native \
    phosphor-fan-control-zone-conditions-config-native \
    , \
"

RDEPENDS_${PN}-control += "sdbusplus"

FAN_CONTROL_TGT = "obmc-fan-control-ready@{0}.target"

TMPL_CONTROL = "phosphor-fan-control@.service"
INSTFMT_CONTROL = "phosphor-fan-control@{0}.service"
FMT_CONTROL = "../${TMPL_CONTROL}:${FAN_CONTROL_TGT}.requires/${INSTFMT_CONTROL}"

TMPL_CONTROL_INIT = "phosphor-fan-control-init@.service"
INSTFMT_CONTROL_INIT = "phosphor-fan-control-init@{0}.service"
FMT_CONTROL_INIT = "../${TMPL_CONTROL_INIT}:${POWERON_TGT}.wants/${INSTFMT_CONTROL_INIT}"

FILES_${PN}-control = "${sbindir}/phosphor-fan-control"
SYSTEMD_SERVICE_${PN}-control += "${TMPL_CONTROL} ${TMPL_CONTROL_INIT}"
SYSTEMD_LINK_${PN}-control += "${@compose_list(d, 'FMT_CONTROL', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-control += "${@compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_INSTANCES')}"

# --------------------------------------
# ${PN}-monitor specific configuration
PACKAGECONFIG[monitor] = "--enable-monitor \
     FAN_MONITOR_YAML_FILE=${STAGING_DIR_NATIVE}${monitor_datadir}/monitor.yaml \
     FAN_MONITOR_OUTPUT_DIR=${S}/monitor, \
    --disable-monitor, \
    phosphor-fan-monitor-config-native \
    , \
"

RDEPENDS_${PN}-monitor += "sdbusplus"

TMPL_MONITOR = "phosphor-fan-monitor@.service"
INSTFMT_MONITOR = "phosphor-fan-monitor@{0}.service"
FMT_MONITOR = "../${TMPL_MONITOR}:${FAN_CONTROL_TGT}.requires/${INSTFMT_MONITOR}"

TMPL_MONITOR_INIT = "phosphor-fan-monitor-init@.service"
INSTFMT_MONITOR_INIT = "phosphor-fan-monitor-init@{0}.service"
FMT_MONITOR_INIT = "../${TMPL_MONITOR_INIT}:${POWERON_TGT}.wants/${INSTFMT_MONITOR_INIT}"

FILES_${PN}-monitor = "${sbindir}/phosphor-fan-monitor"
SYSTEMD_SERVICE_${PN}-monitor += "${TMPL_MONITOR} ${TMPL_MONITOR_INIT}"
SYSTEMD_LINK_${PN}-monitor += "${@compose_list(d, 'FMT_MONITOR', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-monitor += "${@compose_list(d, 'FMT_MONITOR_INIT', 'OBMC_CHASSIS_INSTANCES')}"

# --------------------------------------
# phosphor-cooling-type specific configuration
PACKAGECONFIG[cooling-type] = "--enable-cooling-type,--disable-cooling-type,,"
