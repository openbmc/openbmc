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
#
# SYSTEMD_SUBSTITUTIONS_${path-relative-to-system_unitdir}
#    Variables in this list will be substituted in the specified
#    file during install (if bitbake finds python {format} strings
#    in the file itself).  List entries take the form:
#      VAR:VALUE
#    where {VAR} is the format string bitbake should look for in the
#    file and VALUE is the value to substitute.
#
# SYSTEMD_USER_${PN}.service = "foo"
# SYSTEMD_USER_${unit}.service = "foo"
#    The user for the unit/package.


inherit obmc-phosphor-utils
inherit systemd
inherit useradd

_INSTALL_SD_UNITS=""
SYSTEMD_DEFAULT_TARGET ?= "obmc-standby.target"

# Big ugly hack to prevent useradd.bbclass post-parse sanity checker failure.
# If there are users to be added, we'll add them in our post-parse.
# If not...there don't seem to be any ill effects...
USERADD_PACKAGES ?= " "
USERADD_PARAM_${PN} ?= ";"


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
        unit_dir = d.getVar('systemd_system_unitdir', True)
        set_append(d, 'SRC_URI', 'file://%s' % unit)
        set_append(d, 'FILES_%s' % pkg, '%s/%s' % (unit_dir, unit))
        set_append(d, '_INSTALL_SD_UNITS', unit)
        set_append(d, '_MAKE_SUBS', '%s' % unit)

        for x in [
                'base_bindir',
                'bindir',
                'sbindir',
                'SYSTEMD_DEFAULT_TARGET' ]:
            set_append(d, 'SYSTEMD_SUBSTITUTIONS_%s' % unit,
                '%s:%s' % (x, d.getVar(x, True)))


    def add_sd_user(d, unit, pkg):
        opts = [
            '--system',
            '--home',
            '/',
            '--no-create-home',
            '--shell /sbin/nologin',
            '--user-group']

        var = 'SYSTEMD_USER_%s' % unit
        user = listvar_to_list(d, var)
        if len(user) is 0:
            var = 'SYSTEMD_USER_%s' % pkg
            user = listvar_to_list(d, var)
        if len(user) is not 0:
            if len(user) is not 1:
                bb.fatal('Too many users assigned to %s: \'%s\'' % (var, ' '.join(user)))

            user = user[0]
            set_append(d, 'SYSTEMD_SUBSTITUTIONS_%s' % unit,
                'USER:%s' % user)
            if user not in d.getVar('USERADD_PARAM_%s' % pkg, True):
                set_append(
                    d,
                    'USERADD_PARAM_%s' % pkg,
                    '%s' % (' '.join(opts + [user])),
                    ';')
            if pkg not in d.getVar('USERADD_PACKAGES', True):
                set_append(d, 'USERADD_PACKAGES', pkg)


    pn = d.getVar('PN', True)
    if d.getVar('SYSTEMD_SERVICE_%s' % pn, True) is None:
        d.setVar('SYSTEMD_SERVICE_%s' % pn, '%s.service' % pn)

    for pkg in listvar_to_list(d, 'SYSTEMD_PACKAGES'):
        for unit in listvar_to_list(d, 'SYSTEMD_SERVICE_%s' % pkg):
            check_sd_unit(d, unit)
            add_sd_unit(d, unit, pkg)
            add_sd_user(d, unit, pkg)
}


python systemd_do_postinst() {
    for f in listvar_to_list(d, '_MAKE_SUBS'):
        subs = dict([ x.split(':') for x in
            listvar_to_list(d, 'SYSTEMD_SUBSTITUTIONS_%s' % f)])
        if not subs:
            continue

        path = d.getVar('D', True)
        path += d.getVar('systemd_system_unitdir', True)
        path += '/%s' % f
        with open(path, 'r') as fd:
            content = fd.read()
        with open(path, 'w+') as fd:
            try:
                fd.write(content.format(**subs))
            except KeyError as e:
                bb.fatal('No substitution found for %s in '
                    'file \'%s\'' % (e, f))
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


do_install[postfuncs] += "systemd_do_postinst"
