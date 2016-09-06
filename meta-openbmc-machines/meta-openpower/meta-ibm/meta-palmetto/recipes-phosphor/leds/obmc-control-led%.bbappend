FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_LINK_${PN} += "../obmc-led-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-start@identify.service"
