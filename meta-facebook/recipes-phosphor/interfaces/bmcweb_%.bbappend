EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
    -Dredfish-dump-log=enabled \
    -Dexperimental-redfish-dbus-log-subscription=enabled \
"

PACKAGECONFIG:append = " insecure-redfish-expand"

MUTUAL_TLS_PARSING = "UserPrincipalName"
