FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_LINK_${PN} += "../obmc-led-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-start@beep.service"

STATES = "start stop"
SYSTEMD_GENLINKS_${PN} += "../obmc-led-[0]@.service:obmc-power-[0]@0.target.wants/obmc-led-[0]@identify.service:STATES"
