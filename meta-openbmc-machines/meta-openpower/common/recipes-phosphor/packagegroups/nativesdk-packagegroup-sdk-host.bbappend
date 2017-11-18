RDEPENDS_${PN}_append = " ${@cf_enabled(d, 'obmc-openpower', '\
        nativesdk-openpower-dbus-interfaces-yaml \
        nativesdk-openpower-debug-collector-yaml \
        ')}"
