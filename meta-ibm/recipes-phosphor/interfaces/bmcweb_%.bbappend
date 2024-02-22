EXTRA_OEMESON:append = " \
    -Dinsecure-tftp-update=enabled \
    -Dibm-management-console=enabled \
    -Dredfish-dump-log=enabled \
    -Dredfish-oem-manager-fan-data=disabled \
    -Dredfish-bmc-journal=disabled \
"

EXTRA_OEMESON:append:p10bmc = " \
    -Dkvm=disabled \
    -Dvm-websocket=disabled \
"
PACKAGECONFIG:remove:p10bmc = "mutual-tls-auth"

EXTRA_OEMESON:append:witherspoon-tacoma = " \
    -Dkvm=disabled \
    -Dvm-websocket=disabled \
"
PACKAGECONFIG:remove:witherspoon-tacoma = "mutual-tls-auth"

EXTRA_OEMESON:append:system1 = " \
     -Dhttp-body-limit=400 \
     -Dredfish-dbus-log=enabled \
"

EXTRA_OEMESON:append:sbp1 = " \
     -Dhttp-body-limit=400 \
     -Dredfish-dbus-log=enabled \
"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
