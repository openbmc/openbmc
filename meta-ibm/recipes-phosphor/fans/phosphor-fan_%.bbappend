FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Package configuration
FAN_PACKAGES:append:ibm-ac-server = " \
        phosphor-cooling-type \
        "

FAN_PACKAGES:append:mihawk = " \
        phosphor-cooling-type \
        "

PACKAGECONFIG:append:ibm-ac-server = " cooling-type"
PACKAGECONFIG:append:mihawk = " cooling-type"

TMPL_COOLING = "phosphor-cooling-type@.service"
INSTFMT_COOLING = "phosphor-cooling-type@{0}.service"
MULTI_USR_TGT = "multi-user.target"
FMT_COOLING = "../${TMPL_COOLING}:${MULTI_USR_TGT}.requires/${INSTFMT_COOLING}"

FILES:phosphor-cooling-type:append:ibm-ac-server = " ${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE:phosphor-cooling-type:append:ibm-ac-server = " ${TMPL_COOLING}"
SYSTEMD_LINK_phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"
FILES:phosphor-cooling-type:append:mihawk = " ${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE:phosphor-cooling-type:append:mihawk = " ${TMPL_COOLING}"
SYSTEMD_LINK_phosphor-cooling-type:append:mihawk = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type:append:mihawk = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

#These services are protected by the watchdog
SYSTEMD_OVERRIDE_phosphor-fan-control:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"

#These services need to be stopped when watchdog expires
SYSTEMD_OVERRIDE_phosphor-fan-control:witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-control:p10bmc += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE_phosphor-fan-monitor:p10bmc += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"

# Witherspoon fan control service linking
# Link fan control init service
SYSTEMD_SERVICE:${PN}-control:witherspoon += "${TMPL_CONTROL} ${TMPL_CONTROL_INIT}"
SYSTEMD_LINK_${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to be started at standby
FMT_CONTROL_STDBY:witherspoon = "../${TMPL_CONTROL}:${MULTI_USR_TGT}.wants/${INSTFMT_CONTROL}"
SYSTEMD_LINK_${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_STDBY', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to also start at poweron
FMT_CONTROL_PWRON:witherspoon = "../${TMPL_CONTROL}:${POWERON_TGT}.requires/${INSTFMT_CONTROL}"
SYSTEMD_LINK_${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_PWRON', 'OBMC_CHASSIS_INSTANCES')}"

# Enable the use of JSON on the fan applications that support it
PACKAGECONFIG:append:witherspoon = " json"
EXTRA_OECONF:append:witherspoon = " --disable-json-control"
RDEPENDS:${PN}-presence-tach:append:witherspoon = " phosphor-fan-presence-config"
RDEPENDS:${PN}-monitor:append:witherspoon = " phosphor-fan-monitor-config"

PACKAGECONFIG:append:p10bmc = " json sensor-monitor"
FAN_PACKAGES:append:p10bmc = " sensor-monitor"
RDEPENDS:${PN}-presence-tach:append:p10bmc = " phosphor-fan-presence-config"
RDEPENDS:${PN}-monitor:append:p10bmc = " phosphor-fan-monitor-config"

# Install fan control JSON config files
SRC_URI:append:p10bmc = " \
    file://manager.json \
    file://rainier/fans.json \
    file://rainier-1s4u/fans.json \
    file://rainier-2u/zones.json \
    file://rainier-4u/zones.json \
    file://rainier-1s4u/zones.json \
    file://everest/fans.json \
    file://everest/zones.json"
do_install:append:p10bmc() {
    # Install fan control manager config file
    install -d ${D}/${datadir}/phosphor-fan-presence/control/
    install -m 0644 ${WORKDIR}/manager.json ${D}/${datadir}/phosphor-fan-presence/control/

    # Install Rainier-2U/4U fan config files
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u
    install -m 0644 ${WORKDIR}/rainier/fans.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier/
    install -m 0644 ${WORKDIR}/rainier-1s4u/fans.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u/

    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u/
    install -m 0644 ${WORKDIR}/rainier-2u/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/
    install -m 0644 ${WORKDIR}/rainier-4u/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/
    install -m 0644 ${WORKDIR}/rainier-1s4u/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u/

    # Install Everest fan config files
    install -d ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest
    install -m 0644 ${WORKDIR}/everest/fans.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest/
    install -m 0644 ${WORKDIR}/everest/zones.json ${D}/${datadir}/phosphor-fan-presence/control/ibm,everest/
}
FILES:${PN}-control:append:p10bmc = " \
    ${datadir}/phosphor-fan-presence/control/manager.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier/fans.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u/fans.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-2u/zones.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-4u/zones.json \
    ${datadir}/phosphor-fan-presence/control/ibm,rainier-1s4u/zones.json \
    ${datadir}/phosphor-fan-presence/control/ibm,everest/fans.json \
    ${datadir}/phosphor-fan-presence/control/ibm,everest/zones.json"

# Set the appropriate i2c address used within the overridden phosphor-fan-control@.service
# file that's used for witherspoon type(including witherspoon-tacoma) machines
SYSTEMD_SUBSTITUTIONS:witherspoon = "ADDR:100:phosphor-fan-control@.service"
SYSTEMD_SUBSTITUTIONS:witherspoon-tacoma = "ADDR:200:phosphor-fan-control@.service"
