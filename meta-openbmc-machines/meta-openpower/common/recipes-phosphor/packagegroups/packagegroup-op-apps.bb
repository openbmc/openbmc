SUMMARY = "OpenBMC for OpenPOWER - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-sensors \
        ${PN}-chassis \
        ${PN}-fans \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-sensor-mgmt"
PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-sensors += "virtual-obmc-sensor-mgmt"
RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-sensors = "OpenPOWER Sensors"
RDEPENDS_${PN}-sensors = " \
        obmc-hwmon \
        obmc-mgr-sensor \
        "

SUMMARY_${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-button-power \
        obmc-button-reset \
        obmc-control-chassis \
        obmc-hostcheckstop \
        obmc-mgr-inventory \
        obmc-op-control-power \
        obmc-pcie-detect \
        obmc-watchdog \
        obmc-control-led \
        "

SUMMARY_${PN}-fans = "OpenPOWER Fans"
RDEPENDS_${PN}-fans = " \
        obmc-hwmon \
        obmc-control-fan \
        "

SUMMARY_${PN}-flash = "OpenPOWER Flash"
RDEPENDS_${PN}-flash = " \
        obmc-flash-bios \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-op-flasher \
        obmc-control-bmc \
        "

SUMMARY_${PN}-system = "OpenPOWER System"
RDEPENDS_${PN}-system = " \
        obmc-mgr-system \
        obmc-mgr-state \
        "
