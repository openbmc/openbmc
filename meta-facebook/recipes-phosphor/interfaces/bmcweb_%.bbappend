EXTRA_OEMESON:append = "\
    -Dexperimental-redfish-dbus-log-subscription=enabled \
    -Dhttp-body-limit=128 \
"

EXTRA_OEMESON:append:fb-compute-multihost = "\
    -Dexperimental-redfish-multi-computer-system=enabled \
"

PACKAGECONFIG:append = " \
    insecure-redfish-expand \
    redfish-dbus-log \
    redfish-dump-log \
    redfish-eventlog-managers \
"

PACKAGECONFIG:remove = " \
    redfish-oem-manager-fan-data \
"

MUTUAL_TLS_PARSING = "UserPrincipalName"
