EXTRA_OEMESON:append = " \
    -Dinsecure-enable-redfish-query=enabled \
"

PACKAGECONFIG:append = " \
    redfish-dbus-log \
    redfish-host-logger \
"
