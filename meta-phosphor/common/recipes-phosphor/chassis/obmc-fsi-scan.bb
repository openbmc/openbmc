SUMMARY = "Scan for FSI devices"
DESCRIPTION = "Tells the OpenFSI device driver to scan for FSI devices"
PR = "r1"

inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-license

TMPL = "fsi-scan@.service"
INSTFMT = "fsi-scan@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SUBSTITUTIONS += "MACHINE:${MACHINE}:${TMPL}"
