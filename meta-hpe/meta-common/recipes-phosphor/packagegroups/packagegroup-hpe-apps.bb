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

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "HPE Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-phosphor-buttons-signals \
        obmc-phosphor-buttons-handler \
        obmc-op-control-power \
        obmc-host-failure-reboots \
        "

SUMMARY_${PN}-fans = "HPE Fans"
RDEPENDS_${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY_${PN}-flash = "HPE Flash"
RDEPENDS_${PN}-flash = " \
        obmc-control-bmc \
        "

SUMMARY_${PN}-system = "HPE System"
RDEPENDS_${PN}-system = " \
        bmcweb \
        phosphor-webui \
        phosphor-ipmi-ipmb \
        dbus-sensors \
        "
