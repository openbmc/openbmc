SUMMARY = "Phosphor State Management"
DESCRIPTION = "Phosphor State Manager provides a set of system state \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

include phosphor-state-manager-systemd-links.inc

STATE_MGR_PACKAGES = " \
    ${PN}-host \
    ${PN}-chassis \
    ${PN}-bmc \
    ${PN}-hypervisor \
    ${PN}-discover \
    ${PN}-host-check \
    ${PN}-reset-sensor-states \
    ${PN}-systemd-target-monitor \
    ${PN}-obmc-targets \
    ${PN}-scheduled-host-transition \
"
PACKAGE_BEFORE_PN += "${STATE_MGR_PACKAGES}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${STATE_MGR_PACKAGES}"

SYSTEMD_PACKAGES = "${PN}-discover \
                    ${PN}-reset-sensor-states \
                    ${PN}-systemd-target-monitor \
"

# The host-check function will check if the host is running
# after a BMC reset.
# The reset-sensor-states function will reset the host
# sensors on a BMC reset or system power loss.
# Neither is required for host state function but are
# recommended to deal properly with these reset scenarios.
RRECOMMENDS_${PN}-host = "${PN}-host-check ${PN}-reset-sensor-states"

# The obmc-targets are the base targets required to boot a computer system
RRECOMMENDS_${PN}-host += "${PN}-obmc-targets"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd

DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "libcereal"
DEPENDS += "nlohmann-json"
DEPENDS += "cli11"

FILES_${PN}-host = "${bindir}/phosphor-host-state-manager"
DBUS_SERVICE_${PN}-host += "xyz.openbmc_project.State.Host.service"
DBUS_SERVICE_${PN}-host += "phosphor-reboot-host@.service"
SYSTEMD_SERVICE_${PN}-host += "phosphor-reset-host-reboot-attempts@.service"
SYSTEMD_SERVICE_${PN}-host += "phosphor-clear-one-time@.service"
SYSTEMD_SERVICE_${PN}-host += "phosphor-set-host-transition-to-running@.service"
SYSTEMD_SERVICE_${PN}-host += "phosphor-set-host-transition-to-off@.service"

FILES_${PN}-chassis = "${bindir}/phosphor-chassis-state-manager"
DBUS_SERVICE_${PN}-chassis += "xyz.openbmc_project.State.Chassis.service"

FILES_${PN}-chassis += "${bindir}/obmcutil"

FILES_${PN}-bmc = "${bindir}/phosphor-bmc-state-manager"
DBUS_SERVICE_${PN}-bmc += "xyz.openbmc_project.State.BMC.service"

FILES_${PN}-hypervisor = "${bindir}/phosphor-hypervisor-state-manager"
DBUS_SERVICE_${PN}-hypervisor += "xyz.openbmc_project.State.Hypervisor.service"

FILES_${PN}-discover = "${bindir}/phosphor-discover-system-state"
SYSTEMD_SERVICE_${PN}-discover += "phosphor-discover-system-state@.service"

FILES_${PN}-host-check = "${bindir}/phosphor-host-check"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-check@.service"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-running@.service"

SYSTEMD_SERVICE_${PN}-reset-sensor-states += "phosphor-reset-sensor-states@.service"

FILES_${PN}-systemd-target-monitor = " \
    ${bindir}/phosphor-systemd-target-monitor \
    ${sysconfdir}/phosphor-systemd-target-monitor/phosphor-target-monitor-default.json \
    "
SYSTEMD_SERVICE_${PN}-systemd-target-monitor += "phosphor-systemd-target-monitor.service"

FILES_${PN}-scheduled-host-transition = "${bindir}/phosphor-scheduled-host-transition"
DBUS_SERVICE_${PN}-scheduled-host-transition += "xyz.openbmc_project.State.ScheduledHostTransition.service"

# Chassis power synchronization targets
# - start-pre:         Services to run before we start power on process
# - start:             Services to run to do the chassis power on
# - on:                Services to run once power is on
# - stop-pre,stop,off: Same as above but applied to powering off
# - reset-on:          Services to check if chassis power is on after bmc reset
CHASSIS_SYNCH_TARGETS = "start-pre start on stop-pre stop off reset-on"

# Chassis action power targets
# - on:  Services to run to power on the chassis
# - off: Services to run to power off the chassis
# - powered-off: Services to run once chassis power is off
# - reset: Services to check chassis power state and update chassis "on" target
# - hard-off: Services to force an immediate power off of the chassis
CHASSIS_ACTION_TARGETS = "poweron poweroff powered-off powerreset hard-poweroff"

# Track all host synchronization point targets
# - start-pre:                 Services to run before we start host boot
# - starting:                  Services to run to do the host boot
# - started:                   Services to run once the host is booted
# - stop-pre,stopping,stopped: Same as above but applied to shutting down the host
# - reset-running:             Services to check if host is running after bmc reset
HOST_SYNCH_TARGETS = "start-pre starting started stop-pre stopping stopped reset-running"

# Track all host action targets
# - start:    Will run startmin target, this target used for any additional
#             services that user needs for an initial power on of host.
#             For example, resetting the host reboot count could be put in
#             this target so on any fresh power on, this count is reset.
# - startmin: Minimum services required to start the host. This target will
#             be called by reboot and start target.
# - stop:     Services to run to shutdown the host
# - quiesce:  Target to enter on host boot failure
# - shutdown: Tell host to shutdown, then stop system
# - reset:   Services to check if host is running and update host "start" target
# - crash:   Target to run when host crashes. it is very much similar to
#            quiesce target but the only delta is that this target contains
#            multiple services and one of them is the quiesce target.
# - timeout: Target to run when host watchdog times out
# - reboot:  Reboot the host with a chassis power cycle included
# - warm-reboot: Reboot the host without a chassis power cycle.
# - force-warm-reboot: Reboot the host without a chassis power cycle and without
#                      notifying the host.
# - diagnostic-mode: This will be entered when the host is collecting diagnostic
#                    data for itself.
HOST_ACTION_TARGETS = "start startmin stop quiesce reset shutdown crash timeout "
HOST_ACTION_TARGETS += "reboot warm-reboot force-warm-reboot diagnostic-mode"

CHASSIS_SYNCH_FMT = "obmc-power-{0}@.target"
CHASSIS_ACTION_FMT = "obmc-chassis-{0}@.target"
HOST_SYNCH_FMT = "obmc-host-{0}@.target"
HOST_ACTION_FMT = "obmc-host-{0}@.target"

CHASSIS_LINK_SYNCH_FMT = "${CHASSIS_SYNCH_FMT}:obmc-power-{0}@{1}.target"
CHASSIS_LINK_ACTION_FMT = "${CHASSIS_ACTION_FMT}:obmc-chassis-{0}@{1}.target"
HOST_LINK_SYNCH_FMT = "${HOST_SYNCH_FMT}:obmc-host-{0}@{1}.target"
HOST_LINK_ACTION_FMT = "${HOST_ACTION_FMT}:obmc-host-{0}@{1}.target"
FAN_LINK_FMT = "obmc-fan-control-ready@.target:obmc-fan-control-ready@{0}.target"

# Targets to be executed on checkstop and watchdog timeout
HOST_ERROR_TARGETS = "timeout"

QUIESCE_TMPL = "obmc-host-quiesce@.target"
CRASH_TIMEOUT_TGTFMT = "obmc-host-{0}@{1}.target"
QUIESCE_INSTFMT = "obmc-host-quiesce@{1}.target"
QUIESCE_FMT = "../${QUIESCE_TMPL}:${CRASH_TIMEOUT_TGTFMT}.wants/${QUIESCE_INSTFMT}"

SYSTEMD_SERVICE_${PN}-obmc-targets += " \
        obmc-mapper.target \
        obmc-fans-ready.target \
        obmc-fan-control.target \
        obmc-fan-control-ready@.target \
        obmc-fan-watchdog-takeover.target \
        "

SYSTEMD_SERVICE_${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS')}"
SYSTEMD_SERVICE_${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_ACTION_FMT', 'CHASSIS_ACTION_TARGETS')}"
SYSTEMD_SERVICE_${PN}-obmc-targets += "${@compose_list(d, 'HOST_SYNCH_FMT', 'HOST_SYNCH_TARGETS')}"
SYSTEMD_SERVICE_${PN}-obmc-targets += "${@compose_list(d, 'HOST_ACTION_FMT', 'HOST_ACTION_TARGETS')}"

SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_LINK_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_LINK_ACTION_FMT', 'CHASSIS_ACTION_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'HOST_LINK_SYNCH_FMT', 'HOST_SYNCH_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'HOST_LINK_ACTION_FMT', 'HOST_ACTION_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'FAN_LINK_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN}-obmc-targets += "${@compose_list(d, 'QUIESCE_FMT', 'HOST_ERROR_TARGETS', 'OBMC_HOST_INSTANCES')}"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "35ca2e34cd04b7288a5b659e4e7dcd4590056b7d"

S = "${WORKDIR}/git"
