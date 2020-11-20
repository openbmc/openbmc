SUMMARY = "OpenBMC for Ampere - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        ${PN}-system \
        ${PN}-fans \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"

SUMMARY_${PN}-chassis = "Ampere Chassis"
RDEPENDS_${PN}-chassis = " \
	obmc-phosphor-buttons-signals \
	obmc-phosphor-buttons-handler \
	obmc-op-control-power \
        ampere-hostctrl \
        tempevent-log \
        phosphor-hostlogger \
        phosphor-sel-logger \
        phosphor-logging \
        "

SUMMARY_${PN}-flash = "Ampere Flash"
RDEPENDS_${PN}-flash = " \
        ampere-flash-utils \
        "
SUMMARY_${PN}-fans = "Ampere Fans"
RDEPENDS_${PN}-fans = " \
        "

SUMMARY_${PN}-system = "Ampere System"
RDEPENDS_${PN}-system = " \
        ampere-ipmi-oem \
        "
