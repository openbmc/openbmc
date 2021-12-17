SUMMARY = "OpenBMC for Wistron - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-extras \
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

SUMMARY:${PN}-chassis = "Wistron Chassis"
RDEPENDS:${PN}-chassis = ""

SUMMARY:${PN}-fans = "Wistron Fans"
RDEPENDS:${PN}-fans = ""

SUMMARY:${PN}-flash = "Wistron Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-software-manager \
        obmc-control-bmc \
        "


SUMMARY:${PN}-system = "Wistron System"
RDEPENDS:${PN}-system = " \
        entity-manager \
        dbus-sensors \
        bmcweb \
        webui-vue \
        "
