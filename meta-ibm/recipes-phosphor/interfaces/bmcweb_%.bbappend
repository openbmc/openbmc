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

PACKAGECONFIG:remove:system1 = " \
    redfish-allow-deprecated-power-thermal \
"

EXTRA_OEMESON:append:sbp1 = " \
    -Dredfish-updateservice-use-dbus=disabled \
"

EXTRA_OEMESON:append:witherspoon-tacoma = " \
    -Dvm-websocket=disabled \
    -Dhypervisor-computer-system=enabled \
"

PACKAGECONFIG:remove:p10bmc = " \
    kvm \
    redfish-allow-deprecated-power-thermal \
    mutual-tls-auth \
"

PACKAGECONFIG:remove:witherspoon-tacoma = " \
    kvm \
    mutual-tls-auth \
"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
