FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " file://ssifbridge-override.conf"

FILES:${PN} += "${systemd_system_unitdir}/ssifbridge.service.d"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/ssifbridge.service.d
    install -m 644 ${UNPACKDIR}/ssifbridge-override.conf \
        ${D}${systemd_system_unitdir}/ssifbridge.service.d
}
