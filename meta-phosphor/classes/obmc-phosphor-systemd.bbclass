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
# SYSTEMD_SUBSTITUTIONS = "var:val:file"
#    A specification for making python style {format} string
#    substitutions where:
#      var: the format string to search for
#      val: the value to replace with
#      file: the file in which to make the substitution
#
# SYSTEMD_GENSUBSTITUTIONS = "sub:val:file:listvars..."
#    A convenience variable for generating substitutions from one or more
#    lists where:
#      sub: the variable to substitue
#      val: the value to substitute with
#      file: the file to make the substitution in, relative
#         to ${systemd_system_unitdir}.
#      listvars: one or more list variable names.
#
#    the lists are zipped and simple substitutions (of the current tuple)
#    are supported in the sub, val and file fields using [%i] where i is the
#    tuple element to substitute.
#
# SYSTEMD_USER_${PN}.service = "foo"
# SYSTEMD_USER_${unit}.service = "foo"
#    The user for the unit/package.
#
# SYSTEMD_ENVIRONMENT_FILE_${PN} = "foo"
#    One or more environment files to be installed.
#
# SYSTEMD_LINK_${PN} = "tgt:name"
#    A specification for installing arbitrary links in
#    the ${systemd_system_unitdir} namespace, where:
#      tgt: the link target
#      name: the link name, relative to ${systemd_system_unitdir}
#
# SYSTEMD_GENLINKS_${PN} = "tgt:name:listvars..."
#    A convenience variable for generating links from one or more
#    lists where:
#      tgt: the link target
#      name: the link name, relative to ${systemd_system_unitdir}
#      listvars: one or more list variable names.
#
#    The lists are zipped and simple substitutions (of the current tuple)
#    are supported in the tgt and name fields using [%i] where i is the
#    tuple element to substitute.


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


def systemd_unit_info(unit):
    nfo = {}
    nfo['unit'] = unit
    nfo['cls'] = unit.split('.')[-1]
    nfo['base'] = unit.replace('dbus-', '').replace('.%s' % nfo['cls'], '')
    nfo['is_activated'] = unit.startswith('dbus-')
    nfo['is_template'] = '@.' in unit
    nfo['is_instance'] = '@' in unit and '@.' not in unit
    if nfo['is_template']:
        nfo['template'] = '.'.join(nfo['base'].split('@')[:-1])
        nfo['base'] = nfo['base'].rstrip('@')
    if nfo['is_instance']:
        nfo['instance'] = '.'.join(nfo['base'].split('@')[-1:])
        nfo['base'] = nfo['base'].rstrip('@%s' % nfo['instance'])
    return nfo


def systemd_parse_unit(d, path):
    import ConfigParser
    parser = ConfigParser.SafeConfigParser()
    parser.optionxform = str
    parser.read('%s' % path)
    return parser


python() {
    def check_sd_unit(d, nfo):
        searchpaths = d.getVar('FILESPATH', True)
        path = bb.utils.which(searchpaths, '%s' % nfo['unit'])
        if not os.path.isfile(path):
            bb.fatal('Did not find unit file "%s"' % nfo['unit'])

        parser = systemd_parse_unit(d, path)
        inhibit = listvar_to_list(d, 'INHIBIT_SYSTEMD_RESTART_POLICY_WARNING')
        if nfo['cls'] == 'service' and \
                not nfo['is_template'] and \
                nfo['unit'] not in inhibit and \
                not parser.has_option('Service', 'Restart'):
            bb.warn('Systemd unit \'%s\' does not '
                'have a restart policy defined.' % nfo['unit'])


    def add_default_subs(d, file):
        for x in [
                'base_bindir',
                'bindir',
                'sbindir',
                'envfiledir',
                'SYSTEMD_DEFAULT_TARGET' ]:
            set_append(d, 'SYSTEMD_SUBSTITUTIONS',
                '%s:%s:%s' % (x, d.getVar(x, True), file))


    def add_sd_unit(d, nfo, pkg):
        unit_dir = d.getVar('systemd_system_unitdir', True)
        set_append(d, 'SRC_URI', 'file://%s' % nfo['unit'])
        set_append(d, 'FILES_%s' % pkg, '%s/%s' % (unit_dir, nfo['unit']))
        set_append(d, '_INSTALL_SD_UNITS', nfo['unit'])
        add_default_subs(d, nfo['unit'])


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
            set_append(d, 'SYSTEMD_SUBSTITUTIONS',
                'USER:%s:%s' % (user, file))
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


    def install_link(d, spec, pkg):
        tgt, dest = spec.split(':')

        set_append(d, 'FILES_%s' % pkg, '%s/%s' \
            % (d.getVar('systemd_system_unitdir', True), dest))
        set_append(d, '_INSTALL_LINKS', spec)


    def gen_links(d, spec, pkg):
        spec = spec.split(':')
        tgtfmt, namefmt = spec[:2]
        listvars = spec[2:]
        lists = []
        targets = []
        names = []

        for var in listvars:
            lists.append(listvar_to_list(d, var))

        for tup in zip(*lists):
            tgt = tgtfmt
            name = namefmt
            for i in range(len(tup)):
                tgt = tgt.replace('[%s]' %i, tup[i])
                name = name.replace('[%s]' %i, tup[i])
            targets.append(tgt)
            names.append(name)

        links = ' '.join([':'.join(x) for x in
            zip(targets, names)])

        set_append(d, 'SYSTEMD_LINK_%s' % pkg, links)


    def gen_subs(d, spec):
        spec = spec.split(':')
        varfmt, valfmt, filefmt = spec[:3]
        listvars = spec[3:]
        lists = []
        vars = []
        vals = []
        files = []

        for var in listvars:
            lists.append(listvar_to_list(d, var))

        for tup in zip(*lists):
            var = varfmt
            val = valfmt
	    f = filefmt
            for i in range(len(tup)):
                var = var.replace('[%s]' %i, tup[i])
                val = val.replace('[%s]' %i, tup[i])
                f = f.replace('[%s]' %i, tup[i])
            vars.append(var)
            vals.append(val)
            files.append(f)

        subs = ' '.join([':'.join(x) for x in
            zip(vars, vals, files)])

        set_append(d, 'SYSTEMD_SUBSTITUTIONS', subs)


    pn = d.getVar('PN', True)
    if d.getVar('SYSTEMD_SERVICE_%s' % pn, True) is None:
        d.setVar('SYSTEMD_SERVICE_%s' % pn, '%s.service' % pn)

    for pkg in listvar_to_list(d, 'SYSTEMD_PACKAGES'):
        for spec in listvar_to_list(d, 'SYSTEMD_GENLINKS_%s' % pkg):
            gen_links(d, spec, pkg)
        for spec in listvar_to_list(d, 'SYSTEMD_GENSUBSTITUTIONS'):
            gen_subs(d, spec)
        for unit in listvar_to_list(d, 'SYSTEMD_SERVICE_%s' % pkg):
            nfo = systemd_unit_info(unit)
            check_sd_unit(d, nfo)
            add_sd_unit(d, nfo, pkg)
            add_sd_user(d, nfo['unit'], pkg)
        for name in listvar_to_list(d, 'SYSTEMD_ENVIRONMENT_FILE_%s' % pkg):
            add_env_file(d, name, pkg)
        for spec in listvar_to_list(d, 'SYSTEMD_LINK_%s' % pkg):
            install_link(d, spec, pkg)
        for spec in listvar_to_list(d, 'SYSTEMD_OVERRIDE_%s' % pkg):
            add_override(d, spec, pkg)
}


python systemd_do_postinst() {
    def make_subs(d):
        all_subs = {}
        for spec in listvar_to_list(d, 'SYSTEMD_SUBSTITUTIONS'):
            spec, file = spec.rsplit(':', 1)
            all_subs.setdefault(file, []).append(spec)

        for f, v in all_subs.iteritems():
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

            dest = '%s/%s' % (install_dir, f)
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
            dest = '%s/%s' % (install_dir, dest)
            parent = os.path.dirname(dest)
            if not os.path.exists(parent):
                os.makedirs(parent)
            os.symlink(tgt, dest)


    install_links(d)
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
