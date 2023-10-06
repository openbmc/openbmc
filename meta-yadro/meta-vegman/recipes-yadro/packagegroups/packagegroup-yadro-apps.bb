SUMMARY = "OpenBMC for Yadro - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-chassis \
    ${PN}-fans \
    ${PN}-flash \
    ${PN}-system \
    ${PN}-interface \
    ${PN}-cli \
"

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "Chassis power control"
RDEPENDS:${PN}-chassis = " \
    phosphor-post-code-manager \
    phosphor-host-postd \
"

SUMMARY:${PN}-fans = "Fan control"
RDEPENDS:${PN}-fans = " \
"

SUMMARY:${PN}-flash = "Flash/firmware-related tools"
RDEPENDS:${PN}-flash = " \
"

SUMMARY:${PN}-system = "System software"
RDEPENDS:${PN}-system = " \
    vegman-fru-handler \
    fru-device \
    entity-manager \
    smbios-mdr \
    ${PN}-interface \
    ${PN}-cli \
"

SUMMARY:${PN}-interface = "Interfaces"
RDEPENDS:${PN}-interface = " \
    phosphor-ipmi-ipmb \
"

SUMMARY:${PN}-cli = "CLI utils"
RDEPENDS:${PN}-cli = " \
    ipmitool \
    obmc-yadro-cli \
    obmc-yadro-lssensors \
    obmc-yadro-lsinventory \
    obmc-yadro-netconfig \
"
