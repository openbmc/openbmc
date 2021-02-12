FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# Package configuration
FAN_PACKAGES_append_ibm-ac-server = " \
        phosphor-cooling-type \
        "

FAN_PACKAGES_append_mihawk = " \
        phosphor-cooling-type \
        "

PACKAGECONFIG_append_ibm-ac-server = " cooling-type"
PACKAGECONFIG_append_mihawk = " cooling-type"

TMPL_COOLING = "phosphor-cooling-type@.service"
INSTFMT_COOLING = "phosphor-cooling-type@{0}.service"
MULTI_USR_TGT = "multi-user.target"
FMT_COOLING = "../${TMPL_COOLING}:${MULTI_USR_TGT}.requires/${INSTFMT_COOLING}"

FILES_phosphor-cooling-type_append_ibm-ac-server = " ${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE_phosphor-cooling-type_append_ibm-ac-server = " ${TMPL_COOLING}"
SYSTEMD_LINK_phosphor-cooling-type_append_ibm-ac-server = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"
FILES_phosphor-cooling-type_append_mihawk = " ${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE_phosphor-cooling-type_append_mihawk = " ${TMPL_COOLING}"
SYSTEMD_LINK_phosphor-cooling-type_append_mihawk = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type_append_ibm-ac-server = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type_append_mihawk = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

#These services are protected by the watchdog
SYSTEMD_OVERRIDE_phosphor-fan-control_witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control_witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control_rainier += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control_rainier += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_rainier += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_rainier += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"

#These services need to be stopped when watchdog expires
SYSTEMD_OVERRIDE_phosphor-fan-control_witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control_rainier += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor_rainier += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"

# Witherspoon fan control service linking
# Link fan control init service
SYSTEMD_LINK_${PN}-control_witherspoon += "${@compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to be started at standby
FMT_CONTROL_STDBY_witherspoon = "../${TMPL_CONTROL}:${MULTI_USR_TGT}.wants/${INSTFMT_CONTROL}"
SYSTEMD_LINK_${PN}-control_witherspoon += "${@compose_list(d, 'FMT_CONTROL_STDBY', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to also start at poweron
FMT_CONTROL_PWRON_witherspoon = "../${TMPL_CONTROL}:${POWERON_TGT}.requires/${INSTFMT_CONTROL}"
SYSTEMD_LINK_${PN}-control_witherspoon += "${@compose_list(d, 'FMT_CONTROL_PWRON', 'OBMC_CHASSIS_INSTANCES')}"

# Enable the use of JSON on the fan applications that support it
PACKAGECONFIG_append_witherspoon = " json"
EXTRA_OECONF_append_witherspoon = " --disable-json-control"
RDEPENDS_${PN}-presence-tach_append_witherspoon = " phosphor-fan-presence-config"
RDEPENDS_${PN}-monitor_append_witherspoon = " phosphor-fan-monitor-config"

PACKAGECONFIG_append_rainier = " json sensor-monitor"
FAN_PACKAGES_append_rainier = " sensor-monitor"
RDEPENDS_${PN}-presence-tach_append_rainier = " phosphor-fan-presence-config"
RDEPENDS_${PN}-monitor_append_rainier = " phosphor-fan-monitor-config"

# Install fan control JSON config files
SRC_URI_append_rainier = " \
    file://manager.json \
    file://rainier/fans.json \
    file://rainier-2u/zones.json \
    file://rainier-4u/zones.json \
    file://everest/fans.json \
    file://everest/zones.json"
do_install_append_rainier() {
    # Install fan control manager config file
    install -d ${D}/${datadir}/phosphor-fan-presence/control/
    install -m 0644 ${WORKDIR}/manager.json ${D}/${datadir}/phosphor-fan-presence/control/

    # Install Rainier-2U/4U fan config files
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier
    install -m 0644 ${WORKDIR}/rainier/fans.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier/

    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/
    install -m 0644 ${WORKDIR}/rainier-2u/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/
    install -m 0644 ${WORKDIR}/rainier-4u/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/

    # Install Everest fan config files
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest
    install -m 0644 ${WORKDIR}/everest/fans.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest/
    install -m 0644 ${WORKDIR}/everest/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest/
}
FILES_${PN}-control_append_rainier = " \
    ${datadir}/phosphor-fan-presence/control/manager.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier/fans.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/zones.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/zones.json \
    ${datadir}/phosphor-fan-presence/control/ibm,everest/fans.json \
    ${datadir}/phosphor-fan-presence/control/ibm,everest/zones.json"

# Set the appropriate i2c address used within the overridden phosphor-fan-control@.service
# file that's used for witherspoon type(including witherspoon-tacoma) machines
SYSTEMD_SUBSTITUTIONS_witherspoon = "ADDR:100:phosphor-fan-control@.service"
SYSTEMD_SUBSTITUTIONS_witherspoon-tacoma = "ADDR:200:phosphor-fan-control@.service"
