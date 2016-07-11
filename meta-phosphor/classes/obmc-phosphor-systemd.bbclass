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
#
# Other variables:
# INHIBIT_SYSTEMD_RESTART_POLICY_${unit}
#    Inhibit the warning that is displayed if a service unit without a
#    restart policy is detected.

inherit obmc-phosphor-utils
inherit systemd

_INSTALL_SD_UNITS=""


def systemd_is_service(unit):
    return unit.endswith('.service')


def systemd_is_template(unit):
    return '@.' in unit


def systemd_parse_unit(d, path):
    import ConfigParser
    parser = ConfigParser.SafeConfigParser()
    parser.optionxform = str
    parser.read('%s' % path)
    return parser


python() {
    def check_sd_unit(d, unit):
        searchpaths = d.getVar('FILESPATH', True)
        path = bb.utils.which(searchpaths, '%s' % unit)
        if not os.path.isfile(path):
            bb.fatal('Did not find unit file "%s"' % unit)

        parser = systemd_parse_unit(d, path)
        inhibit = listvar_to_list(d, 'INHIBIT_SYSTEMD_RESTART_POLICY_WARNING')
        if systemd_is_service(unit) and \
                not systemd_is_template(unit) and \
                unit not in inhibit and \
                not parser.has_option('Service', 'Restart'):
            bb.warn('Systemd unit \'%s\' does not '
                'have a restart policy defined.' % unit)


    def add_sd_unit(d, unit, pkg):
        set_append(d, 'SRC_URI', 'file://%s' % unit)
        set_append(d, 'FILES_%s' % pkg, '%s/%s' \
            % (d.getVar('systemd_system_unitdir', True), unit))
        set_append(d, '_INSTALL_SD_UNITS', unit)


    pn = d.getVar('PN', True)
    if d.getVar('SYSTEMD_SERVICE_%s' % pn, True) is None:
        d.setVar('SYSTEMD_SERVICE_%s' % pn, '%s.service' % pn)

    for pkg in listvar_to_list(d, 'SYSTEMD_PACKAGES'):
        for unit in listvar_to_list(d, 'SYSTEMD_SERVICE_%s' % pkg):
            check_sd_unit(d, unit)
            add_sd_unit(d, unit, pkg)
}


do_install_append() {
        # install systemd service/socket/template files
        [ -z "${_INSTALL_SD_UNITS}" ] || \
                install -d ${D}${systemd_system_unitdir}
        for s in ${_INSTALL_SD_UNITS}; do
                install -m 0644 ${WORKDIR}/$s \
                        ${D}${systemd_system_unitdir}/$s
                sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
                        -e 's,@BINDIR@,${bindir},g' \
                        -e 's,@SBINDIR@,${sbindir},g' \
                        ${D}${systemd_system_unitdir}/$s
        done
}
