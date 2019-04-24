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

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "Inspur Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-control-chassis \
        obmc-op-control-power \
        obmc-host-failure-reboots \
        "

SUMMARY_${PN}-fans = "Inspur Fans"
RDEPENDS_${PN}-fans = ""

SUMMARY_${PN}-flash = "Inspur Flash"
RDEPENDS_${PN}-flash = " \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-control-bmc \
        "

SUMMARY_${PN}-system = "Inspur System"
RDEPENDS_${PN}-system = " \
        bmcweb \
        entity-manager \
        phosphor-webui \
        "
