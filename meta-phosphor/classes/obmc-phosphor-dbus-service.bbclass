# Utilities and shortcuts for recipes providing D-Bus services.
# Variables:
#  DBUS_PACKAGES ?= "${PN}"
#    The list of packages to which files should be added.
#
#  DBUS_SERVICE_${PN} += "org.openbmc.Foo"
#    A list of dbus service names.  The class will look for a
#    dbus configuration file with the same name with .conf
#    appended.  If one is found, it is added to the package
#    and used verbatim.  If it is not found, a default one
#    (with very open permissions) is generated and used.
#
#    Additionally the class will instantiate obmc-phosphor-systemd
#    with any SYSTEMD_SERVICE_%s variables translated appropriately.
#
#  DBUS_ACTIVATED_SERVICE_${PN} += "org.openbmc.Foo"
#    A list of services that should have dbus activation configured.
#    Services that appear here need not be in DBUS_SERVICE_%s.
#    If used, the search pattern for the systemd unit file is
#    changed to be dbus-%s.service.  The class will look for a
#    dbus activation file with the same name with .service appended.
#    If one is found, it added to the package and used verbatim.
#    If it is not found, a default one is generated and used.
#
#  DBUS_USER_${PN} = "dbususer"
#  DBUS_USER_${unit} = "dbususer"
#    The user a service/pkg should be configured to run as.


inherit dbus-dir
inherit obmc-phosphor-utils

RDEPENDS_${PN}_append_class-target = " dbus-perms"
DBUS_PACKAGES ?= "${PN}"

_INSTALL_DBUS_CONFIGS=""
_DEFAULT_DBUS_CONFIGS=""
_INSTALL_DBUS_ACTIVATIONS=""
_DEFAULT_DBUS_ACTIVATIONS=""


python dbus_do_postinst() {
    def make_default_dbus_config(d, service, user):
        path = d.getVar('D', True)
        path += d.getVar('dbus_system_confdir', True)
        with open('%s/%s.conf' % (path, service), 'w+') as fd:
            fd.write('<!DOCTYPE busconfig PUBLIC "-//freedesktop//DTD D-BUS Bus Configuration 1.0//EN"\n')
            fd.write('        "http://www.freedesktop.org/standards/dbus/1.0/busconfig.dtd">\n')
            fd.write('<busconfig>\n')
            fd.write('        <policy user="%s">\n' % user)
            fd.write('                <allow own="%s"/>\n' % service)
            fd.write('                <allow send_destination="%s"/>\n' % service)
            fd.write('        </policy>\n')
            fd.write('</busconfig>\n')
            fd.close()


    def make_default_dbus_activation(d, service, user):
        path = d.getVar('D', True)
        path += d.getVar('dbus_system_servicesdir', True)
        with open('%s/%s.service' % (path, service), 'w+') as fd:
            fd.write('[D-BUS Service]\n')
            fd.write('Name=%s\n' % service)
            fd.write('Exec=/bin/false\n')
            fd.write('User=%s\n' % user)
            fd.write('SystemdService=dbus-%s.service\n' % service)
            fd.close()


    for service_user in listvar_to_list(d, '_DEFAULT_DBUS_CONFIGS'):
        make_default_dbus_config(d, *service_user.split(':'))
    for service_user in listvar_to_list(d, '_DEFAULT_DBUS_ACTIVATIONS'):
        make_default_dbus_activation(d, *service_user.split(':'))
}


python() {
    searchpaths = d.getVar('FILESPATH', True)

    def get_user(d, service, pkg):
        user = d.getVar(
            'DBUS_USER_%s' % service, True)
        if user is None:
            user = d.getVar(
                'DBUS_USER_%s' % pkg, True) or 'root'
        return user


    def add_dbus_config(d, service, pkg):
        path = bb.utils.which(searchpaths, '%s.conf' % service)
        if not os.path.isfile(path):
            user = get_user(d, service, pkg)
            set_append(d, '_DEFAULT_DBUS_CONFIGS', '%s:%s' % (
                service, user))
        else:
            set_append(d, 'SRC_URI', 'file://%s.conf' % service)
            set_append(d, '_INSTALL_DBUS_CONFIGS', '%s.conf' % service)
        set_append(d, 'FILES_%s' % pkg, '%s%s.conf' \
            % (d.getVar('dbus_system_confdir', True), service))


    def add_sd_unit(d, prefix, service, pkg):
        set_append(
            d, 'SYSTEMD_SERVICE_%s' % pkg, '%s%s.service' % (
                prefix, service))
        set_append(d, 'SYSTEMD_SUBSTITUTIONS_%s%s.service' % (prefix, service),
            'BUSNAME:%s' % service)


    def add_sd_user(d, prefix, service, pkg):
        var = None
        user = d.getVar(
            'DBUS_USER_%s' % service, True)
        if user:
            var = 'SYSTEMD_USER_%s%s.service' % (prefix, service)
        else:
            user = d.getVar(
                'DBUS_USER_%s' % pkg, True)
            if user:
                var = 'SYSTEMD_USER_%s' % pkg

        if var and user not in listvar_to_list(d, var):
            set_append(d, var, user)


    def add_dbus_activation(d, service, pkg):
        path = bb.utils.which(searchpaths, '%s.service' % service)
        if not os.path.isfile(path):
            user = get_user(d, service, pkg)
            set_append(d, '_DEFAULT_DBUS_ACTIVATIONS', '%s:%s' % (
                service, user))
        else:
            set_append(d, 'SRC_URI', 'file://%s.service' % service)
            set_append(d, '_INSTALL_DBUS_ACTIVATIONS', '%s.service' % service)
        set_append(d, 'FILES_%s' % pkg, '%s%s.service' \
            % (d.getVar('dbus_system_servicesdir', True), service))


    for pkg in listvar_to_list(d, 'DBUS_PACKAGES'):
        if pkg not in (d.getVar('SYSTEMD_PACKAGES', True) or ''):
            set_append(d, 'SYSTEMD_PACKAGES', pkg)

        services = listvar_to_list(d, 'DBUS_SERVICE_%s' % pkg)
        auto = listvar_to_list(d, 'DBUS_ACTIVATED_SERVICE_%s' % pkg)

        for service in set(services).union(auto):
            prefix = 'dbus-' if service in auto else ''
            add_dbus_config(d, service, pkg)
            add_sd_unit(d, prefix, service, pkg)
            add_sd_user(d, prefix, service, pkg)
            if prefix:
                add_dbus_activation(d, service, pkg)
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
