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
    ${PN}-chassis-check-power-status \
    ${PN}-secure-check \
    ${PN}-chassis-poweron-log \
"
PACKAGE_BEFORE_PN += "${STATE_MGR_PACKAGES}"
ALLOW_EMPTY:${PN} = "1"

DBUS_PACKAGES = "${STATE_MGR_PACKAGES}"

SYSTEMD_PACKAGES = "${PN}-discover \
                    ${PN}-reset-sensor-states \
                    ${PN}-systemd-target-monitor \
"

# Set the common defaults
PACKAGECONFIG ??= " \
    only-run-apr-on-power-loss \
    only-allow-boot-when-bmc-ready \
    run-apr-on-software-reset \
    install-utils \
    "

# Disable warm reboots of host
PACKAGECONFIG[no-warm-reboot] = "-Dwarm-reboot=disabled,-Dwarm-reboot=enabled"

# Disable forced warm reboots of host
PACKAGECONFIG[no-force-warm-reboot] = "-Dforce-warm-reboot=disabled,-Dforce-warm-reboot=enabled"

# Only run auto power restore logic if system had ac loss
PACKAGECONFIG[only-run-apr-on-power-loss] = "-Donly-run-apr-on-power-loss=true,-Donly-run-apr-on-power-loss=false"

# Only allow boot operations when BMC is in Ready state
PACKAGECONFIG[only-allow-boot-when-bmc-ready] = "-Donly-allow-boot-when-bmc-ready=true,-Donly-allow-boot-when-bmc-ready=false"

# Allow run APR when BMC has been rebooted due to pinhole action
PACKAGECONFIG[run-apr-on-pinhole-reset] = "-Drun-apr-on-pinhole-reset=true,-Drun-apr-on-pinhole-reset=false"

# Allow run APR when BMC has been rebooted due to watchdog
PACKAGECONFIG[run-apr-on-watchdog-reset] = "-Drun-apr-on-watchdog-reset=true,-Drun-apr-on-watchdog-reset=false"

# Allow run APR when BMC has been rebooted due to software request
PACKAGECONFIG[run-apr-on-software-reset] = "-Drun-apr-on-software-reset=true,-Drun-apr-on-software-reset=false"

# Enable host state GPIO
PACKAGECONFIG[host-gpio] = "-Dhost-gpios=enabled,-Dhost-gpios=disabled,gpioplus"

# Check firmware updating before do BMC/Chassis/Host transition
PACKAGECONFIG[check-fwupdate-before-do-transition] = "-Dcheck-fwupdate-before-do-transition=enabled,-Dcheck-fwupdate-before-do-transition=disabled"

PACKAGECONFIG[install-utils] = "-Dinstall-utils=enabled, -Dinstall-utils=disabled"

PACKAGECONFIG[auto-reboot-on-bmc-quiesce] = "-Dauto-reboot-on-bmc-quiesce=enabled,-Dauto-reboot-on-bmc-quiesce=disabled"

# The host-check function will check if the host is running
# after a BMC reset.
# The reset-sensor-states function will reset the host
# sensors on a BMC reset or system power loss.
# Neither is required for host state function but are
# recommended to deal properly with these reset scenarios.
RRECOMMENDS:${PN}-host = "${PN}-host-check ${PN}-reset-sensor-states"

# The obmc-targets are the base targets required to boot a computer system
RRECOMMENDS:${PN}-host += "${PN}-obmc-targets"

# Make it the default to create an info log when the chassis transitions
# from off to on
RRECOMMENDS:${PN}-chassis:append = " ${PN}-chassis-poweron-log"

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
DEPENDS += "libgpiod"

RDEPENDS:${PN}-bmc += "bash"
RDEPENDS:${PN}-host += "bash"

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN}-host = "${bindir}/phosphor-host-state-manager"
FILES:${PN}-host += "${bindir}/phosphor-host-condition-gpio"
FILES:${PN}-host += "${libexecdir}/phosphor-state-manager/host-reboot"
DBUS_SERVICE:${PN}-host += "xyz.openbmc_project.State.Host@.service"
DBUS_SERVICE:${PN}-host += "phosphor-reboot-host@.service"
SYSTEMD_SERVICE:${PN}-host += "phosphor-reset-host-reboot-attempts@.service"
SYSTEMD_SERVICE:${PN}-host += "phosphor-clear-one-time@.service"
SYSTEMD_SERVICE:${PN}-host += "phosphor-set-host-transition-to-running@.service"
SYSTEMD_SERVICE:${PN}-host += "phosphor-set-host-transition-to-off@.service"
SYSTEMD_SERVICE:${PN}-host += "${@bb.utils.contains('PACKAGECONFIG', 'host-gpio', 'phosphor-host-condition-gpio@.service', '', d)}"

FILES:${PN}-chassis = "${bindir}/phosphor-chassis-state-manager"
DBUS_SERVICE:${PN}-chassis += "xyz.openbmc_project.State.Chassis@.service"
SYSTEMD_SERVICE:${PN}-chassis += "obmc-power-start@.service"
SYSTEMD_SERVICE:${PN}-chassis += "obmc-power-stop@.service"
SYSTEMD_SERVICE:${PN}-chassis += "obmc-powered-off@.service"
SYSTEMD_SERVICE:${PN}-chassis += "phosphor-reset-chassis-on@.service"
SYSTEMD_SERVICE:${PN}-chassis += "phosphor-reset-chassis-running@.service"
SYSTEMD_SERVICE:${PN}-chassis += "phosphor-set-chassis-transition-to-on@.service"
SYSTEMD_SERVICE:${PN}-chassis += "phosphor-set-chassis-transition-to-off@.service"

SYSTEMD_SERVICE:${PN}-chassis-poweron-log += "phosphor-create-chassis-poweron-log@.service"

FILES:${PN}-bmc = "${bindir}/phosphor-bmc-state-manager"
FILES:${PN}-bmc += "${sysconfdir}/phosphor-systemd-target-monitor/phosphor-service-monitor-default.json"
FILES:${PN}-bmc += "${bindir}/obmcutil"
DBUS_SERVICE:${PN}-bmc += "xyz.openbmc_project.State.BMC.service"
DBUS_SERVICE:${PN}-bmc += "obmc-bmc-service-quiesce@.target"
SYSTEMD_SERVICE:${PN}-bmc += "phosphor-bmc-quiesce-reboot.service"
FILES:${PN}-bmc += "${@bb.utils.contains('PACKAGECONFIG', 'auto-reboot-on-bmc-quiesce', '${systemd_system_unitdir}/obmc-bmc-service-quiesce@0.target.wants', '', d)}"
FILES:${PN}-bmc += "${@bb.utils.contains('PACKAGECONFIG', 'auto-reboot-on-bmc-quiesce', '${systemd_system_unitdir}/obmc-bmc-service-quiesce@0.target.wants/phosphor-bmc-quiesce-reboot.service', '', d)}"

FILES:${PN}-secure-check = "${bindir}/phosphor-secure-boot-check"
SYSTEMD_SERVICE:${PN}-secure-check += "phosphor-bmc-security-check.service"

FILES:${PN}-hypervisor = "${bindir}/phosphor-hypervisor-state-manager"
DBUS_SERVICE:${PN}-hypervisor += "xyz.openbmc_project.State.Hypervisor.service"

FILES:${PN}-discover = "${bindir}/phosphor-discover-system-state"
SYSTEMD_SERVICE:${PN}-discover += "phosphor-discover-system-state@.service"

FILES:${PN}-host-check = "${bindir}/phosphor-host-check"
SYSTEMD_SERVICE:${PN}-host-check += "phosphor-reset-host-running@.service"
FILES:${PN}-host-check = "${bindir}/phosphor-host-reset-recovery"
SYSTEMD_SERVICE:${PN}-host-check += "phosphor-reset-host-recovery@.service"


SYSTEMD_SERVICE:${PN}-reset-sensor-states += "phosphor-reset-sensor-states@.service"

FILES:${PN}-systemd-target-monitor = " \
    ${bindir}/phosphor-systemd-target-monitor \
    ${sysconfdir}/phosphor-systemd-target-monitor/phosphor-target-monitor-default.json \
    "
SYSTEMD_SERVICE:${PN}-systemd-target-monitor += "phosphor-systemd-target-monitor.service"

FILES:${PN}-scheduled-host-transition = "${bindir}/phosphor-scheduled-host-transition"
DBUS_SERVICE:${PN}-scheduled-host-transition += "xyz.openbmc_project.State.ScheduledHostTransition@.service"

FILES:${PN}-chassis-check-power-status = "${bindir}/phosphor-chassis-check-power-status"
SYSTEMD_SERVICE:${PN}-chassis-check-power-status += "phosphor-chassis-check-power-status@.service"

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
# - cycle: Services to run to cycle power to the chassis
# - powered-off: Services to run once chassis power is off
# - reset: Services to check chassis power state and update chassis "on" target
# - hard-off: Services to force an immediate power off of the chassis
# - blackout: Target to enter when chassis experiences blackout
CHASSIS_ACTION_TARGETS = "poweron poweroff powercycle powered-off powerreset hard-poweroff blackout"

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
# - graceful-quiesce:  Target to enter on host boot failure (allow host graceful shutdown)
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
HOST_ACTION_TARGETS = "start startmin stop quiesce graceful-quiesce reset shutdown crash timeout "
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

SYSTEMD_SERVICE:${PN}-obmc-targets += " \
        obmc-fans-ready.target \
        obmc-fan-control.target \
        obmc-fan-control-ready@.target \
        obmc-fan-watchdog-takeover.target \
        "

SYSTEMD_SERVICE:${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS')}"
SYSTEMD_SERVICE:${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_ACTION_FMT', 'CHASSIS_ACTION_TARGETS')}"
SYSTEMD_SERVICE:${PN}-obmc-targets += "${@compose_list(d, 'HOST_SYNCH_FMT', 'HOST_SYNCH_TARGETS')}"
SYSTEMD_SERVICE:${PN}-obmc-targets += "${@compose_list(d, 'HOST_ACTION_FMT', 'HOST_ACTION_TARGETS')}"

SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_LINK_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'CHASSIS_LINK_ACTION_FMT', 'CHASSIS_ACTION_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'HOST_LINK_SYNCH_FMT', 'HOST_SYNCH_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'HOST_LINK_ACTION_FMT', 'HOST_ACTION_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'FAN_LINK_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list(d, 'QUIESCE_FMT', 'HOST_ERROR_TARGETS', 'OBMC_HOST_INSTANCES')}"

# Create target relationships

# Starting the host requires chassis power on
START_TMPL_CTRL = "obmc-chassis-poweron@.target"
START_TGTFMT_CTRL = "obmc-host-startmin@{1}.target"
START_INSTFMT_CTRL = "obmc-chassis-poweron@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list_zip(d, 'START_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Chassis off requires host off
STOP_TMPL_CTRL = "obmc-host-stop@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT_CTRL = "obmc-host-stop@{1}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list_zip(d, 'STOP_FMT_CTRL', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Hard power off requires chassis off
HARD_OFF_TMPL_CTRL = "obmc-chassis-poweroff@.target"
HARD_OFF_TGTFMT_CTRL = "obmc-chassis-hard-poweroff@{0}.target"
HARD_OFF_INSTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
HARD_OFF_FMT_CTRL = "../${HARD_OFF_TMPL_CTRL}:${HARD_OFF_TGTFMT_CTRL}.requires/${HARD_OFF_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list_zip(d, 'HARD_OFF_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Force the standby target to run the chassis reset check target
RESET_TMPL_CTRL = "obmc-chassis-powerreset@.target"
SYSD_TGT = "multi-user.target"
RESET_INSTFMT_CTRL = "obmc-chassis-powerreset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN}-obmc-targets += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK[vardeps] += "OBMC_CHASSIS_INSTANCES OBMC_HOST_INSTANCES"

SRC_URI = "git://github.com/openbmc/phosphor-state-manager;branch=master;protocol=https"
SRCREV = "b26e5096b595a35015387f470330991fb2ebf1dc"

S = "${WORKDIR}/git"
