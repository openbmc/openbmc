SUMMARY = "org.openbmc.control.Power implementation for OpenBMC"
DESCRIPTION = "A power control implementation suitable for OpenBMC systems."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

DEPENDS += "libmapper systemd"

SKELETON_DIR = "op-pwrctl"

OBMC_CONTROL_POWER_FMT ?= "org.openbmc.control.Power@{0}.service"
DBUS_SERVICE:${PN} += "${@compose_list(d, 'OBMC_CONTROL_POWER_FMT', 'OBMC_POWER_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += " \
        phosphor-wait-power-on@.service \
        phosphor-wait-power-off@.service \
        "

SYSTEMD_ENVIRONMENT_FILE:${PN} += "obmc/power_control"

START_TGTFMT = "obmc-chassis-poweron@{1}.target"
ON_TMPL = "phosphor-wait-power-on@.service"
ON_INSTFMT = "phosphor-wait-power-on@{0}.service"
ON_FMT = "../${ON_TMPL}:${START_TGTFMT}.requires/${ON_INSTFMT}"

STOP_TGTFMT = "obmc-chassis-poweroff@{1}.target"
OFF_TMPL = "phosphor-wait-power-off@.service"
OFF_INSTFMT = "phosphor-wait-power-off@{0}.service"
OFF_FMT = "../${OFF_TMPL}:${STOP_TGTFMT}.requires/${OFF_INSTFMT}"

# Build up requires relationship for START_TGTFMT and STOP_TGTFMT
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'ON_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'OFF_FMT', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Now show that the main control target requires these power targets
START_TMPL_CTRL = "obmc-chassis-poweron@.target"
START_TGTFMT_CTRL = "obmc-host-startmin@{1}.target"
START_INSTFMT_CTRL = "obmc-chassis-poweron@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'START_FMT_CTRL', 'OBMC_POWER_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

# Chassis off requires host off
STOP_TMPL_CTRL = "obmc-host-stop@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT_CTRL = "obmc-host-stop@{1}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'STOP_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Hard power off requires chassis off
HARD_OFF_TMPL_CTRL = "obmc-chassis-poweroff@.target"
HARD_OFF_TGTFMT_CTRL = "obmc-chassis-hard-poweroff@{0}.target"
HARD_OFF_INSTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
HARD_OFF_FMT_CTRL = "../${HARD_OFF_TMPL_CTRL}:${HARD_OFF_TGTFMT_CTRL}.requires/${HARD_OFF_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HARD_OFF_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Force the standby target to run the chassis reset check target
RESET_TMPL_CTRL = "obmc-chassis-powerreset@.target"
SYSD_TGT = "multi-user.target"
RESET_INSTFMT_CTRL = "obmc-chassis-powerreset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"
