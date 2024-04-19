FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " host-gpio"
EXTRA_OEMESON:append = " \
                         -Dboot-count-max-allowed=1 \
                       "

FILES:${PN} += "${systemd_system_unitdir}/*"
