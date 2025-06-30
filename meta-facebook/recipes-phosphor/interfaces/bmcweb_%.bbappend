EXTRA_OEMESON:append = "\
    -Dexperimental-redfish-dbus-log-subscription=enabled \
"

PACKAGECONFIG:append = " \
    insecure-redfish-expand \
    redfish-dbus-log \
    redfish-dump-log \
"

MUTUAL_TLS_PARSING = "UserPrincipalName"
