SUMMARY = "org.openbmc.control.Power implemention for OpenPOWER"
DESCRIPTION = "A power control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

DEPENDS += "phosphor-mapper systemd"

SKELETON_DIR = "op-pwrctl"

FMT = "org.openbmc.control.Power@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_POWER_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += " \
        op-power-start@.service \
        op-wait-power-on@.service \
        op-power-stop@.service \
        op-wait-power-off@.service \
        op-reset-pgood-check@.service \
        op-reset-set-power-on@.service \
        op-reset-chassis-on@.service \
        "

SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/power_control"

START_TMPL = "op-power-start@.service"
START_TGTFMT = "obmc-power-chassis-on@{1}.target"
START_INSTFMT = "op-power-start@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"

STOP_TMPL = "op-power-stop@.service"
STOP_TGTFMT = "obmc-power-chassis-off@{1}.target"
STOP_INSTFMT = "op-power-stop@{0}.service"
STOP_FMT = "../${STOP_TMPL}:${STOP_TGTFMT}.requires/${STOP_INSTFMT}"

ON_TMPL = "op-wait-power-on@.service"
ON_INSTFMT = "op-wait-power-on@{0}.service"
ON_FMT = "../${ON_TMPL}:${START_TGTFMT}.requires/${ON_INSTFMT}"

OFF_TMPL = "op-wait-power-off@.service"
OFF_INSTFMT = "op-wait-power-off@{0}.service"
OFF_FMT = "../${OFF_TMPL}:${STOP_TGTFMT}.requires/${OFF_INSTFMT}"

RESET_TMPL = "op-reset-pgood-check@.service"
RESET_TGTFMT = "obmc-chassis-reset@{1}.target"
RESET_INSTFMT = "op-reset-pgood-check@{0}.service"
RESET_FMT = "../${RESET_TMPL}:${RESET_TGTFMT}.requires/${RESET_INSTFMT}"

RESET_ON_TMPL = "op-reset-set-power-on@.service"
RESET_ON_INSTFMT = "op-reset-set-power-on@{0}.service"
RESET_ON_FMT = "../${RESET_ON_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_INSTFMT}"

RESET_ON_CHASSIS_TMPL = "op-reset-chassis-on@.service"
RESET_ON_CHASSIS_INSTFMT = "op-reset-chassis-on@{0}.service"
RESET_ON_CHASSIS_FMT = "../${RESET_ON_CHASSIS_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_CHASSIS_INSTFMT}"

# Build up requires relationship for START_TGTFMT and STOP_TGTFMT
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'OFF_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_CHASSIS_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Now show that the main control target requires these power targets
START_TMPL_CTRL = "obmc-power-chassis-on@.target"
START_TGTFMT_CTRL = "obmc-chassis-start@{1}.target"
START_INSTFMT_CTRL = "obmc-power-chassis-on@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT_CTRL', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

STOP_TMPL_CTRL = "obmc-power-chassis-off@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-stop@{1}.target"
STOP_INSTFMT_CTRL = "obmc-power-chassis-off@{0}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT_CTRL', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Force the standby target to run the chassis reset check target
RESET_TMPL_CTRL = "obmc-chassis-reset@.target"
SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"
RESET_INSTFMT_CTRL = "obmc-chassis-reset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"
