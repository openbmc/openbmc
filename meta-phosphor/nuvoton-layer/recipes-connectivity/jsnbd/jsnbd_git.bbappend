FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

FILES_${PN} += "${sysconfdir}/nbd-proxy/state"
SRC_URI += "file://state_hook"

do_install_append() {
    install -d ${D}${sysconfdir}/nbd-proxy/
    install -m 0755 ${WORKDIR}/state_hook ${D}${sysconfdir}/nbd-proxy/state
}
