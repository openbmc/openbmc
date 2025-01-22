EXTRA_OEMESON:append = " \
    -Dibm-management-console=enabled \
    -Dredfish-dump-log=enabled \
    -Dredfish-oem-manager-fan-data=disabled \
    -Dredfish-bmc-journal=disabled \
    -Dinsecure-enable-redfish-query=enabled \
    -Dredfish-dbus-log=enabled \
    -Dhttp-body-limit=400 \
"

EXTRA_OEMESON:append:p10bmc = " \
    -Dkvm=disabled \
    -Dvm-websocket=disabled \
    -Dredfish-allow-deprecated-power-thermal=disabled \
    -Dhypervisor-computer-system=enabled \
"

EXTRA_OEMESON:append:system1 = " \
    -Dredfish-allow-deprecated-power-thermal=disabled \
"
EXTRA_OEMESON:append:sbp1 = " \
    -Dredfish-updateservice-use-dbus=disabled \
"

PACKAGECONFIG:remove:p10bmc = "mutual-tls-auth"

EXTRA_OEMESON:append:witherspoon-tacoma = " \
    -Dkvm=disabled \
    -Dvm-websocket=disabled \
    -Dhypervisor-computer-system=enabled \
"
PACKAGECONFIG:remove:witherspoon-tacoma = "mutual-tls-auth"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
