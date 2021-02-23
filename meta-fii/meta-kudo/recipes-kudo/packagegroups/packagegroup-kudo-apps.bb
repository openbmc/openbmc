SUMMARY = "OpenBMC for KUDO system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-kudo-system \
    ${PN}-kudo-fw \
    "

PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-kudo-system += "virtual-obmc-system-mgmt"
RPROVIDES_${PN}-kudo-fw += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-kudo-system = "KUDO System"
RDEPENDS_${PN}-kudo-system = " \
    ipmitool \
    ethtool \
    memtester \
    loadsvf \
    fii-ipmi-oem \
    kudo-entity-association-map \
    hotswap-power-cycle \
    obmc-console \
    usb-network \
    ncsid \
    gbmc-mac-config \
    kudo-boot \
    "

SUMMARY_${PN}-kudo-fw = "KUDO Firmware"
RDEPENDS_${PN}-kudo-fw = " \
    kudo-fw \
    kudo-bios-update \
    kudo-bmc-update \
    kudo-cpld-update \
    kudo-scp-update \
    "
