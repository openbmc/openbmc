SUMMARY = "OpenBMC for Ampere - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "Ampere Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-op-control-power \
        ampere-hostctrl \
        phosphor-hostlogger \
        phosphor-sel-logger \
        phosphor-logging \
        "

SUMMARY_${PN}-system = "Ampere System"
RDEPENDS_${PN}-system = " \
        "

SUMMARY_${PN}-flash = "Ampere Flash"
RDEPENDS_${PN}-flash = " \
        ampere-flash-utils \
        "
