SUMMARY = "OpenBMC power control for Ethanol Board"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-fans \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "Ethanol Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-control-chassis \
        "

SUMMARY_${PN}-fans = "Ethanol Fans"
RDEPENDS_${PN}-fans = " \
        obmc-control-fan \
        "

SUMMARY_${PN}-system = "Ethanol System"
RDEPENDS_${PN}-system = " \
        amd-powerctrl \
        phosphor-dbus-interfaces\
        "
