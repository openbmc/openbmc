# Utilities and shortcuts for recipes providing D-Bus services.
# Variables:
#  DBUS_PACKAGES ?= "${PN}"
#    The list of packages to which files should be added.
#
#  DBUS_SERVICE:${PN} += "org.openbmc.Foo.service"
#    A list of dbus service names.  The class will look for a
#    dbus configuration file with the same base name with .conf
#    appended.  If one is found, it is added to the package
#    and used verbatim.  If it is not found, a default one
#    (with very open permissions) is generated and used.
#
#    Additionally the class will instantiate obmc-phosphor-systemd
#    with any SYSTEMD_SERVICE:%s variables translated appropriately.
#
#    If a service begins with 'dbus-' DBus activation will be
#    configured.  The class will look for an activation file
#    with the 'dbus-' prefix removed.  If found, it is added to
#    the package and used verbatim.  If it is not found, a default
#    one is generated and used.


inherit dbus-dir
inherit obmc-phosphor-utils

RDEPENDS:${PN}:append:class-target = " dbus-perms"
DBUS_PACKAGES ?= "${PN}"

_INSTALL_DBUS_CONFIGS=""
_DEFAULT_DBUS_CONFIGS=""
_INSTALL_DBUS_ACTIVATIONS=""
_DEFAULT_DBUS_ACTIVATIONS=""


python dbus_do_postinst() {
    def make_default_dbus_config(d, unit, user):
        bus = unit.base
        if unit.is_template:
            bus = '%s*' % bus

        path = d.getVar('D', True)
        path += d.getVar('dbus_system_confdir', True)
        with open('%s/%s.conf' % (path, unit.base), 'w+') as fd:
            fd.write('<!DOCTYPE busconfig PUBLIC "-//freedesktop//DTD D-BUS Bus Configuration 1.0//EN"\n')
            fd.write('        "http://www.freedesktop.org/standards/dbus/1.0/busconfig.dtd">\n')
            fd.write('<busconfig>\n')
            fd.write('        <policy user="%s">\n' % user)
            fd.write('                <allow own="%s"/>\n' % bus)
            fd.write('                <allow send_destination="%s"/>\n' % bus)
            fd.write('        </policy>\n')
            fd.write('</busconfig>\n')
            fd.close()


    def make_default_dbus_activation(d, unit, user):
        dest = unit.base
        sd_unit = unit.name
        if unit.is_instance:
            dest = '%s.%s' % (unit.base, unit.instance)
            sd_unit = '%s@%s' % (unit.base, unit.instance)

        path = d.getVar('D', True)
        path += d.getVar('dbus_system_servicesdir', True)
        with open('%s/%s.service' % (path, dest), 'w+') as fd:
            fd.write('[D-BUS Service]\n')
            fd.write('Name=%s\n' % dest)
            fd.write('Exec=/bin/false\n')
            fd.write('User=%s\n' % user)
            fd.write('SystemdService=dbus-%s.service\n' % sd_unit)
            fd.close()


    for service_user in listvar_to_list(d, '_DEFAULT_DBUS_CONFIGS'):
        service, user = service_user.split(':')
        make_default_dbus_config(d, SystemdUnit(service), user)
    for service_user in listvar_to_list(d, '_DEFAULT_DBUS_ACTIVATIONS'):
        service, user = service_user.split(':')
        make_default_dbus_activation(d, SystemdUnit(service), user)
}


python() {
    searchpaths = d.getVar('FILESPATH', True)

    def get_user(d, service, pkg):
        user = d.getVar(
            'SYSTEMD_USER_%s' % service, True)
        if user is None:
            user = d.getVar(
                'SYSTEMD_USER_%s' % pkg, True) or 'root'
        return user


    def add_dbus_config(d, unit, pkg):
        path = bb.utils.which(searchpaths, '%s.conf' % unit.base)
        if not os.path.isfile(path):
            user = get_user(d, unit.name, pkg)
            set_doappend(d, '_DEFAULT_DBUS_CONFIGS', '%s:%s' % (
                unit.name, user))
        else:
            set_doappend(d, 'SRC_URI', 'file://%s.conf' % unit.base)
            set_doappend(d, '_INSTALL_DBUS_CONFIGS', '%s.conf' % unit.base)
        set_doappend(d, 'FILES:%s' % pkg, '%s%s.conf' \
            % (d.getVar('dbus_system_confdir', True), unit.base))


    def add_dbus_activation(d, unit, pkg):
        if not unit.is_activated or unit.is_template:
            return
        search_match = '%s.service' % unit.base
        if unit.is_instance:
            search_match = '%s.%s.service' % (unit.base, unit.instance)

        path = bb.utils.which(searchpaths, search_match)

        if not os.path.isfile(path):
            user = get_user(d, unit.base, pkg)
            set_doappend(d, '_DEFAULT_DBUS_ACTIVATIONS', '%s:%s' % (
                unit.name, user))
        else:
            set_doappend(d, 'SRC_URI', 'file://%s' % search_match)
            set_doappend(d, '_INSTALL_DBUS_ACTIVATIONS', search_match)
        set_doappend(d, 'FILES:%s' % pkg, '%s%s' \
            % (d.getVar('dbus_system_servicesdir', True), search_match))


    if d.getVar('CLASSOVERRIDE', True) != 'class-target':
        return

    d.appendVarFlag('do_install', 'postfuncs', ' dbus_do_postinst')

    for pkg in listvar_to_list(d, 'DBUS_PACKAGES'):
        if pkg not in (d.getVar('SYSTEMD_PACKAGES', True) or ''):
            set_doappend(d, 'SYSTEMD_PACKAGES', pkg)

        svc = listvar_to_list(d, 'DBUS_SERVICE:%s' % pkg)
        svc = [SystemdUnit(x) for x in svc]
        inst = [x for x in svc if x.is_instance]
        tmpl = [x.template for x in svc if x.is_instance]
        tmpl = list(set(tmpl))
        tmpl = [SystemdUnit(x) for x in tmpl]
        svc = [x for x in svc if not x.is_instance]

        for unit in inst:
            set_doappend(
                d, 'SYSTEMD_SERVICE:%s' % pkg, unit.name)

        for unit in tmpl + svc:
            add_dbus_config(d, unit, pkg)
            add_dbus_activation(d, unit, pkg)
            set_doappend(
                d, 'SYSTEMD_SERVICE:%s' % pkg, unit.name)
            set_doappend(d, 'SYSTEMD_SUBSTITUTIONS',
                'BUSNAME:%s:%s' % (unit.base, unit.name))
}


do_install:append() {
        # install the dbus configuration files
        [ -z "${_INSTALL_DBUS_CONFIGS}" ] && \
                [ -z "${_DEFAULT_DBUS_CONFIGS}" ] || \
                install -d ${D}${dbus_system_confdir}
        for c in ${_INSTALL_DBUS_CONFIGS}; do
                install -m 0644 ${UNPACKDIR}/$c \
                        ${D}${dbus_system_confdir}$c
        done
        # install the dbus activation files
        [ -z "${_INSTALL_DBUS_ACTIVATIONS}" ] && \
                [ -z "${_DEFAULT_DBUS_ACTIVATIONS}" ] || \
                install -d ${D}${dbus_system_servicesdir}
        for s in ${_INSTALL_DBUS_ACTIVATIONS}; do
                install -m 0644 ${UNPACKDIR}/$s\
                        ${D}${dbus_system_servicesdir}$s
        done
}

inherit obmc-phosphor-systemd
