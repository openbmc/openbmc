SUMMARY = "OpenBMC for ARM Server - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"

SUMMARY_${PN}-chassis = "ARM Server Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-button-power \
        obmc-control-chassis \
        obmc-op-control-power \
        "
SUMMARY_${PN}-flash = "ARM Server Flash"
RDEPENDS_${PN}-flash = " \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-control-bmc \
        "
