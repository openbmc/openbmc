FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

inherit obmc-phosphor-systemd

SYSTEMD_OVERRIDE:${PN}:append = "\
    after-gpiopresence.conf:phosphor-dbus-monitor.service.d/after-gpiopresence.conf \
    "
