# Common code for systemd based services.
#
# Prior to inheriting this class, recipes can define services like this:
#
# SYSTEMD_SERVICE_${PN} = "foo.service bar.socket baz@.service"
#
# and these files will be added to the main package if they exist.
#
# Alternatively this class can just be inherited and
# ${PN}.service will be added to the main package.
inherit systemd


python() {
    pn = d.getVar('PN', True)
    searchpaths = d.getVar('FILESPATH', True)

    services = d.getVar('SYSTEMD_SERVICE_' + pn, True)

    if services:
        services = services.split()
    else:
        services = [pn + '.service']

    for s in services:
        file = s
        path = bb.utils.which(searchpaths, file)
        if os.path.isfile(path):
            d.appendVar('SRC_URI', ' file://' + file)
            d.appendVar("FILES_%s" %(pn), " %s/%s" \
                % (d.getVar('systemd_system_unitdir', True), file))
            d.appendVar('OBMC_SYSTEMD_SERVICES', ' ' + file)
            if file not in (d.getVar('SYSTEMD_SERVICE_' + pn, True) or "").split():
                d.appendVar('SYSTEMD_SERVICE_' + pn, ' ' + file)
        else:
            bb.error("Could not find service file: %s" % file)
}

do_install_append() {
        # install systemd service/socket/template files
        if [ "${OBMC_SYSTEMD_SERVICES}" ]; then
                install -d ${D}${systemd_system_unitdir}
        fi
        for s in ${OBMC_SYSTEMD_SERVICES}; do
                install -m 0644 ${WORKDIR}/$s ${D}${systemd_system_unitdir}
                sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
                        -e 's,@BINDIR@,${bindir},g' \
                        -e 's,@SBINDIR@,${sbindir},g' \
                        ${D}${systemd_system_unitdir}/$s
        done
}
