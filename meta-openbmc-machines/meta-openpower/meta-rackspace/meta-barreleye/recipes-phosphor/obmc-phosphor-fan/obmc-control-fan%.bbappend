FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_GENLINKS_${PN} += "../obmc-max-fans.service:obmc-power-start@[0].target.wants/obmc-max-fans.service:OBMC_POWER_INSTANCES"
