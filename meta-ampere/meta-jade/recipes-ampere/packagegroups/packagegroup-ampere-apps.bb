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

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"

SUMMARY:${PN}-chassis = "Ampere Chassis"
RDEPENDS:${PN}-chassis = " \
        obmc-phosphor-buttons-signals \
        obmc-phosphor-buttons-handler \
        phosphor-skeleton-control-power \
        ampere-hostctrl \
        phosphor-hostlogger \
        phosphor-sel-logger \
        phosphor-logging \
        ac01-boot-progress \
        phosphor-post-code-manager \
        phosphor-host-postd \
        "

SUMMARY:${PN}-system = "Ampere System"
RDEPENDS:${PN}-system = " \
        smbios-mdr \
        "

SUMMARY:${PN}-flash = "Ampere Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-software-manager \
        "

SUMMARY:${PN}-fans = "Ampere Fans"
RDEPENDS:${PN}-fans = " \
        "
