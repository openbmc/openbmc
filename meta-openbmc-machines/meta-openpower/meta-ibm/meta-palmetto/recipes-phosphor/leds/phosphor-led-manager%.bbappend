FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_LINK_${PN} += "../obmc-led-group-start@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-led-group-start@BmcBooted.service"
