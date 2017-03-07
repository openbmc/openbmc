SUMMARY = "Disable and enables the FSI device driver"
DESCRIPTION = "Binds and unbinds the FSI device driver"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TMPL_B = "fsi-bind.service"
TGTFMT_B = "obmc-fsi-enable.target"
FMT_B = "../${TMPL_B}:${TGTFMT_B}.requires/${TMPL_B}"

TMPL_U = "fsi-unbind.service"
TGTFMT_U = "obmc-fsi-disable.target"
FMT_U = "../${TMPL_U}:${TGTFMT_U}.requires/${TMPL_U}"

SYSTEMD_SERVICE_${PN} += "${TMPL_B} ${TMPL_U}"
SYSTEMD_LINK_${PN} += "${FMT_B} ${FMT_U}"
