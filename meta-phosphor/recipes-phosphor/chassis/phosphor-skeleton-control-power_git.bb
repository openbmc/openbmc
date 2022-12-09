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
