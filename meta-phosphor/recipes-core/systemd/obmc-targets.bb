SUMMARY = "Phosphor Systemd targets"
DESCRIPTION = "Provides well known Systemd synchronization points for OpenBMC."
HOMEPAGE = "http://github.com/openbmc"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd
inherit allarch

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
# - reboot:  Reboot the host
HOST_ACTION_TARGETS = "start startmin stop quiesce reset shutdown crash timeout reboot"

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
HOST_ERROR_TARGETS = "crash timeout"

QUIESCE_TMPL = "obmc-host-quiesce@.target"
CRASH_TIMEOUT_TGTFMT = "obmc-host-{0}@{1}.target"
QUIESCE_INSTFMT = "obmc-host-quiesce@{1}.target"
QUIESCE_FMT = "../${QUIESCE_TMPL}:${CRASH_TIMEOUT_TGTFMT}.wants/${QUIESCE_INSTFMT}"

SYSTEMD_SERVICE_${PN} += " \
        obmc-mapper.target \
        obmc-webserver-pre.target \
        obmc-fans-ready.target \
        obmc-fan-control.target \
        obmc-fan-control-ready@.target \
        obmc-fan-watchdog-takeover.target \
        obmc-standby.target \
        "

SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHASSIS_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHASSIS_ACTION_FMT', 'CHASSIS_ACTION_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'HOST_SYNCH_FMT', 'HOST_SYNCH_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'HOST_ACTION_FMT', 'HOST_ACTION_TARGETS')}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHASSIS_LINK_SYNCH_FMT', 'CHASSIS_SYNCH_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHASSIS_LINK_ACTION_FMT', 'CHASSIS_ACTION_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'HOST_LINK_SYNCH_FMT', 'HOST_SYNCH_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'HOST_LINK_ACTION_FMT', 'HOST_ACTION_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FAN_LINK_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'QUIESCE_FMT', 'HOST_ERROR_TARGETS', 'OBMC_HOST_INSTANCES')}"
