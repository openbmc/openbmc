SUMMARY = "org.openbmc.control.Power implemention for OpenPOWER"
DESCRIPTION = "A power control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

DEPENDS += "obmc-mapper systemd"

SKELETON_DIR = "op-pwrctl"
BASE_BUSNAME = "org.openbmc.control.Power"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_SERVICE_${PN} += " \
        op-power-start@.service \
        op-wait-power-on@.service \
        op-power-stop@.service \
        op-wait-power-off@.service \
        "

SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/power_control"

SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../op-power-start@.service:obmc-chassis-start@[1].target.wants/op-power-start@[0].service:OBMC_POWER_INSTANCES:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../op-wait-power-on@.service:obmc-chassis-start@[1].target.wants/op-wait-power-on@[0].service:OBMC_POWER_INSTANCES:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../op-power-stop@.service:obmc-chassis-stop@[1].target.wants/op-power-stop@[0].service:OBMC_POWER_INSTANCES:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../op-wait-power-off@.service:obmc-chassis-stop@[1].target.wants/op-wait-power-off@[0].service:OBMC_POWER_INSTANCES:OBMC_CHASSIS_INSTANCES"
