FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
SRC_URI += " file://start-ipkvm.service "
SRC_URI += " file://create_usbhid.sh "

FILES:${PN} += " \
    ${systemd_system_unitdir}/start-ipkvm.service \
    ${bindir}/create_usbhid.sh \
"

do_install:append () {
    install -D -m 0755 ${WORKDIR}/start-ipkvm.service ${D}${systemd_system_unitdir}
    install -D -m 0755 ${WORKDIR}/create_usbhid.sh ${D}${bindir}
}