FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " file://ssifbridge.service"

do_install:append() {
    cp ${WORKDIR}/ssifbridge.service ${D}${systemd_system_unitdir}/ssifbridge.service
}
