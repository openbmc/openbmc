# Utilities and shortcuts for recipes providing D-Bus services.
# Variables:
#  DBUS_PACKAGES ?= "${PN}"
#    The list of packages to which files should be added.
#
#  DBUS_SERVICE_${PN} += "org.openbmc.Foo.service"
#    A list of dbus service names.  The class will look for a
#    dbus configuration file with the same base name with .conf
#    appended.  If one is found, it is added to the package
#    and used verbatim.  If it is not found, a default one
#    (with very open permissions) is generated and used.
#
#    Additionally the class will instantiate obmc-phosphor-systemd
#    with any SYSTEMD_SERVICE_%s variables translated appropriately.
#
#    If a service begins with 'dbus-' DBus activation will be
#    configured.  The class will look for an activation file
#    with the 'dbus-' prefix removed.  If found, it is added to
#    the package and used verbatim.  If it is not found, a default
#    one is generated and used.


inherit dbus-dir
inherit obmc-phosphor-utils

RDEPENDS_${PN}_append_class-target = " dbus-perms"
DBUS_PACKAGES ?= "${PN}"

_INSTALL_DBUS_CONFIGS=""
_DEFAULT_DBUS_CONFIGS=""
_INSTALL_DBUS_ACTIVATIONS=""
_DEFAULT_DBUS_ACTIVATIONS=""


python dbus_do_postinst() {
    def make_default_dbus_config(d, nfo, user):
        bus = nfo['base']
        if nfo['is_template']:
            bus = '%s*' % bus

        path = d.getVar('D', True)
        path += d.getVar('dbus_system_confdir', True)
        with open('%s/%s.conf' % (path, nfo['base']), 'w+') as fd:
            fd.write('<!DOCTYPE busconfig PUBLIC "-//freedesktop//DTD D-BUS Bus Configuration 1.0//EN"\n')
            fd.write('        "http://www.freedesktop.org/standards/dbus/1.0/busconfig.dtd">\n')
            fd.write('<busconfig>\n')
            fd.write('        <policy user="%s">\n' % user)
            fd.write('                <allow own="%s"/>\n' % bus)
            fd.write('                <allow send_destination="%s"/>\n' % bus)
            fd.write('        </policy>\n')
            fd.write('</busconfig>\n')
            fd.close()


    def make_default_dbus_activation(d, nfo, user):
        dest = nfo['base']
        sd_unit = nfo['base']
        if nfo['is_instance']:
            dest = '%s.%s' % (nfo['base'], nfo['instance'])
            sd_unit = '%s@%s' % (nfo['base'], nfo['instance'])

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
        nfo = systemd_unit_info(service)
        make_default_dbus_config(d, nfo, user)
    for service_user in listvar_to_list(d, '_DEFAULT_DBUS_ACTIVATIONS'):
        service, user = service_user.split(':')
        nfo = systemd_unit_info(service)
        make_default_dbus_activation(d, nfo, user)
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


    def add_dbus_config(d, nfo, pkg):
        path = bb.utils.which(searchpaths, '%s.conf' % nfo['base'])
        if not os.path.isfile(path):
            user = get_user(d, service, pkg)
            set_append(d, '_DEFAULT_DBUS_CONFIGS', '%s:%s' % (
                nfo['unit'], user))
        else:
            set_append(d, 'SRC_URI', 'file://%s.conf' % nfo['base'])
            set_append(d, '_INSTALL_DBUS_CONFIGS', '%s.conf' % nfo['base'])
        set_append(d, 'FILES_%s' % pkg, '%s%s.conf' \
            % (d.getVar('dbus_system_confdir', True), nfo['base']))


    def add_sd_unit(d, nfo, pkg):
        set_append(
            d, 'SYSTEMD_SERVICE_%s' % pkg, nfo['unit'])
        set_append(d, 'SYSTEMD_SUBSTITUTIONS',
            'BUSNAME:%s:%s' % (nfo['base'], nfo['unit']))


    def add_dbus_activation(d, nfo, pkg):
        if not nfo['is_activated'] or nfo['is_template']:
            return
        search_match = '%s.service' % nfo['base']
        if nfo['is_instance']:
            search_match = '%s.%s.service' % (nfo['base'], nfo['instance'])

        path = bb.utils.which(searchpaths, search_match)

        if not os.path.isfile(path):
            user = get_user(d, nfo['base'], pkg)
            set_append(d, '_DEFAULT_DBUS_ACTIVATIONS', '%s:%s' % (
                nfo['unit'], user))
        else:
            set_append(d, 'SRC_URI', 'file://%s' % search_match)
            set_append(d, '_INSTALL_DBUS_ACTIVATIONS', search_match)
        set_append(d, 'FILES_%s' % pkg, '%s%s' \
            % (d.getVar('dbus_system_servicesdir', True), search_match))


    for pkg in listvar_to_list(d, 'DBUS_PACKAGES'):
        if pkg not in (d.getVar('SYSTEMD_PACKAGES', True) or ''):
            set_append(d, 'SYSTEMD_PACKAGES', pkg)

        for service in listvar_to_list(d, 'DBUS_SERVICE_%s' % pkg):
            nfo = systemd_unit_info(service)

            add_dbus_config(d, nfo, pkg)
            add_sd_unit(d, nfo, pkg)
            add_dbus_activation(d, nfo, pkg)
}


do_install_append() {
        # install the dbus configuration files
        [ -z "${_INSTALL_DBUS_CONFIGS}" ] && \
                [ -z "${_DEFAULT_DBUS_CONFIGS}" ] || \
                install -d ${D}${dbus_system_confdir}
        for c in ${_INSTALL_DBUS_CONFIGS}; do
                install -m 0644 ${WORKDIR}/$c \
                        ${D}${dbus_system_confdir}$c
        done
        # install the dbus activation files
        [ -z "${_INSTALL_DBUS_ACTIVATIONS}" ] && \
                [ -z "${_DEFAULT_DBUS_ACTIVATIONS}" ] || \
                install -d ${D}${dbus_system_servicesdir}
        for s in ${_INSTALL_DBUS_ACTIVATIONS}; do
                install -m 0644 ${WORKDIR}/$s\
                        ${D}${dbus_system_servicesdir}$s
        done
}

do_install[postfuncs] += "dbus_do_postinst"

inherit obmc-phosphor-systemd
