SUMMARY = "OpenBMC for Ufispace - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-chassis \
    ${PN}-fans \
    ${PN}-flash \
    ${PN}-system \
    "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "Ufispace Chassis"
RDEPENDS:${PN}-chassis = " \
    phosphor-skeleton-control-power \
    "

SUMMARY:${PN}-fans = "Ufispace Fans"
RDEPENDS:${PN}-fans = " \
    phosphor-pid-control \
    "

SUMMARY:${PN}-flash = "Ufispace Flash"
RDEPENDS:${PN}-flash = " \
    phosphor-software-manager \
    phosphor-ipmi-blobs \
    "

SUMMARY:${PN}-system = "Ufispace System"
RDEPENDS:${PN}-system = " \
    phosphor-sel-logger \
    phosphor-state-manager \
    usb-network \
    smbios-mdr \
    "
