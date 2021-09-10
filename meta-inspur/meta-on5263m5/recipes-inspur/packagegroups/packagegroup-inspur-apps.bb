SUMMARY = "OpenBMC for Inspur - Applications"
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

SUMMARY:${PN}-chassis = "Inspur Chassis"
RDEPENDS:${PN}-chassis = " \
        obmc-control-chassis \
        obmc-op-control-power \
        obmc-host-failure-reboots \
        "

SUMMARY:${PN}-fans = "Inspur Fans"
RDEPENDS:${PN}-fans = ""

SUMMARY:${PN}-flash = "Inspur Flash"
RDEPENDS:${PN}-flash = " \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-control-bmc \
        "

SUMMARY:${PN}-system = "Inspur System"
RDEPENDS:${PN}-system = " \
        bmcweb \
        entity-manager \
        webui-vue \
        "
