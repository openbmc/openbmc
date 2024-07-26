# Temporarily enable redfish aggregation until Redfish Client is
# written and supported.
EXTRA_OEMESON:append = "\
    -Dredfish-aggregation=enabled \
"
