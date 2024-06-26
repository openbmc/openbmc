# Common code for systemd based services.
#
# Prior to inheriting this class, recipes can define services like this:
#
# SYSTEMD_SERVICE:${PN} = "foo.service bar.socket baz@.service"
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
# SYSTEMD_SUBSTITUTIONS = "var:val:file"
#    A specification for making python style {format} string
#    substitutions where:
#      var: the format string to search for
#      val: the value to replace with
#      file: the file in which to make the substitution
#
# SYSTEMD_USER_${PN}.service = "foo"
# SYSTEMD_USER_${unit}.service = "foo"
#    The user for the unit/package.
#
# SYSTEMD_ENVIRONMENT_FILE:${PN} = "foo"
#    One or more environment files to be installed.
#
# SYSTEMD_LINK:${PN} = "tgt:name"
#    A specification for installing arbitrary links in
#    the ${systemd_system_unitdir} namespace, where:
#      tgt: the link target
#      name: the link name, relative to ${systemd_system_unitdir}
#
# SYSTEMD_OVERRIDE:${PN} = "src:dest"
#    A specification for installing unit overrides where:
#      src: the override file template
#      dest: the override install location, relative to ${systemd_system_unitdir}
#
#    Typically SYSTEMD_SUBSTITUTIONS is used to deploy a range
#    of overrides from a single template file.  To simply install
#    a single override use "foo.conf:my-service.d/foo.conf"


inherit obmc-phosphor-utils
inherit systemd
inherit useradd

_INSTALL_SD_UNITS=""
SYSTEMD_DEFAULT_TARGET ?= "multi-user.target"
envfiledir ?= "${sysconfdir}/default"

# Big ugly hack to prevent useradd.bbclass post-parse sanity checker failure.
# If there are users to be added, we'll add them in our post-parse.
# If not...there don't seem to be any ill effects...
USERADD_PACKAGES ?= " "
USERADD_PARAM:${PN} ?= ";"


def SystemdUnit(unit):
    class Unit(object):
        def __init__(self, unit):
            self.unit = unit

        def __getattr__(self, item):
            if item == 'name':
                return self.unit
            if item == 'is_activated':
                return self.unit.startswith('dbus-')
            if item == 'is_template':
                return '@.' in self.unit
            if item == 'is_instance':
                return '@' in self.unit and not self.is_template
            if item in ['is_service', 'is_target']:
                return self.unit.split('.')[-1] == item
            if item == 'base':
                cls = self.unit.split('.')[-1]
                base = self.unit.replace('dbus-', '')
                base = base.replace('.%s' % cls, '')
                if self.is_instance:
                    base = base.replace('@%s' % self.instance, '')
                if self.is_template:
                    base = base.rstrip('@')
                return base
            if item == 'instance' and self.is_instance:
                inst = self.unit.rsplit('@')[-1]
                return inst.rsplit('.')[0]
            if item == 'template' and self.is_instance:
                cls = self.unit.split('.')[-1]
                return '%s@.%s' % (self.base, cls)
            if item == 'template' and self.is_template:
                return '.'.join(self.base.split('@')[:-1])

            raise AttributeError(item)
    return Unit(unit)


def systemd_parse_unit(d, path):
    import configparser
    parser = configparser.ConfigParser(strict=False)
    parser.optionxform = str
    parser.read('%s' % path)
    return parser


python() {
    def check_sd_unit(d, unit):
        searchpaths = d.getVar('FILESPATH', True)
        path = bb.utils.which(searchpaths, '%s' % unit.name)
        if not os.path.isfile(path):
            # Unit does not exist in tree. Allow it to install from repo.
            # Return False here to indicate it does not exist.
            return False

        parser = systemd_parse_unit(d, path)
        inhibit = listvar_to_list(d, 'INHIBIT_SYSTEMD_RESTART_POLICY_WARNING')
        if unit.is_service and \
                not unit.is_template and \
                unit.name not in inhibit and \
                not parser.has_option('Service', 'Restart'):
            bb.warn('Systemd unit \'%s\' does not '
                'have a restart policy defined.' % unit.name)
        return True


    def add_default_subs(d, file):
        for x in [
                'base_bindir',
                'bindir',
                'sbindir',
                'libexecdir',
                'envfiledir',
                'sysconfdir',
                'localstatedir',
                'datadir',
                'SYSTEMD_DEFAULT_TARGET' ]:
            set_doappend(d, 'SYSTEMD_SUBSTITUTIONS',
                '%s:%s:%s' % (x, d.getVar(x, True), file))


    def add_sd_unit(d, unit, pkg, unit_exist):
        # Do not add unit if it does not exist in tree.
        # It will be installed from repo.
        if not unit_exist:
            return

        name = unit.name
        unit_dir = d.getVar('systemd_system_unitdir', True)
        set_doappend(d, 'SRC_URI', 'file://%s' % name)
        set_doappend(d, 'FILES:%s' % pkg, '%s/%s' % (unit_dir, name))
        set_doappend(d, '_INSTALL_SD_UNITS', name)
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
        if len(user) == 0:
            var = 'SYSTEMD_USER_%s' % pkg
            user = listvar_to_list(d, var)
        if len(user) != 0:
            if len(user) != 1:
                bb.fatal('Too many users assigned to %s: \'%s\'' % (var, ' '.join(user)))

            user = user[0]
            set_doappend(d, 'SYSTEMD_SUBSTITUTIONS',
                'USER:%s:%s' % (user, file))
            if user not in d.getVar('USERADD_PARAM:%s' % pkg, True):
                set_doappend(
                    d,
                    'USERADD_PARAM:%s' % pkg,
                    '%s' % (' '.join(opts + [user])),
                    ';')
            if pkg not in d.getVar('USERADD_PACKAGES', True):
                set_doappend(d, 'USERADD_PACKAGES', pkg)


    def add_env_file(d, name, pkg):
        set_doappend(d, 'SRC_URI', 'file://%s' % name)
        set_doappend(d, 'FILES:%s' % pkg, '%s/%s' \
            % (d.getVar('envfiledir', True), name))
        set_doappend(d, '_INSTALL_ENV_FILES', name)


    def install_link(d, spec, pkg):
        tgt, dest = spec.split(':')

        set_doappend(d, 'FILES:%s' % pkg, '%s/%s' \
            % (d.getVar('systemd_system_unitdir', True), dest))
        set_doappend(d, '_INSTALL_LINKS', spec)


    def add_override(d, spec, pkg):
        tmpl, dest = spec.split(':')
        set_doappend(d, '_INSTALL_OVERRIDES', '%s' % spec)
        unit_dir = d.getVar('systemd_system_unitdir', True)
        set_doappend(d, 'FILES:%s' % pkg, '%s/%s' % (unit_dir, dest))
        add_default_subs(d, '%s' % dest)
        add_sd_user(d, '%s' % dest, pkg)


    if d.getVar('CLASSOVERRIDE', True) != 'class-target':
        return

    d.appendVarFlag('do_install', 'postfuncs', ' systemd_do_postinst')

    pn = d.getVar('PN', True)
    if d.getVar('SYSTEMD_SERVICE:%s' % pn, True) is None:
        d.setVar('SYSTEMD_SERVICE:%s' % pn, '%s.service' % pn)

    for pkg in listvar_to_list(d, 'SYSTEMD_PACKAGES'):
        svc = listvar_to_list(d, 'SYSTEMD_SERVICE:%s' % pkg)
        svc = [SystemdUnit(x) for x in svc]
        tmpl = [x.template for x in svc if x.is_instance]
        tmpl = list(set(tmpl))
        tmpl = [SystemdUnit(x) for x in tmpl]
        svc = [x for x in svc if not x.is_instance]

        for unit in tmpl + svc:
            unit_exist = check_sd_unit(d, unit)
            add_sd_unit(d, unit, pkg, unit_exist)
            add_sd_user(d, unit.name, pkg)
        for name in listvar_to_list(d, 'SYSTEMD_ENVIRONMENT_FILE:%s' % pkg):
            add_env_file(d, name, pkg)
        for spec in listvar_to_list(d, 'SYSTEMD_LINK:%s' % pkg):
            install_link(d, spec, pkg)
        for spec in listvar_to_list(d, 'SYSTEMD_OVERRIDE:%s' % pkg):
            add_override(d, spec, pkg)
}


python systemd_do_postinst() {
    def make_subs(d):
        all_subs = {}
        for spec in listvar_to_list(d, 'SYSTEMD_SUBSTITUTIONS'):
            spec, file = spec.rsplit(':', 1)
            all_subs.setdefault(file, []).append(spec)

        for f, v in all_subs.items():
            subs = dict([ x.split(':') for x in v])
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


    def install_links(d):
        install_dir = d.getVar('D', True)
        install_dir += d.getVar('systemd_system_unitdir', True)

        for spec in listvar_to_list(d, '_INSTALL_LINKS'):
            tgt, dest = spec.split(':')
            dest = os.path.join(install_dir, dest)
            parent = os.path.dirname(dest)
            if not os.path.exists(parent):
                os.makedirs(parent)
            os.symlink(tgt, dest)


    def install_overrides(d):
        install_dir = d.getVar('D', True)
        install_dir += d.getVar('systemd_system_unitdir', True)
        searchpaths = d.getVar('FILESPATH', True)

        for spec in listvar_to_list(d, '_INSTALL_OVERRIDES'):
            tmpl, dest = spec.split(':')
            source = bb.utils.which(searchpaths, tmpl)
            if not os.path.isfile(source):
                bb.fatal('Did not find SYSTEMD_OVERRIDE '
                    'template: \'%s\'' % source)

            dest = os.path.join(install_dir, dest)
            parent = os.path.dirname(dest)
            if not os.path.exists(parent):
                os.makedirs(parent)

            with open(source, 'r') as fd:
                content = fd.read()
            with open('%s' % dest, 'w+') as fd:
                fd.write(content)


    install_links(d)
    install_envs(d)
    install_overrides(d)
    make_subs(d)
}


do_install:append() {
        # install systemd service/socket/template files
        [ -z "${_INSTALL_SD_UNITS}" ] || \
                install -d ${D}${systemd_system_unitdir}
        for s in ${_INSTALL_SD_UNITS}; do
                install -m 0644 ${UNPACKDIR}/$s \
                        ${D}${systemd_system_unitdir}/$s
                sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
                        -e 's,@BINDIR@,${bindir},g' \
                        -e 's,@SBINDIR@,${sbindir},g' \
                        -e 's,@LIBEXECDIR@,${libexecdir},g' \
                        -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
                        -e 's,@DATADIR@,${datadir},g' \
                        ${D}${systemd_system_unitdir}/$s
        done
}
