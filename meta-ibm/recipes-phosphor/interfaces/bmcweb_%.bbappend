EXTRA_OEMESON:append = " \
    -Dibm-management-console=enabled \
    -Dredfish-oem-manager-fan-data=disabled \
    -Dinsecure-enable-redfish-query=enabled \
    -Dhttp-body-limit=400 \
"
PACKAGECONFIG:append = " \
    redfish-dbus-log \
    redfish-dump-log \
"

PACKAGECONFIG:remove = " \
    redfish-bmc-journal \
"

EXTRA_OEMESON:append:p10bmc = " \
    -Dvm-websocket=disabled \
    -Dhypervisor-computer-system=enabled \
"

EXTRA_OEMESON:append:sbp1 = " \
    -Dredfish-updateservice-use-dbus=disabled \
"

PACKAGECONFIG:remove:p10bmc = " \
    kvm \
    mutual-tls-auth \
"

# Witherspoon doesn't have the space for the both zstd and xz compression
PACKAGECONFIG:remove:witherspoon = " \
    http-zstd \
"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
