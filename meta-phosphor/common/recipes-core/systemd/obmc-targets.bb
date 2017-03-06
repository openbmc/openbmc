SUMMARY = "Phosphor Systemd targets"
DESCRIPTION = "Provides well known Systemd syncronization points for OpenBMC."
HOMEPAGE = "http://github.com/openbmc"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit allarch

# Overall chassis and host control
# - start: Services to run to start the host
# - stop:  Services to run to stop the host
CHASSIS_TARGETS = "start stop"

# Synchronization targets
# - start-pre:         Services to run before we start power on process
# - start:             Services to run to do the chassis power on
# - on:                Services to run once power is on
# - stop-pre,stop,off: Same as above but applied to powering off
# - reset-on:          Services to check if chassis power is on after bmc reset
SYNCH_POWER_TARGETS = "start-pre start on stop-pre stop off reset-on"

# Control chassis power
# - on:  Services to run to power on the chassis
# - off: Services to run to power off the chassis
CHASSIS_POWER_TARGETS = "on off"

#TODO - After target renames #1205, combine this with CHASSIS_POWER_TARGETS
# - reset: Services to check chassis power state and update chassis "on" target
CHASSIS_POWER_TARGETS_2 = "reset"

# Track all host synchronization point targets
# - start-pre:             Services to run before we start host boot
# - start:                 Services to run to do the host boot
# - started:               Services to run once the host is booted
# - stop-pre,stop,stopped: Same as above but applied to shutting down the host
HOST_SYNCH_TARGETS = "start-pre start started stop-pre stop stopped"

# Track all host action targets
# - stop:    Services to run to shutdown the host
# - quiesce: Target to enter on host boot failure
HOST_ACTION_TARGETS = "stop quiesce"

CHASSIS_FMT = "obmc-chassis-{0}@.target"
SYNCH_POWER_FMT = "obmc-power-{0}@.target"
CHASSIS_POWER_FMT = "obmc-power-chassis-{0}@.target"
CHASSIS_POWER_FMT_2 = "obmc-chassis-{0}@.target"
HOST_SYNCH_FMT = "obmc-host-{0}@.target"
HOST_ACTION_FMT = "obmc-{0}-host@.target"

CHASSIS_LINK_FMT = "${CHASSIS_FMT}:obmc-chassis-{0}@{1}.target"
SYNCH_POWER_LINK_FMT = "${SYNCH_POWER_FMT}:obmc-power-{0}@{1}.target"
CHASSIS_POWER_LINK_FMT = "${CHASSIS_POWER_FMT}:obmc-power-chassis-{0}@{1}.target"
CHASSIS_POWER_LINK_FMT_2 = "${CHASSIS_POWER_FMT_2}:obmc-chassis-{0}@{1}.target"
HOST_LINK_SYNCH_FMT = "${HOST_SYNCH_FMT}:obmc-host-{0}@{1}.target"
HOST_LINK_ACTION_FMT = "${HOST_ACTION_FMT}:obmc-{0}-host@{1}.target"

SYSTEMD_SERVICE_${PN} += " \
        obmc-mapper.target \
        obmc-webserver-pre.target \
        obmc-fans-ready.target \
        obmc-fan-control.target \
        obmc-standby.target \
        "

SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHASSIS_FMT', 'CHASSIS_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'SYNCH_POWER_FMT', 'SYNCH_POWER_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHASSIS_POWER_FMT', 'CHASSIS_POWER_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHASSIS_POWER_FMT_2', 'CHASSIS_POWER_TARGETS_2')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'HOST_SYNCH_FMT', 'HOST_SYNCH_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'HOST_ACTION_FMT', 'HOST_ACTION_TARGETS')}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHASSIS_LINK_FMT', 'CHASSIS_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'SYNCH_POWER_LINK_FMT', 'SYNCH_POWER_TARGETS', 'OBMC_POWER_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHASSIS_POWER_LINK_FMT', 'CHASSIS_POWER_TARGETS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHASSIS_POWER_LINK_FMT_2', 'CHASSIS_POWER_TARGETS_2', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'HOST_LINK_SYNCH_FMT', 'HOST_SYNCH_TARGETS', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'HOST_LINK_ACTION_FMT', 'HOST_ACTION_TARGETS', 'OBMC_HOST_INSTANCES')}"
