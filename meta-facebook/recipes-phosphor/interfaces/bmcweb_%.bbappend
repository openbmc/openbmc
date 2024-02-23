EXTRA_OEMESON:append = "\
    -Dredfish-dbus-log=enabled \
    -Dbmcweb-logging=warning \
"

PACKAGECONFIG:append = " insecure-redfish-expand"

MUTUAL_TLS_PARSING="meta"
