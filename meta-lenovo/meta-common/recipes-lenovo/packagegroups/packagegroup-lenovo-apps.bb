#"Copyright ? 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

SUMMARY = "OpenBMC for Lenovo - Applications"
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

SUMMARY_${PN}-chassis = "Lenovo Chassis"
RDEPENDS_${PN}-chassis = " \
        obmc-control-chassis \
        obmc-op-control-power \
        lenovo-powerctrl \
        "

SUMMARY_${PN}-fans = "Lenovo Fans"
RDEPENDS_${PN}-fans = " \
        obmc-control-fan \
        "

SUMMARY_${PN}-flash = "Lenovo Flash"
RDEPENDS_${PN}-flash = " \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-control-bmc \
        "

SUMMARY_${PN}-system = "Lenovo System"
RDEPENDS_${PN}-system = " \
        ipmitool \
        tree \
        obmc-pydevtools \
        spitools \
        bmcweb \
        entity-manager \
        phosphor-webui \
        google-ipmi-sys \
        phosphor-ipmi-ethstats \
        uart-routing \
        "
