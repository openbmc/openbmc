FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_LINK_${PN} += "../obmc-led-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-start@beep.service"

STATES = "start stop"
TMPLFMT = "obmc-led-{0}@.service"
TGTFMT = "obmc-power-{0}@0.target"
INSTFMT = "obmc-led-{0}@identify.service"
FMT = "../${TMPLFMT}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'STATES')}"
