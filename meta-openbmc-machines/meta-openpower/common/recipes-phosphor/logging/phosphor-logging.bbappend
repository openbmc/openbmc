DEPENDS_append = " ${@cf_enabled(d, 'obmc-openpower', '\
        openpower-debug-collector-native \
        openpower-dbus-interfaces-native \
        openpower-occ-control-native \
        ')}"
