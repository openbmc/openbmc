SUMMARY = "OpenBMC for ARM Server - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

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
SUMMARY_${PN}-system = "ARM Server"
RDEPENDS_${PN}-system = " \
        obmc-mgr-system \
        "
