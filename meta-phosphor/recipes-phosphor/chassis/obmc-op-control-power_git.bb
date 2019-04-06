SUMMARY = "org.openbmc.control.Power implementation for OpenPOWER"
DESCRIPTION = "A power control implementation suitable for OpenPOWER systems."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

DEPENDS += "phosphor-mapper systemd"

SKELETON_DIR = "op-pwrctl"

FMT = "org.openbmc.control.Power@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_POWER_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += " \
        op-power-start@.service \
        op-wait-power-on@.service \
        op-power-stop@.service \
        op-wait-power-off@.service \
        op-reset-chassis-running@.service \
        op-reset-chassis-on@.service \
        op-powered-off@.service \
        "

SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/power_control"

START_TMPL = "op-power-start@.service"
START_TGTFMT = "obmc-chassis-poweron@{1}.target"
START_INSTFMT = "op-power-start@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"

STOP_TMPL = "op-power-stop@.service"
STOP_TGTFMT = "obmc-chassis-poweroff@{1}.target"
STOP_INSTFMT = "op-power-stop@{0}.service"
STOP_FMT = "../${STOP_TMPL}:${STOP_TGTFMT}.requires/${STOP_INSTFMT}"

POWERED_OFF_TMPL = "op-powered-off@.service"
POWERED_OFF_INSTFMT = "op-powered-off@{0}.service"
POWERED_OFF_FMT = "../${POWERED_OFF_TMPL}:${STOP_TGTFMT}.requires/${POWERED_OFF_INSTFMT}"

ON_TMPL = "op-wait-power-on@.service"
ON_INSTFMT = "op-wait-power-on@{0}.service"
ON_FMT = "../${ON_TMPL}:${START_TGTFMT}.requires/${ON_INSTFMT}"

OFF_TMPL = "op-wait-power-off@.service"
OFF_INSTFMT = "op-wait-power-off@{0}.service"
OFF_FMT = "../${OFF_TMPL}:${STOP_TGTFMT}.requires/${OFF_INSTFMT}"

RESET_TGTFMT = "obmc-chassis-powerreset@{1}.target"

RESET_ON_TMPL = "op-reset-chassis-running@.service"
RESET_ON_INSTFMT = "op-reset-chassis-running@{0}.service"
RESET_ON_FMT = "../${RESET_ON_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_INSTFMT}"

RESET_ON_CHASSIS_TMPL = "op-reset-chassis-on@.service"
RESET_ON_CHASSIS_INSTFMT = "op-reset-chassis-on@{0}.service"
RESET_ON_CHASSIS_FMT = "../${RESET_ON_CHASSIS_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_CHASSIS_INSTFMT}"

# Build up requires relationship for START_TGTFMT and STOP_TGTFMT
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'POWERED_OFF_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'OFF_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_CHASSIS_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Now show that the main control target requires these power targets
START_TMPL_CTRL = "obmc-chassis-poweron@.target"
START_TGTFMT_CTRL = "obmc-host-startmin@{1}.target"
START_INSTFMT_CTRL = "obmc-chassis-poweron@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT_CTRL', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Chassis off requires host off
STOP_TMPL_CTRL = "obmc-host-stop@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT_CTRL = "obmc-host-stop@{1}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Hard power off requires chassis off
HARD_OFF_TMPL_CTRL = "obmc-chassis-poweroff@.target"
HARD_OFF_TGTFMT_CTRL = "obmc-chassis-hard-poweroff@{0}.target"
HARD_OFF_INSTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
HARD_OFF_FMT_CTRL = "../${HARD_OFF_TMPL_CTRL}:${HARD_OFF_TGTFMT_CTRL}.requires/${HARD_OFF_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HARD_OFF_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Force the standby target to run the chassis reset check target
RESET_TMPL_CTRL = "obmc-chassis-powerreset@.target"
SYSD_TGT = "multi-user.target"
RESET_INSTFMT_CTRL = "obmc-chassis-powerreset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"
