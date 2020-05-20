SUMMARY = "OpenBMC for GBS system - Applications"
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

SUMMARY_${PN}-chassis = "GBS Chassis"
RDEPENDS_${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY_${PN}-fans = "GBS Fans"
RDEPENDS_${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY_${PN}-flash = "GBS Flash"
RDEPENDS_${PN}-flash = " \
        phosphor-ipmi-flash \
        gbs-bmc-update \
        gbs-bios-update \
        "
