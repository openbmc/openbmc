SUMMARY = "OpenBMC for OpenPOWER - Applications"
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

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS:${PN}-chassis = " \
        obmc-phosphor-buttons-signals \
        obmc-phosphor-buttons-handler \
        phosphor-skeleton-control-power \
        obmc-host-failure-reboots \
        "
#Pull in obmc-fsi on all P9 OpenPOWER systems
RDEPENDS:${PN}-chassis += "${@bb.utils.contains('MACHINE_FEATURES', 'op-fsi', 'op-fsi', '', d)}"

#Pull in p9-cfam-override on all P9 OpenPOWER systems
RDEPENDS:${PN}-chassis += "${@bb.utils.contains('MACHINE_FEATURES', 'p9-cfam-override', 'p9-cfam-override', '', d)}"

SUMMARY:${PN}-fans = "OpenPOWER Fans"
RDEPENDS:${PN}-fans = " \
        "

SUMMARY:${PN}-flash = "OpenPOWER Flash"

RDEPENDS:${PN}-flash = " \
        openpower-software-manager\
        "

SUMMARY:${PN}-system = "OpenPOWER System"
RDEPENDS:${PN}-system = " \
        pdbg \
        croserver \
        "
