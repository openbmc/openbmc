FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

IDENTIFY_OVERRIDE = "obmc-identify-deps.conf"
SYSTEMD_OVERRIDE_${PN} += "${IDENTIFY_OVERRIDE}:obmc-led-start@identify.service.d/${IDENTIFY_OVERRIDE}"
SYSTEMD_LINK_${PN} += "../obmc-led-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-start@identify.service"
