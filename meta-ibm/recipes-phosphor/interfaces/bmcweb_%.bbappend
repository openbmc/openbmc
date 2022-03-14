EXTRA_OEMESON:append = " \
    -Dinsecure-tftp-update=enabled \
    -Dibm-management-console=enabled \
    -Dredfish-new-powersubsystem-thermalsubsystem=enabled \
    -Dredfish-dump-log=enabled \
"

inherit obmc-phosphor-discovery-service

EXTRA_OEMESON:append:p10bmc = " -Dmutual-tls-auth=disabled"

EXTRA_OEMESON:append:witherspoon-tacoma = "-Dmutual-tls-auth=disabled"

REGISTERED_SERVICES:${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES:${PN} += "obmc_rest:tcp:443:"
