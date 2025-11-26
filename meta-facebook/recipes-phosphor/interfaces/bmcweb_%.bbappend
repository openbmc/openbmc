EXTRA_OEMESON:append = "\
    -Dexperimental-redfish-dbus-log-subscription=enabled \
"

EXTRA_OEMESON:append:fb-compute-multihost = "\
    -Dexperimental-redfish-multi-computer-system=enabled \
"

PACKAGECONFIG:append = " \
    insecure-redfish-expand \
    redfish-dbus-log \
    redfish-dump-log \
"

PACKAGECONFIG:remove = " \
    redfish-oem-manager-fan-data \
"

MUTUAL_TLS_PARSING = "UserPrincipalName"
