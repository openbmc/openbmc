EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
    -Dmeta-tls-common-name-parsing=enabled \
"

PACKAGECONFIG:append = " insecure-redfish-expand"

MUTUAL_TLS_PARSING="Meta"
