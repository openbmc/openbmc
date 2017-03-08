SUMMARY = "OpenBMC for OpenPOWER - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

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

SUMMARY_${PN}-chassis = "OpenPOWER Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-button-power \
        obmc-button-reset \
        obmc-control-chassis \
        obmc-hostcheckstop \
        obmc-op-control-power \
        obmc-pcie-detect \
        obmc-watchdog \
        obmc-host-failure-reboots \
        "
#Pull in obmc-fsi on all P9 OpenPOWER systems
RDEPENDS_${PN}-chassis += "${@mf_enabled(d, 'obmc-fsi', 'obmc-fsi')}"

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
