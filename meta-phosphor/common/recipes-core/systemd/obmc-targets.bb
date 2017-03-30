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

POW_CHASSIS_ON_TMPL = "obmc-power-chassis-on@.target"
POW_CHASSIS_ON = "obmc-power-chassis-on@.target"
POW_CHASSIS_ON_INSTFMT = "obmc-chassis-poweron@{0}.target"
POW_CHASSIS_OFF_TMPL = "obmc-chassis-poweroff@.target"
POW_CHASSIS_OFF = "obmc-power-chassis-off@{0}.target"
POW_CHASSIS_OFF_INSTFMT = "obmc-chassis-poweroff@{0}.target"
CHASSIS_STOP_TMPL = "obmc-host-stop@.target"
CHASSIS_STOP = "obmc-chassis-stop@{0}.target"
CHASSIS_STOP_INSTFMT = "obmc-host-stop@{0}.target"
CHASSIS_START_TMPL = "obmc-host-start@.target"
CHASSIS_START = "obmc-chassis-start@{0}.target"
CHASSIS_START_INSTFMT = "obmc-host-start@{0}.target"
STOP_HOST_TMPL = "obmc-host-shutdown@.target"
STOP_HOST = "obmc-stop-host@{0}.target"
STOP_HOST_INSTFMT = "obmc-host-shutdown@{0}.target"

START_CHASSIS_POWON = "${POW_CHASSIS_ON_TMPL}:${POW_CHASSIS_ON}.requires/${POW_CHASSIS_ON_INSTFMT}"
START_CHASSIS_POWOFF = "${POW_CHASSIS_OFF_TMPL}:${POW_CHASSIS_OFF}.requires/${POW_CHASSIS_OFF_INSTFMT}"
START_HOST_STOP = "${CHASSIS_STOP_TMPL}:${CHASSIS_STOP}.requires/${CHASSIS_STOP_INSTFMT}"
START_HOST_START = "${CHASSIS_START_TMPL}:${CHASSIS_START}.requires/${CHASSIS_START_INSTFMT}"
START_HOST_SHUTDOWN = "${STOP_HOST_TMPL}:${STOP_HOST}.requires/${STOP_HOST_INSTFMT}"

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

SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'START_CHASSIS_POWON', 'CHASSIS_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'START_CHASSIS_POWOFF', 'CHASSIS_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'START_HOST_STOP', 'HOST_ACTION_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'START_HOST_START', 'HOST_ACTION_TARGETS')}"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'START_HOST_SHUTDOWN', 'HOST_ACTION_TARGETS')}"

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
