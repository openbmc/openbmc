PACKAGES += " \
    ${PN}-system \
    ${PN}-chassis \
    ${PN}-flash \
    "
PROVIDES += " \
    virtual/obmc-system-mgmt \
    virtual/obmc-chassis-mgmt \
    virtual/obmc-flash-mgmt \
    "

RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"

SUMMARY:${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS:${PN}-chassis = " \
        obmc-phosphor-buttons-signals \
        obmc-phosphor-buttons-handler \
        phosphor-pid-control \
        phosphor-power-control \
        phosphor-power-psu-monitor \
        phosphor-post-code-manager \
        phosphor-host-postd        \
        phosphor-skeleton-control-power \
        phosphor-ipmi-ipmb \
"

SUMMARY:${PN}-flash = "System1 Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-software-manager \
        "

RDEPENDS:${PN}-inventory:append = " entity-manager"
