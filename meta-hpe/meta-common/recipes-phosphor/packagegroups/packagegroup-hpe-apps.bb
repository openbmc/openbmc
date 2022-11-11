SUMMARY = "OpenBMC for HPE - Applications"
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

SUMMARY:${PN}-chassis = "HPE Chassis"
RDEPENDS:${PN}-chassis = " \
        obmc-phosphor-buttons-signals \
        obmc-phosphor-buttons-handler \
        phosphor-skeleton-control-power \
        obmc-host-failure-reboots \
        "

SUMMARY:${PN}-fans = "HPE Fans"
RDEPENDS:${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY:${PN}-flash = "HPE Flash"
RDEPENDS:${PN}-flash = " \
        "

SUMMARY:${PN}-system = "HPE System"
RDEPENDS:${PN}-system = " \
        bmcweb \
        webui-vue \
        phosphor-ipmi-ipmb \
        dbus-sensors \
        "
