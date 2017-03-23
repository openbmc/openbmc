FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_LINK_${PN} += "../obmc-led-group-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-group-start@BmcBooted.service"

STATES = "start stop"
TMPLFMT = "obmc-led-group-{0}@.service"
TGTFMT = "obmc-power-{0}@0.target"
INSTFMT = "obmc-led-group-{0}@PowerOn.service"
FMT = "../${TMPLFMT}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'STATES')}"
