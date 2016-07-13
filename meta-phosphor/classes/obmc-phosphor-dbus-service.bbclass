# Utilities and shortcuts for recipes providing D-Bus services.
# Variables:
#  DBUS_PACKAGES ?= "${PN}"
#    The list of packages to which files should be added.
#
#  DBUS_SERVICE_${PN} += "org.openbmc.Foo"
#    A list of dbus service names.  The class will look for a dbus
#    configuration file with the same name with .conf appended.
#
inherit dbus-dir
inherit obmc-phosphor-utils

RDEPENDS_${PN} += "dbus-perms"
DBUS_PACKAGES ?= "${PN}"

_INSTALL_DBUS_CONFIGS=""


python() {
    searchpaths = d.getVar('FILESPATH', True)

    def add_dbus_config(d, service, pkg):
        set_append(d, 'SRC_URI', 'file://%s.conf' % service)
        set_append(d, 'FILES_%s' % pkg, '%s%s.conf' \
            % (d.getVar('dbus_system_confdir', True), service))
        set_append(d, '_INSTALL_DBUS_CONFIGS', '%s.conf' % service)


    for pkg in listvar_to_list(d, 'DBUS_PACKAGES'):
        services = listvar_to_list(d, 'DBUS_SERVICE_%s' % pkg)

        for service in services:
            add_dbus_config(d, service, pkg)
}


do_install_append() {
        # install the dbus configuration files
        [ -z "${_INSTALL_DBUS_CONFIGS}" ] || \
                install -d ${D}${dbus_system_confdir}
        for c in ${_INSTALL_DBUS_CONFIGS}; do
                install -m 0644 ${WORKDIR}/$c \
                        ${D}${dbus_system_confdir}$c
        done
}
