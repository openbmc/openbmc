FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

FAN_OVERRIDE = "obmc-max-fans-deps.conf"
SYSTEMD_OVERRIDE_${PN} += "${FAN_OVERRIDE}:obmc-max-fans.service.d/${FAN_OVERRIDE}"
SYSTEMD_LINK_${PN} += "../obmc-max-fans.service:obmc-chassis-start@0.target.wants/obmc-max-fans.service"
