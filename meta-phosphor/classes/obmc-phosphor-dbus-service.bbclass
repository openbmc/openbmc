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

inherit dbus-dir
inherit obmc-phosphor-utils

RDEPENDS_${PN} += "dbus-perms"
DBUS_PACKAGES ?= "${PN}"

_INSTALL_DBUS_CONFIGS=""
_DEFAULT_DBUS_CONFIGS=""


python dbus_do_postinst() {
    def make_default_dbus_config(d, service):
        path = d.getVar('D', True)
        path += d.getVar('dbus_system_confdir', True)
        with open('%s/%s.conf' % (path, service), 'w+') as fd:
            fd.write('<!DOCTYPE busconfig PUBLIC "-//freedesktop//DTD D-BUS Bus Configuration 1.0//EN"\n')
            fd.write('        "http://www.freedesktop.org/standards/dbus/1.0/busconfig.dtd">\n')
            fd.write('<busconfig>\n')
            fd.write('        <policy user="root">\n')
            fd.write('                <allow own="%s"/>\n' % service)
            fd.write('                <allow send_destination="%s"/>\n' % service)
            fd.write('        </policy>\n')
            fd.write('</busconfig>\n')
            fd.close()


    for service in listvar_to_list(d, '_DEFAULT_DBUS_CONFIGS'):
        make_default_dbus_config(d, service)
}


python() {
    searchpaths = d.getVar('FILESPATH', True)

    def add_dbus_config(d, service, pkg):
        path = bb.utils.which(searchpaths, '%s.conf' % service)
        if not os.path.isfile(path):
            set_append(d, '_DEFAULT_DBUS_CONFIGS', service)
        else:
            set_append(d, 'SRC_URI', 'file://%s.conf' % service)
            set_append(d, '_INSTALL_DBUS_CONFIGS', '%s.conf' % service)
        set_append(d, 'FILES_%s' % pkg, '%s%s.conf' \
            % (d.getVar('dbus_system_confdir', True), service))


    for pkg in listvar_to_list(d, 'DBUS_PACKAGES'):
        services = listvar_to_list(d, 'DBUS_SERVICE_%s' % pkg)

        for service in services:
            add_dbus_config(d, service, pkg)
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
}

do_install[postfuncs] += "dbus_do_postinst"
