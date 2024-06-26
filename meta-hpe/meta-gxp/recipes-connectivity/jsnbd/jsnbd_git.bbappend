FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FILES:${PN} += "${sysconfdir}/nbd-proxy/state"
SRC_URI += "file://state_hook"

do_install:append() {
    install -d ${D}${sysconfdir}/nbd-proxy/
    install -m 0755 ${UNPACKDIR}/state_hook ${D}${sysconfdir}/nbd-proxy/state
}
