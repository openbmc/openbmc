# Common code for Phosphor OpenBMC systemd services.

inherit systemd

SYSTEMD_SERVICE_${PN} = "${PN}.service"
SRC_URI += " \
        file://${PN}.service \
        "

do_install_append() {
        # install systemd unit files
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/${PN}.service ${D}${systemd_unitdir}/system
}
