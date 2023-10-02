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

# TODO - use skeleton power implementation until full power
# code for system1 can be brought in
SUMMARY:${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS:${PN}-chassis = " \
        phosphor-skeleton-control-power \
"

RDEPENDS:${PN}-inventory:append = " entity-manager"
