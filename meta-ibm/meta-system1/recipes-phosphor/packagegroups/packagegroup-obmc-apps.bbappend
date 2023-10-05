PACKAGES += " \
    ${PN}-system \
    ${PN}-chassis \
    "
PROVIDES += " \
    virtual/obmc-system-mgmt \
    virtual/obmc-chassis-mgmt \
    "

RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"

SUMMARY:${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS:${PN}-chassis = " \
        phosphor-power-control \
        phosphor-skeleton-control-power \
"

RDEPENDS:${PN}-inventory:append = " entity-manager"
