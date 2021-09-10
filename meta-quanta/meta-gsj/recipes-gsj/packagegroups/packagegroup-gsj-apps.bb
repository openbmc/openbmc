SUMMARY = "OpenBMC for GSJ system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-fans \
        ${PN}-flash \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"

SUMMARY:${PN}-chassis = "GSJ Chassis"
RDEPENDS:${PN}-chassis = ""

SUMMARY:${PN}-fans = "GSJ Fans"
RDEPENDS:${PN}-fans = ""

SUMMARY:${PN}-flash = "GSJ Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-ipmi-flash \
        "
