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
#
# SYSTEMD_ENVIRONMENT_FILE_${PN} = "foo"
#    One or more environment files to be installed.


inherit obmc-phosphor-utils
inherit systemd
inherit useradd

_INSTALL_SD_UNITS=""
SYSTEMD_DEFAULT_TARGET ?= "obmc-standby.target"
envfiledir ?= "${sysconfdir}/default"

# Big ugly hack to prevent useradd.bbclass post-parse sanity checker failure.
# If there are users to be added, we'll add them in our post-parse.
# If not...there don't seem to be any ill effects...
USERADD_PACKAGES ?= " "
USERADD_PARAM_${PN} ?= ";"


def SystemdUnit(unit):
    class Unit(object):
        def __init__(self, unit):
            self.unit = unit

        def __getattr__(self, item):
            if item is 'name':
                return self.unit
            if item is 'is_activated':
                return self.unit.startswith('dbus-')
            if item is 'is_template':
                return '@.' in self.unit
            if item is 'is_instance':
                return '@' in self.unit and not self.is_template
            if item in ['is_service', 'is_target']:
                return self.unit.split('.')[-1] == item
            if item is 'base':
                cls = self.unit.split('.')[-1]
                base = self.unit.replace('dbus-', '')
                base = base.replace('.%s' % cls, '')
                if self.is_instance:
                    base = base.rstrip('@%s' % self.instance)
                if self.is_template:
                    base = base.rstrip('@')
                return base
            if item is 'instance' and self.is_instance:
                inst = self.unit.rsplit('@')[-1]
                return inst.rsplit('.')[0]
            if item is 'template' and self.is_instance:
                cls = self.unit.split('.')[-1]
                return '%s@.%s' % (self.base, cls)
            if item is 'template' and self.is_template:
                return '.'.join(self.base.split('@')[:-1])

            raise AttributeError(item)
    return Unit(unit)


def systemd_parse_unit(d, path):
    import ConfigParser
    parser = ConfigParser.SafeConfigParser()
    parser.optionxform = str
    parser.read('%s' % path)
    return parser


python() {
    def check_sd_unit(d, unit):
        searchpaths = d.getVar('FILESPATH', True)
        path = bb.utils.which(searchpaths, '%s' % unit.name)
        if not os.path.isfile(path):
            bb.fatal('Did not find unit file "%s"' % unit.name)

        parser = systemd_parse_unit(d, path)
        inhibit = listvar_to_list(d, 'INHIBIT_SYSTEMD_RESTART_POLICY_WARNING')
        if unit.is_service and \
                not unit.is_template and \
                unit.name not in inhibit and \
                not parser.has_option('Service', 'Restart'):
            bb.warn('Systemd unit \'%s\' does not '
                'have a restart policy defined.' % unit.name)


    def add_default_subs(d, file):
        set_append(d, '_MAKE_SUBS', '%s' % file)

        for x in [
                'base_bindir',
                'bindir',
                'sbindir',
                'envfiledir',
                'SYSTEMD_DEFAULT_TARGET' ]:
            set_append(d, 'SYSTEMD_SUBSTITUTIONS_%s' % file,
                '%s:%s' % (x, d.getVar(x, True)))


    def add_sd_unit(d, unit, pkg):
        name = unit.name
        unit_dir = d.getVar('systemd_system_unitdir', True)
        set_append(d, 'SRC_URI', 'file://%s' % name)
        set_append(d, 'FILES_%s' % pkg, '%s/%s' % (unit_dir, name))
        set_append(d, '_INSTALL_SD_UNITS', name)
        add_default_subs(d, name)


    def add_sd_user(d, file, pkg):
        opts = [
            '--system',
            '--home',
            '/',
            '--no-create-home',
            '--shell /sbin/nologin',
            '--user-group']

        var = 'SYSTEMD_USER_%s' % file
        user = listvar_to_list(d, var)
        if len(user) is 0:
            var = 'SYSTEMD_USER_%s' % pkg
            user = listvar_to_list(d, var)
        if len(user) is not 0:
            if len(user) is not 1:
                bb.fatal('Too many users assigned to %s: \'%s\'' % (var, ' '.join(user)))

            user = user[0]
            set_append(d, 'SYSTEMD_SUBSTITUTIONS_%s' % file,
                'USER:%s' % user)
            if user not in d.getVar('USERADD_PARAM_%s' % pkg, True):
                set_append(
                    d,
                    'USERADD_PARAM_%s' % pkg,
                    '%s' % (' '.join(opts + [user])),
                    ';')
            if pkg not in d.getVar('USERADD_PACKAGES', True):
                set_append(d, 'USERADD_PACKAGES', pkg)


    def add_env_file(d, name, pkg):
        set_append(d, 'SRC_URI', 'file://%s' % name)
        set_append(d, 'FILES_%s' % pkg, '%s/%s' \
            % (d.getVar('envfiledir', True), name))
        set_append(d, '_INSTALL_ENV_FILES', name)


    pn = d.getVar('PN', True)
    if d.getVar('SYSTEMD_SERVICE_%s' % pn, True) is None:
        d.setVar('SYSTEMD_SERVICE_%s' % pn, '%s.service' % pn)

    for pkg in listvar_to_list(d, 'SYSTEMD_PACKAGES'):
        svc = listvar_to_list(d, 'SYSTEMD_SERVICE_%s' % pkg)
        svc = [SystemdUnit(x) for x in svc]
        tmpl = [x.template for x in svc if x.is_instance]
        tmpl = list(set(tmpl))
        tmpl = [SystemdUnit(x) for x in tmpl]
        svc = [x for x in svc if not x.is_instance]

        for unit in tmpl + svc:
            check_sd_unit(d, unit)
            add_sd_unit(d, unit, pkg)
            add_sd_user(d, unit.name, pkg)
        for name in listvar_to_list(d, 'SYSTEMD_ENVIRONMENT_FILE_%s' % pkg):
            add_env_file(d, name, pkg)
}


python systemd_do_postinst() {
    def make_subs(d):
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


    def install_envs(d):
        install_dir = d.getVar('D', True)
        install_dir += d.getVar('envfiledir', True)
        searchpaths = d.getVar('FILESPATH', True)

        for f in listvar_to_list(d, '_INSTALL_ENV_FILES'):
            src = bb.utils.which(searchpaths, f)
            if not os.path.isfile(src):
                bb.fatal('Did not find SYSTEMD_ENVIRONMENT_FILE:'
                    '\'%s\'' % src)

            dest = os.path.join(install_dir, f)
            parent = os.path.dirname(dest)
            if not os.path.exists(parent):
                os.makedirs(parent)

            with open(src, 'r') as fd:
                content = fd.read()
            with open(dest, 'w+') as fd:
                fd.write(content)


    install_envs(d)
    make_subs(d)
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
