SUMMARY = "org.openbmc.control.Power implemention for OpenPOWER"
DESCRIPTION = "A power control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

DEPENDS += "obmc-mapper systemd"

DEPENDS += "obmc-mapper systemd"

SKELETON_DIR = "op-pwrctl"

FMT = "org.openbmc.control.Power@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_POWER_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += " \
        op-power-start@.service \
        op-wait-power-on@.service \
        op-power-stop@.service \
        op-wait-power-off@.service \
        "

SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/power_control"

START_TMPL = "op-power-start@.service"
START_TGTFMT = "obmc-chassis-start@{1}.target"
START_INSTFMT = "op-power-start@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.wants/${START_INSTFMT}"

STOP_TMPL = "op-power-stop@.service"
STOP_TGTFMT = "obmc-chassis-stop@{1}.target"
STOP_INSTFMT = "op-power-stop@{0}.service"
STOP_FMT = "../${STOP_TMPL}:${STOP_TGTFMT}.wants/${STOP_INSTFMT}"

ON_TMPL = "op-wait-power-on@.service"
ON_INSTFMT = "op-wait-power-on@{0}.service"
ON_FMT = "../${ON_TMPL}:${START_TGTFMT}.wants/${ON_INSTFMT}"

OFF_TMPL = "op-wait-ppower-off@.service"
OFF_INSTFMT = "op-wait-power-off@{0}.service"
OFF_FMT = "../${OFF_TMPL}:${STOP_TGTFMT}.wants/${OFF_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'OFF_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
