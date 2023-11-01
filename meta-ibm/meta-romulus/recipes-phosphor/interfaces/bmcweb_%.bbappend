# enable bmcweb info by default for easier CI debug
EXTRA_OEMESON:append = " \
    -Dbmcweb-logging=info \
"
