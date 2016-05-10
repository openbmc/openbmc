# Common code for Phosphor OpenBMC systemd services.

inherit systemd

SYSTEMD_SERVICE_${PN} ?= "${BPN}.service"

python() {
        service_files = d.getVar(
                'SYSTEMD_SERVICE_' + d.getVar('BPN', True),
                True)
        if service_files:
                uris = " ".join(['file://' + s for s in service_files.split()])
                d.appendVar('SRC_URI', ' ' + uris)
}

do_install_append() {
        # install systemd unit files
        install -d ${D}${systemd_unitdir}/system
        for i in ${SYSTEMD_SERVICE_${PN}}; do
                install -m 0644 ${WORKDIR}/$i ${D}${systemd_unitdir}/system
        done
}
