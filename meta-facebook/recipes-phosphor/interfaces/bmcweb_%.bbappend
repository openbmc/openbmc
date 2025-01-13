EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
    -Dmeta-tls-common-name-parsing=enabled \
    -Dredfish-dump-log=enabled \
    -Dexperimental-redfish-dbus-log-subscription=enabled \
"

PACKAGECONFIG:append = " insecure-redfish-expand"

MUTUAL_TLS_PARSING="Meta"
