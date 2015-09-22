# Common code for applications providing a D-Bus service.

# Class users should define DBUS_SERVICES prior to including.

python() {
        services = d.getVar('DBUS_SERVICES', True)
        if services:
                uris = " ".join( [ 'file://' + s + '.conf' for s in services.split() ] )
                d.appendVar('SRC_URI', ' ' + uris + ' ')
}

do_install_append() {
        # install the service configuration files
        install -d ${D}${sysconfdir}/dbus-1/system.d
        for s in ${DBUS_SERVICES}; do
                install ${WORKDIR}/$s.conf ${D}${sysconfdir}/dbus-1/system.d/$s.conf
        done
}
