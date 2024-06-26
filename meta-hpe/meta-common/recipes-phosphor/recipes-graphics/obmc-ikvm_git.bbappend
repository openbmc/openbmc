FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
SRC_URI += " file://obmc-ikvm.service "
SRC_URI += " file://create_usbhid.sh "

FILES:${PN} += " \
    ${systemd_system_unitdir}/obmc-ikvm.service \
    ${bindir}/create_usbhid.sh \
"

do_install:append () {
    install -D -m 0644 ${UNPACKDIR}/obmc-ikvm.service ${D}${systemd_system_unitdir}
    install -D -m 0755 ${UNPACKDIR}/create_usbhid.sh ${D}${bindir}
}
