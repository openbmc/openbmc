SUMMARY = "OpenBMC for Inventec - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-fans \
        ${PN}-flash \
        ${PN}-system \
        ${PN}-ipmi-oem \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"
PROVIDES += "virtual/obmc-ipmi-oem"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-ipmi-oem += "virtual-obmc-ipmi-oem"

SUMMARY:${PN}-chassis = "Inventec Chassis"
RDEPENDS:${PN}-chassis = " \
        x86-power-control \
        obmc-host-failure-reboots \
        "

SUMMARY:${PN}-fans = "Inventec Fans"
RDEPENDS:${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY:${PN}-flash = "Inventec Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-ipmi-blobs \
        phosphor-ipmi-flash \
        "

SUMMARY:${PN}-system = "Inventec System"
RDEPENDS:${PN}-system = " \
        bmcweb \
        entity-manager \
        dbus-sensors \
        webui-vue \
        phosphor-snmp \
        phosphor-sel-logger \
        phosphor-gpio-monitor \
        phosphor-gpio-monitor-monitor \
        vlan \
        "
