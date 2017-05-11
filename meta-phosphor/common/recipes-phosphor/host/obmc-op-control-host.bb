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
        op-init-pnor@.service \
        "

START_TMPL = "op-start-host@.service"
START_TGTFMT = "obmc-host-start@{1}.target"
START_INSTFMT = "obmc-start-host@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"

INIT_PNOR_TMPL = "op-init-pnor@.service"
INIT_PNOR_INSTFMT = "op-init-pnor@{0}.service"
INIT_PNOR_FMT = "../${INIT_PNOR_TMPL}:${START_TGTFMT}.requires/${INIT_PNOR_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'INIT_PNOR_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
