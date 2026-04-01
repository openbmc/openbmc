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
SYSTEMD_AUTO_ENABLE:phosphor-cooling-type = "disable"
SYSTEMD_LINK:phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE:phosphor-cooling-type:append:ibm-ac-server = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

#These services are protected by the watchdog
SYSTEMD_OVERRIDE:phosphor-fan-control:ibm-enterprise += "fan-watchdog-monitor.conf:phosphor-fan-control-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-control:ibm-enterprise += "fan-watchdog-monitor.conf:phosphor-fan-control@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:ibm-enterprise += "fan-watchdog-monitor.conf:phosphor-fan-monitor-init@0.service.d/fan-watchdog-monitor.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:ibm-enterprise += "fan-watchdog-monitor.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-monitor.conf"

#These services need to be stopped when watchdog expires
SYSTEMD_OVERRIDE:phosphor-fan-control:ibm-enterprise += "fan-watchdog-conflicts.conf:phosphor-fan-control@0.service.d/fan-watchdog-conflicts.conf"
SYSTEMD_OVERRIDE:phosphor-fan-monitor:ibm-enterprise += "fan-watchdog-conflicts.conf:phosphor-fan-monitor@0.service.d/fan-watchdog-conflicts.conf"

# Enable the use of JSON on the fan applications that support it
PACKAGECONFIG:append:ibm-enterprise = " json sensor-monitor"
PACKAGECONFIG:append:sbp1 = " json sensor-monitor"
EXTRA_OEMESON:append:sbp1 = " -Duse-host-power-state=enabled"

