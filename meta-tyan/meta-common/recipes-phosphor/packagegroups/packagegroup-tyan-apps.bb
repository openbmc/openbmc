SUMMARY = "OpenBMC for TYAN - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "TYAN Chassis"
RDEPENDS:${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY:${PN}-flash = "TYAN Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-software-manager \
        "

SUMMARY:${PN}-system = "TYAN System"
RDEPENDS:${PN}-system = " \
        dbus-sensors \
        phosphor-hostlogger \
        phosphor-sel-logger \
        ipmitool \
        phosphor-post-code-manager \
        phosphor-host-postd \
        "
