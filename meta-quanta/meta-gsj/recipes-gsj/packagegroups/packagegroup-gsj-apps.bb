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

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"

SUMMARY_${PN}-chassis = "GSJ Chassis"
RDEPENDS_${PN}-chassis = ""

SUMMARY_${PN}-fans = "GSJ Fans"
RDEPENDS_${PN}-fans = ""

SUMMARY_${PN}-flash = "GSJ Flash"
RDEPENDS_${PN}-flash = " \
        phosphor-ipmi-flash \
        "
