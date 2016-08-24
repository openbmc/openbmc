FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

BEEP_OVERRIDE = "obmc-beep-deps.conf"
SYSTEMD_OVERRIDE_${PN} += "${BEEP_OVERRIDE}:obmc-led-start@beep.service.d/${BEEP_OVERRIDE}"
SYSTEMD_LINK_${PN} += "../obmc-led-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-start@beep.service"

STATES = "start stop"

SYSTEMD_GENLINKS_${PN} += "../obmc-led-[0]@.service:obmc-chassis-[0]@0.target.wants/obmc-led-[0]@identify.service:STATES"
SYSTEMD_GENOVERRIDES_${PN} += "obmc-identify-deps.conf:obmc-led-[0]@identify.service.d/obmc-identify-[0]-deps.conf:STATES"
SYSTEMD_GENSUBSTITUTIONS += "STATE:[0]:obmc-led-[0]@identify.service.d/obmc-identify-[0]-deps.conf:STATES"
