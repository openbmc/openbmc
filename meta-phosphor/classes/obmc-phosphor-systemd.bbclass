# Common code for Phosphor OpenBMC systemd services.

inherit systemd

SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SRC_URI += " \
        file://${BPN}.service \
        "

do_install_append() {
        # install systemd unit files
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_unitdir}/system
}
