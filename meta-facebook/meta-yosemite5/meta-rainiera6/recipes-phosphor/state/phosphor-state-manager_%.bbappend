FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-force-warm-reboot@{}.target.requires/host-powerreset@{}.service \
    "
