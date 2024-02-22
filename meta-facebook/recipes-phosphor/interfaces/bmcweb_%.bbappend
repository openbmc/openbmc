EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
"

PACKAGECONFIG:append = " insecure-redfish-expand"

MUTUAL_TLS_PARSING="meta"
