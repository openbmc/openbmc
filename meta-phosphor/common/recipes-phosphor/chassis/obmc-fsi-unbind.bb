SUMMARY = "Disable the FSI Device driver"
DESCRIPTION = "Unbinds the FSI device driver"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TMPL = "fsi-unbind.service"
TGTFMT = "obmc-fsi-disable.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${TMPL}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${FMT}"
