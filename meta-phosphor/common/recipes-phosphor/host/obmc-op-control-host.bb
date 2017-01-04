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
SYSTEMD_SERVICE_${PN} = "op-start-host@.service"

START_TMPL = "op-start-host@.service"
START_TGTFMT = "obmc-chassis-start@{1}.target"
START_INSTFMT = "obmc-start-host@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.wants/${START_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
