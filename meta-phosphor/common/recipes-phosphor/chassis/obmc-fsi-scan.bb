SUMMARY = "Scan for FSI devices"
DESCRIPTION = "Tells the OpenFSI device driver to scan for FSI devices"
PR = "r1"

inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-license

TMPL = "fsi-scan.service"
TGTFMT = "obmc-fsi-enable.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${TMPL}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${FMT}"
