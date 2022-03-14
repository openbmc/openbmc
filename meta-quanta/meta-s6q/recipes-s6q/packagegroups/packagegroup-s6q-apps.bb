SUMMARY = "OpenBMC for S6Q - Applications"

PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-chassis \
    ${PN}-fans \
    ${PN}-flash \
    ${PN}-system \
    "

PROVIDES += " virtual/obmc-chassis-mgmt"
PROVIDES += " virtual/obmc-fan-mgmt"
PROVIDES += " virtual/obmc-flash-mgmt"
PROVIDES += " virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis = " virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans = " virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash = " virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system = " virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "S6Q Chassis"
RDEPENDS:${PN}-chassis = " \
    x86-power-control \
    "

SUMMARY:${PN}-fans = "S6Q Fans"
RDEPENDS:${PN}-fans = " \
    phosphor-pid-control \
    "

SUMMARY:${PN}-flash = "S6Q Flash"
RDEPENDS:${PN}-flash = " \
    phosphor-software-manager \
    "

SUMMARY_${PN}-system = "S6Q System"
RDEPENDS:${PN}-system = " \
    phosphor-ipmi-ipmb \
    phosphor-hostlogger \
    phosphor-sel-logger \
    ipmitool \
    phosphor-post-code-manager \
    phosphor-host-postd \
    phosphor-watchdog \
    phosphor-virtual-sensor \
    "
