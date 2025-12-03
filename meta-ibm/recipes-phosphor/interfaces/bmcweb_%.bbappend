EXTRA_OEMESON:append = " \
    -Dibm-management-console=enabled \
    -Dinsecure-enable-redfish-query=enabled \
    -Dhttp-body-limit=400 \
    -Dredfish-use-hardcoded-system-location-indicator=disabled \
"
PACKAGECONFIG:append = " \
    redfish-dbus-log \
    redfish-dump-log \
"

PACKAGECONFIG:remove = " \
    redfish-bmc-journal \
    redfish-oem-manager-fan-data \
"

EXTRA_OEMESON:append:ibm-enterprise = " \
    -Dvm-websocket=disabled \
    -Dhypervisor-computer-system=enabled \
"

EXTRA_OEMESON:append:sbp1 = " \
    -Dredfish-updateservice-use-dbus=disabled \
"

PACKAGECONFIG:remove:ibm-enterprise = " \
    kvm \
    mutual-tls-auth \
"

# Witherspoon doesn't have the space for the both zstd and xz compression
PACKAGECONFIG:remove:witherspoon = " \
    http-zstd \
"

PACKAGECONFIG:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'redundant-bmc', 'redundant-bmc', '', d)}"
PACKAGECONFIG[redundant-bmc] = "-Dredfish-aggregation=enabled"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
