SUMMARY = "OpenBMC for Intel AVCRP - Applications"

inherit packagegroup

PACKAGES = " \
    ${PN}-chassis \
    ${PN}-system \
"

RDEPENDS:${PN} = " \
    ${PN}-chassis \
    ${PN}-system \
"

RPROVIDES:${PN}-chassis = "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-system = "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "OpenBMC Chassis applications"
RDEPENDS:${PN}-chassis = " \
    x86-power-control \
    "

SUMMARY:${PN}-system = "OpenBMC system"
RDEPENDS:${PN}-system = " \
    platform-init \
    "
