SUMMARY = "org.openbmc.control.Host implementation for OpenPOWER"
DESCRIPTION = "A host control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

PROVIDES += "virtual/obmc-host-ctl"
RPROVIDES_${PN} += "virtual-obmc-host-ctl"

SKELETON_DIR = "op-hostctl"

FMT = "org.openbmc.control.Host@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_SERVICE_${PN} = " \
        op-start-host@.service \
        op-reset-host-check@.service \
        op-reset-host-running@.service \
        "

START_TMPL = "op-start-host@.service"
START_TGTFMT = "obmc-chassis-start@{1}.target"
START_INSTFMT = "obmc-start-host@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"

RESET_CHECK_TMPL = "op-reset-host-check@.service"
RESET_CHECK_TGTFMT = "obmc-host-reset@{1}.target"
RESET_CHECK_INSTFMT = "op-reset-host-check@{0}.service"
RESET_CHECK_FMT = "../${RESET_CHECK_TMPL}:${RESET_CHECK_TGTFMT}.requires/${RESET_CHECK_INSTFMT}"

RESET_RUNNING_TMPL = "op-reset-host-running@.service"
RESET_RUNNING_TGTFMT = "obmc-host-reset@{1}.target"
RESET_RUNNING_INSTFMT = "op-reset-host-running@{0}.service"
RESET_RUNNING_FMT = "../${RESET_RUNNING_TMPL}:${RESET_RUNNING_TGTFMT}.requires/${RESET_RUNNING_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_CHECK_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_RUNNING_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Force the standby target to run the host reset check target
RESET_TMPL_CTRL = "obmc-host-reset@.target"
SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"
RESET_INSTFMT_CTRL = "obmc-host-reset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"
