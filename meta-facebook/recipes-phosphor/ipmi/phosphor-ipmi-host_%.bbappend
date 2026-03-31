FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://fb-ipmi-oem.conf \
"

SYSTEMD_OVERRIDE:${PN} += "fb-ipmi-oem.conf:phosphor-ipmi-host.service.d/fb-ipmi-oem.conf"
