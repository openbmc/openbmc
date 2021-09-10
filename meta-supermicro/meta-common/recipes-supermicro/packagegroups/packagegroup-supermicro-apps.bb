SUMMARY = "OpenBMC for Supermicro - Applications"
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

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "Supermicro Chassis"
RDEPENDS_${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY_${PN}-fans = "Supermicro Fans"
RDEPENDS_${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY_${PN}-flash = "Supermicro Flash"
RDEPENDS_${PN}-flash = " \
        phosphor-software-manager \
        "

SUMMARY_${PN}-system = "Supermicro System"
RDEPENDS_${PN}-system = " \
        bmcweb \
        entity-manager \
        dbus-sensors \
        phosphor-webui \
        ipmitool \
        "
