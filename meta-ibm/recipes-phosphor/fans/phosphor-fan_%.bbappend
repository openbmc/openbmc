FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Package configuration
FAN_PACKAGES:append:ibm-ac-server = " \
        phosphor-cooling-type \
        "

PACKAGECONFIG:append:ibm-ac-server = " cooling-type"

TMPL_COOLING = "phosphor-cooling-type@.service"
INSTFMT_COOLING = "phosphor-cooling-type@{0}.service"
MULTI_USR_TGT = "multi-user.target"
FMT_COOLING = "../${TMPL_COOLING}:${MULTI_USR_TGT}.requires/${INSTFMT_COOLING}"

FILES:phosphor-cooling-type:append:ibm-ac-server = " ${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE:phosphor-cooling-type:append:ibm-ac-server = " ${TMPL_COOLING}"
SYSTEMD_LINK:phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE:phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

#These services are protected by the watchdog
SYSTEMD_OVERRIDE:phosphor-fan-control:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-control:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:witherspoon += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-control:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-control:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:p10bmc += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"

#These services need to be stopped when watchdog expires
SYSTEMD_OVERRIDE:phosphor-fan-control:witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:witherspoon += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE:phosphor-fan-control:p10bmc += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:p10bmc += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"

# Witherspoon fan control service linking
# Link fan control init service
SYSTEMD_SERVICE:${PN}-control:witherspoon += "${TMPL_CONTROL} ${TMPL_CONTROL_INIT}"
SYSTEMD_LINK:${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_INIT', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to be started at standby
FMT_CONTROL_STDBY:witherspoon = "../${TMPL_CONTROL}:${MULTI_USR_TGT}.wants/${INSTFMT_CONTROL}"
SYSTEMD_LINK:${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_STDBY', 'OBMC_CHASSIS_INSTANCES')}"
# Link fan control service to also start at poweron
FMT_CONTROL_PWRON:witherspoon = "../${TMPL_CONTROL}:${POWERON_TGT}.requires/${INSTFMT_CONTROL}"
SYSTEMD_LINK:${PN}-control:witherspoon += "${@compose_list(d, 'FMT_CONTROL_PWRON', 'OBMC_CHASSIS_INSTANCES')}"

# Enable the use of JSON on the fan applications that support it
PACKAGECONFIG:append:witherspoon = " json"
EXTRA_OEMESON:append:witherspoon = " -Djson-control=disabled"

PACKAGECONFIG:append:p10bmc = " json sensor-monitor"
PACKAGECONFIG:append:sbp1 = " json sensor-monitor"
EXTRA_OEMESON:append:sbp1 = " -Duse-host-power-state=enabled"

# Set the appropriate i2c address used within the overridden phosphor-fan-control@.service
# file that's used for witherspoon type(including witherspoon-tacoma) machines
SYSTEMD_SUBSTITUTIONS:witherspoon = "ADDR:100:phosphor-fan-control@.service"
SYSTEMD_SUBSTITUTIONS:witherspoon-tacoma = "ADDR:200:phosphor-fan-control@.service"

# Set the PKG_DEFAULT_MACHINE name to "witherspoon" for tacoma so witherspoon's
# JSON config files are installed on tacoma machines (since they use the same ones)
PKG_DEFAULT_MACHINE:witherspoon-tacoma = "witherspoon"
