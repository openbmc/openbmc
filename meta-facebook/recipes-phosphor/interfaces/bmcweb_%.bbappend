EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
    -Dredfish-new-powersubsystem-thermalsubsystem=enabled \
"

PACKAGECONFIG:append = " insecure-redfish-expand"
